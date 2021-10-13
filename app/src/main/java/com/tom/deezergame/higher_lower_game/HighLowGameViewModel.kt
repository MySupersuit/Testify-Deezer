package com.tom.deezergame.higher_lower_game

import android.app.Application
import androidx.lifecycle.*
import com.tom.deezergame.album_game.SpotifyApiStatus
import com.tom.deezergame.models.questions.DzHighLowQuestion
import com.tom.deezergame.models.deezer_models.TracksData
import com.tom.deezergame.models.lastfm_models.LfmTrack
import com.tom.deezergame.network.ApiClient
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.utils.Utils.regexedString
import com.tom.deezergame.utils.Utils.unaccent
import kotlinx.coroutines.*
import timber.log.Timber

class HighLowGameViewModel(application: Application, playlist_id: String) :
    AndroidViewModel(application) {

    private var apiClient: ApiClient = ApiClient()
    private var dzInitialItems = listOf<TracksData>()

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _nextQuestion = MutableLiveData<DzHighLowQuestion?>()
    val nextQuestion: LiveData<DzHighLowQuestion?>
        get() = _nextQuestion

    private val _currentQuestion = MutableLiveData<DzHighLowQuestion>()
    val currentQuestion: LiveData<DzHighLowQuestion>
        get() = _currentQuestion

    private val _showModal = MutableLiveData<DzHighLowQuestion>()
    val showModal: LiveData<DzHighLowQuestion>
        get() = _showModal

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _numQsLoaded = MutableLiveData<Int>()
    val numQsLoaded: LiveData<Int>
        get() = _numQsLoaded

    private val _loginClick = MutableLiveData<Boolean>()
    val loginClick: LiveData<Boolean>
        get() = _loginClick

    private val _enoughQuestions = MutableLiveData<Boolean>()
    val enoughQuestions: LiveData<Boolean>
        get() = _enoughQuestions

    private val localTracksPlaycount = mutableListOf<LfmTrack>()
    private var dzLocalTracks = listOf<TracksData>()
    private val dzQuestions: MutableList<DzHighLowQuestion> = mutableListOf()

    private var numQuestions = -1
    private var questionIndex = 0

    private var botchedLastFmQs = 0

    init {
        _score.value = 0
        _numQsLoaded.value = 0
        runBlocking {
            _status.value = SpotifyApiStatus.LOADING
            fetchData(playlist_id)
        }
    }

    private fun makeQuestions() {
        for (i in dzLocalTracks.indices step 2) {
            dzQuestions.add(DzHighLowQuestion(dzLocalTracks[i], dzLocalTracks[i + 1]))
        }
    }

    private fun startGame() {
        questionIndex = 0
        setQuestion()
    }

    private fun setQuestion() {
        if (questionIndex + 1 < numQuestions) _nextQuestion.value = dzQuestions[questionIndex + 1]
        else _nextQuestion.value = null
        _currentQuestion.value = dzQuestions[questionIndex]
    }

    fun onAnswerClick(index: Int) {
        val chosenAnswer = when (index) {
            1 -> dzQuestions[questionIndex].track1
            2 -> dzQuestions[questionIndex].track2
            else -> throw IllegalArgumentException("neither answer 1 or 2 was chosen")
        }

        val otherAnswer = when (index) {
            1 -> dzQuestions[questionIndex].track2
            2 -> dzQuestions[questionIndex].track1
            else -> throw IllegalArgumentException("neither answer 1 or 2 was chosen")
        }

        val result = currentQuestion.value
        if (chosenAnswer.playCount > otherAnswer.playCount) {
            _score.value = (_score.value)?.plus(1)
            result?.correct = true
        } else {
            result?.correct = false
        }

        if (result != null) {
            showModal(result)
        }
    }

    fun onNextModalClick() {
        if ((questionIndex + 1) == numQuestions) {
            // Game over
            onGameFinish()
        } else {
            questionIndex++
            setQuestion()
            hideModal()
        }
    }

    private fun showModal(result: DzHighLowQuestion) {
        _showModal.value = result
    }

    private fun hideModal() {
        _showModal.value = null
    }

    private fun onGameFinish() {
        _eventGameFinish.value = true
    }

    fun onGameFinishComplete() {
        _eventGameFinish.value = false
    }

    fun onLoginClick() {
        _loginClick.value = true
    }

    fun onLoginClickFinish() {
        _loginClick.value = false
    }

    fun onEnoughQsHandle() {
        _enoughQuestions.value = true
    }

    private suspend fun fetchData(playlistId: String) {
        viewModelScope.launch {
            Timber.d("fetching data")

            val initialJob = fetchTracks(playlistId)
            initialJob.join()
            Timber.d("initialTracks fetched")
            if (dzInitialItems.isEmpty()) return@launch
            else if (dzInitialItems.size < 25) {
                _enoughQuestions.value = false
                _status.value = SpotifyApiStatus.ERROR
                return@launch
            }

            val shuffled = dzInitialItems.shuffled()

            val jobs = mutableListOf<Job>()
            // get 20 tracks' playcounts
            for (i in 0 until Constants.HIGH_LOW_NUM_QUESTIONS * 2) {
                val track = shuffled[i]
                Timber.d("Fetch count: ${track.artist.name}: ${track.title_short}")
                jobs.add(fetchTrackPlaycount(track.artist.name, track.title_short))
            }
            jobs.forEach {
                it.join()
                _numQsLoaded.value = _numQsLoaded.value?.plus(1)
            }
            Timber.d("localTracksPlaycount size: ${localTracksPlaycount.size}")
            // TODO Handle stuff that doesn't have last fm tracks
            localTracksPlaycount.forEach {
                Timber.d("${it.name} : ${it.playCount}")
            }

            // TODO clean up string manipulation shtuff
            for (i in 0 until Constants.HIGH_LOW_NUM_QUESTIONS * 2 - (botchedLastFmQs + (botchedLastFmQs % 2))) {

                val paranth_regex_local =
                    regexedString(
                        localTracksPlaycount[i].name,
                        listOf(Constants.PARANTHESES_REGEX, Constants.SINGLE_SPACE_REGEX)
                    ).unaccent().trim()
                for (item in shuffled.subList(0, Constants.HIGH_LOW_NUM_QUESTIONS * 2 + 5)) {

                    val paranth_regex_item =
                        regexedString(
                            item.title_short,
                            listOf(Constants.PARANTHESES_REGEX, Constants.SINGLE_SPACE_REGEX)
                        ).unaccent().trim()

                    if (regexedString(
                            item.title_short.unaccent(),
                            listOf(Constants.ALPHANUM_REGEX, Constants.SINGLE_SPACE_REGEX)
                        ).equals(
                            regexedString(
                                localTracksPlaycount[i].name.unaccent(),
                                listOf(Constants.ALPHANUM_REGEX, Constants.SINGLE_SPACE_REGEX)
                            ),
                            ignoreCase = true
                        ) || (paranth_regex_item.equals(paranth_regex_local, ignoreCase = true))
                    ) {
                        item.playCount = localTracksPlaycount[i].playCount
                    }
                }
            }

            dzLocalTracks = shuffled.subList(0, Constants.HIGH_LOW_NUM_QUESTIONS * 2)
            dzLocalTracks.forEach { item ->
                Timber.d("${item.title_short} by ${item.artist.name} has ${item.playCount} plays")
            }
            makeQuestions()
            _nextQuestion.value = dzQuestions[0]
            numQuestions = dzQuestions.size
            Timber.d("$numQuestions questions")
            _status.value = SpotifyApiStatus.DONE
            startGame()

        }
    }

    // gets all (or first 100) tracks from playlist
    private fun fetchTracks(playlist_id: String): Job {
        val job = viewModelScope.launch {
            try {
                val localItems =
                    apiClient.getDeezerApiService(getApplication())
                        .getPlaylistTracks(playlist_id).data
                dzInitialItems = localItems
                for (track in dzInitialItems) {
                    Timber.d("${track.title_short} ${track.album.cover_medium}")
                }
            } catch (e: Exception) {
                _status.value = SpotifyApiStatus.ERROR
                Timber.e(e)
            }
        }
        return job
    }


    // Gets tracks' playcounts from LastFM
    private fun fetchTrackPlaycount(
        artist: String = "Billie Eilish",
        trackName: String = "bad guy"
    ): Job {
        val job = viewModelScope.launch {
            try {
                val track = apiClient.getLastFmApiService(getApplication())
                    .getLastFmTrackInfo(
                        "track.getInfo",
                        Constants.LASTFM_API_KEY,
                        artist,
                        trackName
                    ).track
                Timber.d("track: ${track.name}")
                localTracksPlaycount.add(track)
            } catch (e: Exception) {
                Timber.e("err on $artist - $trackName")
                Timber.e(e.toString())
                botchedLastFmQs++
                Timber.d("botched++")
                // TODO Take out offending artist track name
                // or add to offending tracks dict to ignore later on?
            }
        }
        return job
    }
}