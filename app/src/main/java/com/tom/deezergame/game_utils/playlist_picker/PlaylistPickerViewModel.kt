package com.tom.deezergame.game_utils.playlist_picker

import android.app.Application
import androidx.lifecycle.*
import com.tom.deezergame.album_game.SpotifyApiStatus
import com.tom.deezergame.database.getDatabase
import com.tom.deezergame.database.getDzDatabase
import com.tom.deezergame.models.deezer_models.UserPlaylistData
import com.tom.deezergame.models.spotify_models.SimplePlaylist
import com.tom.deezergame.network.ApiClient
import com.tom.deezergame.network.NetworkConstants
import com.tom.deezergame.repository.PlaylistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import timber.log.Timber
import java.io.IOException

class PlaylistPickerViewModel(application: Application, gameType: Int) :
    AndroidViewModel(application) {

    private val TAG = "PlaylistPickerViewModel"

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private var commonPlaylistFetchStatus = false

    private val playlistRepository =
        PlaylistRepository(getDatabase(application), getDzDatabase(application), getApplication())

    val dzUserRepoPlaylists = playlistRepository.dzUserPlaylists

    private val _dzPlaylists = MutableLiveData<List<UserPlaylistData>>()
    val dzPlaylists: LiveData<List<UserPlaylistData>>
        get() = _dzPlaylists

    private val _navigateToGame = MutableLiveData<Long>()
    val navigateToGame: LiveData<Long>
        get() = _navigateToGame

    private val _gameType = MutableLiveData<Int>()
    val gameType: LiveData<Int>
        get() = _gameType

    private val _finishDataFetch = MutableLiveData<Boolean>()
    val finishDataFetch: LiveData<Boolean>
        get() = _finishDataFetch


    private lateinit var dzObserver: Observer<List<UserPlaylistData>>
    private var apiClient: ApiClient = ApiClient()

    init {
        _status.value = SpotifyApiStatus.LOADING
        commonPlaylistFetchStatus = false
        _gameType.value = gameType
        runBlocking {
            fetchData()
            setUpForeverObservers()
        }
    }

    fun forceRefresh() {
//        _status.value = SpotifyApiStatus.LOADING
        commonPlaylistFetchStatus = false
        runBlocking {
            setUpForeverObservers()
            Timber.d("size prerefresh " + _dzPlaylists.value?.size)
            forceRefreshDb()
        }
    }

    private fun setUpForeverObservers() {
        setUpUserObserver()
    }

    private fun setUpUserObserver() {
        dzObserver = Observer {
            Timber.d("userplaylistobserver")
            if (!dzUserRepoPlaylists.value.isNullOrEmpty()) {
                Timber.d("dzUserPlaylist not empty")
                _dzPlaylists.value = dzUserRepoPlaylists.value
                finishObserving()
                Timber.d("finishing data fetching")
                finishDataFetching()
            } else {
                fetchUserPlaylistsRepo()
            }
        }
        dzUserRepoPlaylists.observeForever(dzObserver)
        Timber.d("adding observer")
    }


    private fun fetchData() {
//        fetchUserPlaylists()
    }

    private fun fetchUserPlaylists() {
        viewModelScope.launch {
            Timber.d("fetching user playlists")
            val job = fetchUserPlaylistsRepo()
            job.join()
            Timber.d("Just done")
            _status.value = SpotifyApiStatus.DONE
        }
    }

    // just use this as fetchUserPlaylists()
    // do this in splash screen? have to be after login
    private fun fetchUserPlaylistsRepo(): Job {
        val job = viewModelScope.launch {
            try {
//                Timber.d("nullempty "+dzUserRepoPlaylists.value.isNullOrEmpty()) // always null or empty
                // TODO look at caching properly
                // hopefully easier with just one source to look at
//                if (dzUserRepoPlaylists.value.isNullOrEmpty()) playlistRepository.refreshUserPlaylists()
                playlistRepository.refreshUserPlaylists()
            } catch (networkError: IOException) {
                if (dzUserRepoPlaylists.value.isNullOrEmpty()) {
                    Timber.e("err here")
                }
                Timber.e("fetch user playlist $networkError")
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    private fun forceRefreshDb() : Job {
        val job = viewModelScope.launch {
            try {
                val playlists = apiClient.getDeezerApiService(getApplication()).getUserPlaylists(NetworkConstants.DZ_USER)
                _dzPlaylists.value = playlists.data
                // TODO
                // maybe take this one outside the forcerefresh
                playlistRepository.forceRefreshPlaylists()
            } catch (networkError: IOException) {
                Timber.e(networkError)
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    private fun finishObserving() {
        dzUserRepoPlaylists.removeObserver(dzObserver)
//        userRepoPlaylists.removeObserver(userObserver)
//        commonRepoPlaylists.removeObserver(commonObserver)
    }

    private fun finishDataFetching() {
        _status.value = SpotifyApiStatus.DONE
        _finishDataFetch.value = true
    }

    fun onFinishAckd() {
        _finishDataFetch.value = false
    }

    fun onPlaylistChosen(id: Long) {
        _navigateToGame.value = id
    }

    fun onNavigationToGame() {
        _navigateToGame.value = null
    }

    fun fabClick() {
//        _showUserPlaylists.value = _showUserPlaylists.value?.not()
    }


}