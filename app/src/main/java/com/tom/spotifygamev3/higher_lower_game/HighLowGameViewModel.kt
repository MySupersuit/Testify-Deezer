package com.tom.spotifygamev3.higher_lower_game

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.utils.Utils.regexedString
import com.tom.spotifygamev3.utils.Utils.unaccent
import com.tom.spotifygamev3.album_game.SpotifyApiStatus
import com.tom.spotifygamev3.models.HighLowQuestion
import com.tom.spotifygamev3.models.lastfm_models.LfmTrack
import com.tom.spotifygamev3.models.spotify_models.Items
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.*
import java.lang.Exception
import java.lang.IllegalArgumentException

class HighLowGameViewModel(application: Application, playlist_id: String) :
    AndroidViewModel(application) {

    private val TAG = "HighLowGameViewModel"

    private var apiClient: ApiClient = ApiClient()
    private var initialItems = listOf<Items>()

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _nextQuestion = MutableLiveData<HighLowQuestion?>()
    val nextQuestion: LiveData<HighLowQuestion?>
        get() = _nextQuestion

    private val _currentQuestion = MutableLiveData<HighLowQuestion>()
    val currentQuestion: LiveData<HighLowQuestion>
        get() = _currentQuestion

    private val _showModal = MutableLiveData<HighLowQuestion>()
    val showModal: LiveData<HighLowQuestion>
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

    private val localTracksPlaycount = mutableListOf<LfmTrack>()
    private var localTracks = listOf<Items>()
    private val questions: MutableList<HighLowQuestion> = mutableListOf()

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
        for (i in localTracks.indices step 2) {
            questions.add(HighLowQuestion(localTracks[i], localTracks[i + 1]))
        }
    }

    private fun startGame() {
        questionIndex = 0
        setQuestion()
    }

    private fun setQuestion() {
        _currentQuestion.value = questions[questionIndex]
        if (questionIndex + 1 < numQuestions) _nextQuestion.value = questions[questionIndex + 1]
        else _nextQuestion.value = null
    }

    fun onAnswerClick(index: Int) {

        val chosenAnswer = when (index) {
            1 -> questions[questionIndex].track1
            2 -> questions[questionIndex].track2
            else -> throw IllegalArgumentException("neither answer 1 or 2 was chosen")
        }

        val otherAnswer = when (index) {
            1 -> questions[questionIndex].track2
            2 -> questions[questionIndex].track1
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

    private fun showModal(result: HighLowQuestion) {
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

    private suspend fun fetchData(playlistId: String) {
        viewModelScope.launch {
            Log.d(TAG, "fetching data")

            val initialJob = fetchTracks(playlistId)
            initialJob.join()
            Log.d(TAG, "initialTracks fetched")
            if (initialItems.isEmpty()) return@launch

            val shuffled = initialItems.shuffled()

            val jobs = mutableListOf<Job>()
            // get 20 tracks' playcounts
            for (i in 0 until Constants.HIGH_LOW_NUM_QUESTIONS * 2) {
                val track = shuffled[i].track
                Log.d(TAG, track.name)
                jobs.add(fetchTrackPlaycount(track.artists[0].name, track.name))
            }
            jobs.forEach {
                it.join()
                _numQsLoaded.value = _numQsLoaded.value?.plus(1)
            }
            Log.d(TAG, "localTracksPlaycount size: ${localTracksPlaycount.size}")
            // TODO Handle stuff that doesn't have last fm tracks
            localTracksPlaycount.forEach {
                Log.d(TAG, "${it.name} : ${it.playCount}")
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
                            item.track.name,
                            listOf(Constants.PARANTHESES_REGEX, Constants.SINGLE_SPACE_REGEX)
                        ).unaccent().trim()

                    if (regexedString(
                            item.track.name.unaccent(),
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

            localTracks = shuffled.subList(0, Constants.HIGH_LOW_NUM_QUESTIONS * 2)
            localTracks.forEach { item ->
                Log.d(
                    TAG,
                    "${item.track.name} by ${item.track.artists[0].name} has ${item.playCount} plays"
                )
            }
            makeQuestions()
            _nextQuestion.value = questions[0]
            numQuestions = questions.size
            Log.d(TAG, "$numQuestions questions")
            _status.value = SpotifyApiStatus.DONE
            startGame()

        }
    }

    // gets all (or first 100) tracks from playlist
    private fun fetchTracks(playlist_id: String = Constants.TOP_50_IRL): Job {
        val job = viewModelScope.launch {
            try {
                val localItems =
                    apiClient.getApiService(getApplication()).getPlaylistTracks(playlist_id).items
                initialItems = localItems
            } catch (e: Exception) {
                _status.value = SpotifyApiStatus.ERROR
                Log.e(TAG, e.toString())
            }
        }
        return job
    }

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
                Log.d(TAG, "track: ${track.name}")
                localTracksPlaycount.add(track)

            } catch (e: Exception) {
                Log.e(TAG, "err on $artist - $trackName")
                Log.e(TAG, e.toString())
                botchedLastFmQs++
                // TODO Take out offending artist track name
                // or add to offending tracks dict to ignore later on?
            }
        }
        return job
    }
}