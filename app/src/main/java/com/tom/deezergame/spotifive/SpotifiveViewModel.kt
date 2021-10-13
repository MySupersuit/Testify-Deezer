package com.tom.deezergame.spotifive

import android.app.Application
import androidx.lifecycle.*
import com.tom.deezergame.album_game.SpotifyApiStatus
import com.tom.deezergame.models.deezer_models.ArtistTopTracksData
import com.tom.deezergame.models.questions.SpotifiveQuestion
import com.tom.deezergame.network.ApiClient
import kotlinx.coroutines.*
import timber.log.Timber

class SpotifiveViewModel(application: Application, artist_id: String) :
    AndroidViewModel(application) {
    private var apiClient = ApiClient()

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _currentQuestion = MutableLiveData<SpotifiveQuestion>()
    val currentQuestion: LiveData<SpotifiveQuestion>
        get() = _currentQuestion


    private val questions: MutableList<SpotifiveQuestion> = mutableListOf()
    private var initialTopTracks = listOf<ArtistTopTracksData>()
    private var questionIndex = 0

    val searchQuery = MutableLiveData<String>()
    val user = MutableLiveData("")
    val userList: MutableLiveData<ArrayList<String>> = MutableLiveData()

    var inputText = MutableLiveData<String>()

//    val searchResults = MutableLiveData<List<TracksData>>()
    val searchResults = MutableLiveData<List<String>>()
    val valid = MediatorLiveData<Boolean>().apply {
        addSource(user) {
            searchTrack(it)
            value = true
        }
    }

    private fun searchTrack(name: String) {
//        if (name.length < 3) return
        viewModelScope.launch {
            Timber.d("Searching track")
            val job = deezerSearch(name)
        }
    }

    private fun deezerSearch(query: String) : Job {
        val job = viewModelScope.launch {
            try {
                val tracks = apiClient.getDeezerApiService(getApplication())
                    .searchTracks("queen $query")
                for (t in tracks.data) {
                    Timber.d(t.title_short)
                }
                val names = tracks.data.map { it.title_short }
                Timber.d("Search done")
                searchResults.value = names //playlists.data
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
        return job
    }

//    private fun searchUser(name: String) {
//        CoroutineScope(Dispatchers.Main).launch {
//            try {
//                val response =
//            }
//        }
//    }


    init {
//        runBlocking {
//            _status.value = SpotifyApiStatus.LOADING
//            fetchData(artist_id)
//        }
    }


    // fetch top songs for artist
    fun fetchData(artist_id: String) {
        viewModelScope.launch {
            Timber.d("Fetching top 5")

            val initialJob = fetchTop5(artist_id)
            initialJob.join()

            if (initialTopTracks.isEmpty()) return@launch

            makeQuestions()
            Timber.d("${questions.size} numQuestions")
            _status.value = SpotifyApiStatus.DONE
            startGame()
        }
    }

    private fun startGame() {
        questionIndex = 0
        setQuestion()
    }

    private fun setQuestion() {
        _currentQuestion.value = questions[questionIndex]
    }

    private fun makeQuestions() {
        val top5Titles = mutableListOf<String>()
        for (i in 0..5) {
            top5Titles.add(initialTopTracks[i].title_short)
        }
        val artist = initialTopTracks[0].artist
        val artistImages = listOf<String>(
            artist.picture_medium, artist.picture_big, artist.picture_small, artist.picture_xl
        )
        questions.add(SpotifiveQuestion(top5Titles, artistImages))
    }

    private fun fetchTop5(artist_id: String): Job {
        val job = viewModelScope.launch {
            try {
                val localItems = apiClient.getDeezerApiService(getApplication())
                    .getArtistTopTracks(artist_id)
                initialTopTracks = localItems.data
            } catch (e: Exception) {
                _status.value = SpotifyApiStatus.ERROR
                Timber.e(e)
            }
        }
        return job
    }
}