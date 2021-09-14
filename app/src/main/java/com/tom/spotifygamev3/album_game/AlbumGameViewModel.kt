package com.tom.spotifygamev3.album_game

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.utils.Utils.cleanedString
import com.tom.spotifygamev3.models.spotify_models.Album
import com.tom.spotifygamev3.models.AlbumQuestion
import com.tom.spotifygamev3.models.spotify_models.Items
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.*
import kotlin.math.min
import java.util.*
import kotlin.collections.HashMap

enum class SpotifyApiStatus { LOADING, ERROR, DONE }

class AlbumGameViewModel(application: Application, playlist_id: String) :
    AndroidViewModel(application) {

    private val TAG = "AlbumGameViewModel"

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _currentQuestion = MutableLiveData<AlbumQuestion>()
    val currentQuestion: LiveData<AlbumQuestion>
        get() = _currentQuestion

    private val _nextQuestion = MutableLiveData<AlbumQuestion>()
    val nextQuestion: LiveData<AlbumQuestion>
        get() = _nextQuestion

    private val _eventGameFinish = MutableLiveData<Boolean>()
    val eventGameFinish: LiveData<Boolean>
        get() = _eventGameFinish

    private val _numAlbumsLoaded = MutableLiveData<Int>()
    val numAlbumsLoaded: LiveData<Int>
        get() = _numAlbumsLoaded

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private var _numWrong = MutableLiveData<Int>()
    val numWrong: LiveData<Int>
        get() = _numWrong

    private val _loginClick = MutableLiveData<Boolean>()
    val loginClick : LiveData<Boolean>
        get() = _loginClick

    private var questionIndex = 0
    private var numQuestions = -1

    private var initialItems = listOf<Items>()
    private val nullQuestion = AlbumQuestion(listOf(), "", listOf())

    private var artistAlbums = listOf<Album>()
    private var artistIdToOtherAlbums: HashMap<String, MutableList<Album>> = HashMap()
    private var answerOptions: MutableList<List<String>> = mutableListOf()
    private var questions: MutableList<AlbumQuestion> = mutableListOf()

    private var apiClient: ApiClient = ApiClient()

    init {
        _score.value = 0
        _numAlbumsLoaded.value = 0
        runBlocking {
            _status.value = SpotifyApiStatus.LOADING
            fetchData(playlist_id)
        }
    }

    private fun startQuiz() {
//        preloadAlbumArt()
        questionIndex = 0
        setQuestion()
    }

    private fun setQuestion() {
        // If index blah blah blah
        _currentQuestion.value = questions[questionIndex]
        if (questionIndex + 1 < numQuestions) _nextQuestion.value = questions[questionIndex + 1]
        else _nextQuestion.value = nullQuestion
    }

    fun onAnswerClick(answer_index: Int) {
        val chosenAnswer = questions[questionIndex].allAnswers[answer_index]
        val correctAnswer = questions[questionIndex].correctAnswer

        if (chosenAnswer == correctAnswer) {
            _score.value = (_score.value)?.plus(1)
        } else {
            _numWrong.value = (_numWrong.value)?.plus(1)
        }

        // Then move on to next question if there is another question
        if ((questionIndex + 1) == numQuestions) {
            //  Game over
            Log.d(TAG, "Game finished")
            onGameFinish()
        } else {
            questionIndex++
            setQuestion()
        }
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

    private fun removeIfPresent(
        album_name: String,
        artist_id: String,
        albums: MutableList<Album>?
    ) {
        if (albums == null) {
            Log.e(TAG, "No albums for $artist_id")
            return
        }

        // remove direct duplicate album names
        albums.removeAll {
            it.name == album_name
        }

        // TODO find a better way to remove duplicate album releases

        val toAdd = albums.shuffled().slice(0..min(2, albums.size - 1)).toMutableList()
        toAdd.forEach { Log.d(TAG, "toAdd ${it.name}") }
        artistIdToOtherAlbums[artist_id] = toAdd
        Log.d(TAG, "=================")
    }

    private suspend fun fetchData(playlist_id: String) {

        viewModelScope.launch {
            Log.d(TAG, "Fetching data")

            // Fetch the 10 base tracks
            val job = fetchPlaylistTracks(playlist_id)
            job.join()  // wait for it to be finished
            Log.d(TAG, "Initial tracks fetched")

            if (initialItems.isEmpty()) return@launch

            for (i in initialItems.indices) {
                // Get three different albums from that artist for the other answers
                val artistId = initialItems[i].track.artists[0].id
                val correctAlbumName = initialItems[i].track.album.name
                val correctAlbum = initialItems[i].track.album

                // get other albums
                Log.d(TAG, "Correct: $correctAlbumName")
                val job2 = getArtistAlbums(artistId)
                job2.join()

                removeIfPresent(correctAlbumName, artistId, artistIdToOtherAlbums[artistId])
                val albums = artistIdToOtherAlbums[artistId]

                // TODO avoid repeat questions in some way etc
                // TODO prioritise album names rather than singles/eps?

                makeAnswerOptionsV2(correctAlbum, albums)
            }
            if (questions.size == 0) _status.value = SpotifyApiStatus.ERROR
            else {
                _nextQuestion.value = questions[0]
                numQuestions = initialItems.size
                _status.value = SpotifyApiStatus.DONE
                startQuiz()
            }
        }
    }

    private fun makeAnswerOptionsV2(correctAlbum: Album, otherAlbums: MutableList<Album>?) {
        val correctAlbumName = correctAlbum.name
        if (otherAlbums == null) {
            answerOptions.add(
                listOf(
                    correctAlbumName,
                    correctAlbumName,
                    correctAlbumName,
                    correctAlbumName
                )
            )
            return
        }

        val incorrectAnswers = otherAlbums.map { album ->
            album.name
        }

        val question = AlbumQuestion(correctAlbum.images, correctAlbumName, incorrectAnswers)
        questions.add(question)
    }

    // add offset parameter to get random songs from given playlist each time
    // based on playlist length and so on
    private fun fetchPlaylistTracks(playlist_id: String = Constants.TEST_PLAYLIST_URI): Job {
        val job = viewModelScope.launch {
            try {
                val localItems =
                    apiClient.getApiService(getApplication()).getPlaylistTracks(playlist_id).items
                initialItems = getRandomSubset(localItems)

            } catch (e: Exception) {
                Log.e(TAG, "fetchPlaylistTracks $e")
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    // Maybe change to if already seen album rather than artist
    private fun getRandomSubset(items: List<Items>): List<Items> {
        val shuffled = items.shuffled()
        val subset = mutableListOf<Items>()
        val artistsSeen = mutableListOf<String>()
        for (item in shuffled) {
            val artistId = item.track.artists[0].id

            if (!artistsSeen.contains(artistId)) {
                subset.add(item)
                artistsSeen.add(item.track.artists[0].name)
            }

            if (subset.size == Constants.ALBUM_GAME_NUM_QUESTIONS) {
                return subset
            }
        }
        return subset
    }

    private fun getArtistAlbums(artist_id: String = Constants.BICEP_URI): Job {
        val job = viewModelScope.launch {
            try {
                artistAlbums =
                    apiClient.getApiService(getApplication()).getArtistAlbums(artist_id).items

                artistAlbums.forEach { Log.d(TAG, "preDistinct ${it.name}") }
                val subList =
                    artistAlbums.slice(0..min(20, artistAlbums.size - 1)).distinctBy {
//                        regexedString(it.name.toLowerCase(Locale.ROOT), Constants.ALPHANUM_REGEX)
                        cleanedString(it.name.toLowerCase(Locale.ROOT))
                    }.toList()
                subList.forEach { Log.d(TAG, "postDistinct ${it.name}") }
                _numAlbumsLoaded.value = _numAlbumsLoaded.value?.plus(1)

                artistIdToOtherAlbums[artist_id] = subList.toMutableList()

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }
}