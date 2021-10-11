package com.tom.deezergame.album_game

import android.app.Application
import androidx.lifecycle.*
import com.tom.deezergame.models.DzAlbumQuestion
import com.tom.deezergame.models.deezer_models.Album
import com.tom.deezergame.models.deezer_models.ArtistAlbumData
import com.tom.deezergame.models.deezer_models.PlaylistTracksData
import com.tom.deezergame.network.ApiClient
import com.tom.deezergame.network.NetworkConstants
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.utils.Utils.cleanedString
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*
import kotlin.collections.HashMap
import kotlin.math.min

enum class SpotifyApiStatus { LOADING, ERROR, DONE }
enum class DeezerApiStatus { LOADING, ERROR, DONE }

class AlbumGameViewModel(application: Application, playlist_id: String) :
    AndroidViewModel(application) {

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _currentQuestion = MutableLiveData<DzAlbumQuestion>()
    val currentQuestion: LiveData<DzAlbumQuestion>
        get() = _currentQuestion

    private val _nextQuestion = MutableLiveData<DzAlbumQuestion>()
    val nextQuestion: LiveData<DzAlbumQuestion>
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
    val loginClick: LiveData<Boolean>
        get() = _loginClick

    private var questionIndex = 0
    private var numQuestions = -1

    private var dzInitialItems = listOf<PlaylistTracksData>()
    private val nullQuestion = DzAlbumQuestion(listOf(), "", listOf())

    private var dzArtistAlbums = listOf<ArtistAlbumData>()
    private var dzArtistIdToOtherAlbums: HashMap<String, MutableList<ArtistAlbumData>> = HashMap()
    private var answerOptions: MutableList<List<String>> = mutableListOf()
    private var questions: MutableList<DzAlbumQuestion> = mutableListOf()

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
            Timber.d("Game finished")
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
        albums: MutableList<ArtistAlbumData>?
    ) {
        if (albums == null) {
            Timber.e("No albums for $artist_id")
            return
        }

        // remove direct duplicate album names
        albums.removeAll {
            it.title == album_name
        }

        // TODO find a better way to remove duplicate album releases

        val toAdd = albums.shuffled().slice(0..min(2, albums.size - 1)).toMutableList()
        toAdd.forEach { Timber.d("toAdd ${it.title}") }
        dzArtistIdToOtherAlbums[artist_id] = toAdd
        Timber.d("=================")
    }

    private suspend fun fetchData(playlist_id: String) {

        viewModelScope.launch {
            Timber.d("Fetching data")

            // Fetch the 10 base tracks
//            val job = fetchPlaylistTracks(playlist_id)
            val job = fetchDzPlaylistTracks(playlist_id)
            job.join()  // wait for it to be finished
            Timber.d("Initial tracks fetched")

            // in dzInitialItemsNow
            if (dzInitialItems.isEmpty()) return@launch

            for (i in dzInitialItems.indices) {
                // Get three different albums from that artist for the other answers

                val artistId = dzInitialItems[i].artist.id.toString()
                val correctAlbumName = dzInitialItems[i].album.title
//                val correctAlbum = dzInitialItems[i].album
                val correctTrack = dzInitialItems[i]

                // get other albums
                Timber.d("Correct: $correctAlbumName")
                val job2 = getDzArtistAlbums(artistId)
                job2.join()
                removeIfPresent(correctAlbumName, artistId, dzArtistIdToOtherAlbums[artistId])
                val albums = dzArtistIdToOtherAlbums[artistId]

                // TODO avoid repeat questions in some way etc
                // TODO prioritise album names rather than singles/eps?

                makeAnswerOptionsV3(correctTrack, albums)
            }
            if (questions.size == 0) _status.value = SpotifyApiStatus.ERROR
            else {
                _nextQuestion.value = questions[0]
                numQuestions = dzInitialItems.size
                _status.value = SpotifyApiStatus.DONE
                startQuiz()
            }
        }
    }

    private fun makeAnswerOptionsV3(
        correctAlbumTrack: PlaylistTracksData,
        otherAlbums: MutableList<ArtistAlbumData>?
    ) {
        val correctAlbumTitle = correctAlbumTrack.album.title
        if (otherAlbums == null) {
            answerOptions.add(
                listOf(
                    correctAlbumTitle,
                    correctAlbumTitle,
                    correctAlbumTitle,
                    correctAlbumTitle
                )
            )
            return
        }

        val incorrectAnswers = otherAlbums.map { album ->
            album.title
        }
        val imagesUrls = correctAlbumTrack.getImages()
        for (url in imagesUrls) Timber.d("url: $url")
        val question =
            DzAlbumQuestion(correctAlbumTrack.getImages(), correctAlbumTitle, incorrectAnswers)
        questions.add(question)
    }

    // add offset parameter to get random songs from given playlist each time
    // based on playlist length and so on

    private fun fetchDzPlaylistTracks(playlist_id: String = NetworkConstants.DZ_POP_ALLSTARS): Job {
        val job = viewModelScope.launch {
            try {
                // still have next + total fields if needed
                val localItems = apiClient.getDeezerApiService(getApplication())
                    .getPlaylistTracks(playlist_id).data
                dzInitialItems = getRandomSubset(localItems)
                for (item in dzInitialItems) {
                    Timber.d("${item.artist.name}: ${item.album.title}")
                }
            } catch (e: Exception) {
                Timber.e(e)
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    private fun getRandomSubset(items: List<PlaylistTracksData>): List<PlaylistTracksData> {
        val shuffled = items.shuffled()
        val subset = mutableListOf<PlaylistTracksData>()
        val artistsSeen = mutableListOf<Int>()
        for (item in shuffled) {
            val artistId = item.artist.id

            if (!artistsSeen.contains(artistId)) {
                subset.add(item)
                artistsSeen.add(artistId)
            }

            if (subset.size == Constants.ALBUM_GAME_NUM_QUESTIONS) {
                return subset
            }
        }
        return subset
    }

    private fun getDzArtistAlbums(artist_id: String = NetworkConstants.DZ_RADIOHEAD): Job {
        val job = viewModelScope.launch {
            try {
                dzArtistAlbums =
                    apiClient.getDeezerApiService(getApplication()).getArtistAlbums(artist_id).data

                dzArtistAlbums.forEach { Timber.d("preDistinct ${it.title}") }
                val subList =
                    dzArtistAlbums.slice(0..min(20, dzArtistAlbums.size - 1)).distinctBy {
                        cleanedString(it.title.toLowerCase(Locale.ROOT))
                    }.toList()
                subList.forEach { Timber.d("postDistinct ${it.title}") }
                _numAlbumsLoaded.value = _numAlbumsLoaded.value?.plus(1)

                dzArtistIdToOtherAlbums[artist_id] = subList.toMutableList()
            } catch (e: Exception) {
                Timber.e(e)
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }
}