package com.tom.spotifygamev3.beat_the_intro_game

import android.app.Application
import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.album_game.SpotifyApiStatus
import com.tom.spotifygamev3.models.BeatIntroQuestion
import com.tom.spotifygamev3.models.spotify_models.Items
import com.tom.spotifygamev3.models.spotify_models.Track
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

enum class MediaPlayerStatus { PREPARING, DONE, ERROR }

class BeatTheIntroViewModel(application: Application, playlistId: String) :
    AndroidViewModel(application) {
    private val TAG = "BeatTheIntroViewModel"

    private var apiClient: ApiClient = ApiClient()
    private var initialPlaylistItems = listOf<Items>()
    private var artistIdToTopTracks: HashMap<String, List<Track>> = HashMap()
    private var questions = mutableListOf<BeatIntroQuestion>()

    var playerPosition = 0;
    var playerDuration = 0;

    private var questionIndex = -1

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    val _mpStatus = MutableLiveData<MediaPlayerStatus>()
    val mpStatus: LiveData<MediaPlayerStatus>
        get() = _mpStatus

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _numWrong = MutableLiveData<Int>()
    val numWrong: LiveData<Int>
        get() = _numWrong

    private val _numTracksLoaded = MutableLiveData<Int>()
    val numTracksLoaded: LiveData<Int>
        get() = _numTracksLoaded

    private val _currentQuestion = MutableLiveData<BeatIntroQuestion>()
    val currentQuestion: LiveData<BeatIntroQuestion>
        get() = _currentQuestion

    private val _nextQuestion = MutableLiveData<BeatIntroQuestion>()
    val nextQuestion: LiveData<BeatIntroQuestion>
        get() = _nextQuestion

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private val _showModal = MutableLiveData<BeatIntroQuestion>()
    val showModal: LiveData<BeatIntroQuestion>
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
            Log.d(TAG, "fetching data")

            // Fetch base 10 tracks
            val initialJob = fetchPlaylistTracks(playlistId)
            initialJob.join()
            Log.d(TAG, "initial fetched. size: ${initialPlaylistItems.size}")
            if (initialPlaylistItems.isEmpty()) return@launch

            for (item in initialPlaylistItems) {
                val track = item.track
                // Get three other top tracks for that artist
                val artistId = track.artists[0].id
                Log.d(TAG, "artistId $artistId")

                val job2 = fetchTopTracks(artistId)
                job2.join()

                // just need three other top tracks
                val incorrectAnswers = mutableListOf<Track>()
                val otherTopTracks = artistIdToTopTracks[artistId]?.shuffled()
                for (otherTrack in otherTopTracks!!) {
                    if (incorrectAnswers.size == 3) break

                    if (otherTrack.name != track.name) {
                        incorrectAnswers.add(otherTrack)
                    } else {
                        Log.d(TAG, "same ${otherTrack.name} == ${track.name}")
                    }
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
        Log.d(TAG, "starting quiz")
        questionIndex = 0
        setQuestion()
    }

    private fun setQuestion() {
        _currentQuestion.value = questions[questionIndex]
        // for caching / preloading - useful maybe in prepare async mediaplayer
        if (questionIndex + 1 < questions.size) _nextQuestion.value = questions[questionIndex + 1]
        else _nextQuestion.value = null

    }

    private fun makeQuestion(correctTrack: Track, otherTracks: List<Track>) {
        val incorrectAnswers = otherTracks.map {
            it.name
        }
        // Some don't have previews so need to pick 10 tracks that do
        Log.d(TAG, "${correctTrack.name} preview ${correctTrack.preview_url}")
        questions.add(
            BeatIntroQuestion(
                correctAnswer = correctTrack.name,
                incorrectAnswers = incorrectAnswers,
                previewUrl = correctTrack.preview_url!!,
                correctTrack = correctTrack
            )
        )
    }

    fun onAnswerClick(answerIndex: Int) {
        val chosenAnswer = questions[questionIndex].allAnswers[answerIndex]
        val correctAnswer = questions[questionIndex].correctAnswer
        val remaining = playerDuration - playerPosition
        val questionScore = (remaining.toDouble() / playerDuration * 1000).toInt()
        Log.d(TAG, "score = ${questionScore.toString()}")

        val result = currentQuestion.value
        if (chosenAnswer == correctAnswer) {
            _score.value = (_score.value)?.plus(questionScore)
            result?.questionScore = questionScore
        } else {
            _score.value = (_score.value)?.minus(questionScore / 2)
            result?.questionScore = -(questionScore/2)
        }


        if (result != null) {
            showModal(result)
        } else {
            throw Exception("result null")
        }
    }

    fun onNextModalClick() {
        if ((questionIndex + 1) == questions.size) {
            Log.d(TAG, "game finished")
            onGameFinish()
        } else {
            questionIndex++
            setQuestion()
            hideModal()
        }
    }

    private fun showModal(result: BeatIntroQuestion) {
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
                    apiClient.getApiService(getApplication()).getPlaylistTracks(playlistId).items
                initialPlaylistItems = getRandomSubset(localItems)
            } catch (e: Exception) {
                _status.value = SpotifyApiStatus.ERROR
                Log.e(TAG, e.toString())
            }
        }
        return job
    }

    private fun fetchTopTracks(artistId: String): Job {
        val job = viewModelScope.launch {
            try {
                val localItems =
                    apiClient.getApiService(getApplication()).getTopTracks(artistId).tracks
                // clean tracks maybe

                artistIdToTopTracks[artistId] = localItems
                _numTracksLoaded.value = (_numTracksLoaded.value)?.plus(1)
            } catch (e: Exception) {
                e.message?.let { Log.d(TAG, it) }
                Log.e(TAG, e.toString())
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    // random subset of items if they have a preview url
    private fun getRandomSubset(items: List<Items>): List<Items> {
        val shuffled = items.shuffled()
        val subset = mutableListOf<Items>()
        val artistsSeen = mutableListOf<String>()
        for (item in shuffled) {
            val artistId = item.track.artists[0].id

            if (!artistsSeen.contains(artistId) && item.track.preview_url != null) {
                subset.add(item)
                artistsSeen.add(item.track.artists[0].name)
            }

            if (subset.size == Constants.BEAT_INTRO_NUM_QUESTIONS) {
                return subset
            }
        }
        return subset
    }
}