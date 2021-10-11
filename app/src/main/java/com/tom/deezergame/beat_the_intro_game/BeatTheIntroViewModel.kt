package com.tom.deezergame.beat_the_intro_game

import android.app.Application
import androidx.lifecycle.*
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.album_game.SpotifyApiStatus
import com.tom.deezergame.models.DzBeatIntroQuestion
import com.tom.deezergame.models.deezer_models.ArtistTopTracksData
import com.tom.deezergame.models.deezer_models.PlaylistTracksData
import com.tom.deezergame.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber

import java.lang.Exception

enum class MediaPlayerStatus { PREPARING, DONE, ERROR }

class BeatTheIntroViewModel(application: Application, playlistId: String) :
    AndroidViewModel(application) {

    private var apiClient: ApiClient = ApiClient()
    private var dzInitialItems = listOf<PlaylistTracksData>()

    private var artistIdToTopTracks: HashMap<String, List<ArtistTopTracksData>> = HashMap()
    private var questions = mutableListOf<DzBeatIntroQuestion>()

    var playerPosition = 0;
    var playerDuration = 0;

    private var questionIndex = -1

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _numWrong = MutableLiveData<Int>()
    val numWrong: LiveData<Int>
        get() = _numWrong

    private val _numTracksLoaded = MutableLiveData<Int>()
    val numTracksLoaded: LiveData<Int>
        get() = _numTracksLoaded

    private val _currentQuestion = MutableLiveData<DzBeatIntroQuestion>()
    val currentQuestion: LiveData<DzBeatIntroQuestion>
        get() = _currentQuestion

    private val _nextQuestion = MutableLiveData<DzBeatIntroQuestion>()
    val nextQuestion: LiveData<DzBeatIntroQuestion>
        get() = _nextQuestion

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private val _showModal = MutableLiveData<DzBeatIntroQuestion>()
    val showModal: LiveData<DzBeatIntroQuestion>
        get() = _showModal

    private val _loginClick = MutableLiveData<Boolean>()
    val loginClick: LiveData<Boolean>
        get() = _loginClick

    init {
        _score.value = 0
        _numTracksLoaded.value = 0
        runBlocking {
            _status.value = SpotifyApiStatus.LOADING
            fetchData(playlistId)
        }
    }

    private suspend fun fetchData(playlistId: String = Constants.TEST_PLAYLIST_URI) {
        viewModelScope.launch {
            Timber.d("fetching data")

            // Fetch base 10 tracks
            val initialJob = fetchPlaylistTracks(playlistId)
            initialJob.join()
            Timber.d("initial fetched. size: ${dzInitialItems.size}")
            if (dzInitialItems.isEmpty()) return@launch

            for (item in dzInitialItems) {
                val track = item
                // Get three other top tracks for that artist
                val artistId = "${track.artist.id}"
                Timber.d("artistId $artistId")

                val job2 = fetchTopTracks(artistId)
                job2.join()

                // just need three other top tracks
                val incorrectAnswers = mutableListOf<ArtistTopTracksData>()
                val otherTopTracks = artistIdToTopTracks[artistId]?.shuffled()
                for (otherTrack in otherTopTracks!!) {
                    if (incorrectAnswers.size == 3) break

                    if (otherTrack.title_short != track.title_short &&
                        !incorrectAnswers.contains(otherTrack)
                    ) {
                        incorrectAnswers.add(otherTrack)
                    } else {
                        Timber.d("${otherTrack.title_short} already in or same as answer")
                    }
                }
                Timber.d("correct track: ${track.title_short}")
                for (incorrect in incorrectAnswers) {
                    Timber.d("incorrect: ${incorrect.title_short}")
                }

                // make question
                makeQuestion(track, incorrectAnswers)
            }
            _nextQuestion.value = questions[0]
            _status.value = SpotifyApiStatus.DONE
            startQuiz()
        }
    }

    private fun startQuiz() {
        Timber.d("starting quiz")
        questionIndex = 0
        setQuestion()
    }

    private fun setQuestion() {
        _currentQuestion.value = questions[questionIndex]
        // for caching / preloading - useful maybe in prepare async mediaplayer
        if (questionIndex + 1 < questions.size) _nextQuestion.value = questions[questionIndex + 1]
        else _nextQuestion.value = null

    }

    private fun makeQuestion(
        correctTrack: PlaylistTracksData,
        otherTracks: List<ArtistTopTracksData>
    ) {
        val incorrectAnswers = otherTracks.map {
            it.title_short
        }
        // Some don't have previews so need to pick 10 tracks that do
        Timber.d("${correctTrack.title_short} preview ${correctTrack.preview}")
        questions.add(
            DzBeatIntroQuestion(
                correctAnswer = correctTrack.title_short,
                incorrectAnswers = incorrectAnswers,
                previewUrl = correctTrack.preview,
                correctTrack = correctTrack
            )
        )
    }

    fun onAnswerClick(answerIndex: Int) {
        val chosenAnswer = questions[questionIndex].allAnswers[answerIndex]
        val correctAnswer = questions[questionIndex].correctAnswer
        val remaining = playerDuration - playerPosition
        val questionScore = (remaining.toDouble() / playerDuration * 1000).toInt()
        Timber.d("score = ${questionScore}")

        val result = currentQuestion.value
        if (chosenAnswer == correctAnswer) {
            _score.value = (_score.value)?.plus(questionScore)
            result?.questionScore = questionScore
        } else {
            _score.value = (_score.value)?.minus(questionScore / 2)
            result?.questionScore = -(questionScore / 2)
        }

        if (result != null) {
            showModal(result)
        } else {
            throw Exception("result null")
        }
    }

    fun onNextModalClick() {
        if ((questionIndex + 1) == questions.size) {
            Timber.d("game finished")
            onGameFinish()
        } else {
            questionIndex++
            setQuestion()
            hideModal()
        }
    }

    private fun showModal(result: DzBeatIntroQuestion) {
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

    private fun fetchPlaylistTracks(playlistId: String): Job {
        val job = viewModelScope.launch {
            try {
                val localItems =
                    apiClient.getDeezerApiService(getApplication())
                        .getPlaylistTracks(playlistId).data
                dzInitialItems = getRandomSubset(localItems)
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return job
    }

    private fun fetchTopTracks(artistId: String): Job {
        val job = viewModelScope.launch {
            try {
                val localItems = apiClient.getDeezerApiService(getApplication())
                    .getArtistTopTracks(artistId).data
                // clean tracks maybe

                artistIdToTopTracks[artistId] = localItems
                _numTracksLoaded.value = (_numTracksLoaded.value)?.plus(1)
            } catch (e: Exception) {
                e.message?.let { Timber.d(it) }
                Timber.e(e.toString())
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    // random subset of items if they have a preview url
    private fun getRandomSubset(items: List<PlaylistTracksData>): List<PlaylistTracksData> {
        val shuffled = items.shuffled()
        val subset = mutableListOf<PlaylistTracksData>()
        val songsSeen = mutableListOf<Int>()
        for (item in shuffled) {
            val songId = item.id

            if (!songsSeen.contains(songId) && item.preview.isNotEmpty()) {
                subset.add(item)
                songsSeen.add(songId)
            }

            if (subset.size == Constants.BEAT_INTRO_NUM_QUESTIONS) {
                return subset
            }
        }
        return subset
    }
}