package com.tom.spotifygamev3.game_utils.playlist_picker

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.album_game.SpotifyApiStatus
import com.tom.spotifygamev3.database.getDatabase
import com.tom.spotifygamev3.models.spotify_models.SimplePlaylist
import com.tom.spotifygamev3.repository.PlaylistRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.IOException

class PlaylistPickerViewModel(application: Application, gameType: Int) :
    AndroidViewModel(application) {

    private val TAG = "PlaylistPickerViewModel"

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private var commonPlaylistFetchStatus = false

    private val playlistRepository = PlaylistRepository(getDatabase(application), getApplication())

    val userRepoPlaylists = playlistRepository.userPlaylists
    val commonRepoPlaylists = playlistRepository.commonPlaylists

    private val _userPlaylists = MutableLiveData<List<SimplePlaylist>>()
    val userPlaylists: LiveData<List<SimplePlaylist>>
        get() = _userPlaylists

    private val _commonPlaylists = MutableLiveData<List<SimplePlaylist>>()
    val commonPlaylists: LiveData<List<SimplePlaylist>>
        get() = _commonPlaylists

    private val _navigateToGame = MutableLiveData<String>()
    val navigateToGame: LiveData<String>
        get() = _navigateToGame

    private val _gameType = MutableLiveData<Int>()
    val gameType: LiveData<Int>
        get() = _gameType

    private val _finishDataFetch = MutableLiveData<Boolean>()
    val finishDataFetch : LiveData<Boolean>
        get() = _finishDataFetch

    private val _showUserPlaylists = MutableLiveData<Boolean>()
    val showUserPlaylists: LiveData<Boolean>
        get() = _showUserPlaylists

    private lateinit var userObserver: Observer<List<SimplePlaylist>>
    private lateinit var commonObserver: Observer<List<SimplePlaylist>>

    init {
        Log.d(TAG, "here first")
        _status.value = SpotifyApiStatus.LOADING
        commonPlaylistFetchStatus = false
        _showUserPlaylists.value = true
        _gameType.value = gameType
        runBlocking {
//            _status.value = SpotifyApiStatus.LOADING
            fetchData()
            setUpForeverObservers()
        }
    }

    fun forceRefresh() {
//        _status.value = SpotifyApiStatus.LOADING
        commonPlaylistFetchStatus = false
        runBlocking {
            fetchData()
            setUpForeverObservers()
        }
    }

    private fun setUpForeverObservers() {
        setUpUserObserver()
    }

    private fun setUpUserObserver() {
        userObserver = Observer {
            Log.d(TAG, "userplaylistobserver")
            if (userRepoPlaylists.value != null) {
                _userPlaylists.value = userRepoPlaylists.value
                setUpCommonObserver()
            }
        }
        userRepoPlaylists.observeForever(userObserver)
    }

    private fun setUpCommonObserver() {
        commonObserver = Observer {
            Log.d(TAG, "common observer")
            if (it.size == Constants.COMMON_PLAYLISTS.size) {
                Log.d(TAG, "have all common playlists")
                _commonPlaylists.value = commonRepoPlaylists.value
                finishObserving()
                finishDataFetching()
            } else if (!commonPlaylistFetchStatus) {
                // nothing in cache so fetch all the usual way
                commonPlaylistFetchStatus = true
                Log.d(TAG, "getting in here")
                viewModelScope.launch {
                    for (playlistId in Constants.COMMON_PLAYLISTS) {
                        playlistRepository.addCommonPlaylist(playlistId)
                    }
                }
            }
        }
        commonRepoPlaylists.observeForever(commonObserver)
    }

    private fun fetchData() {
        fetchUserPlaylists()
    }

    private fun fetchUserPlaylists() {
        viewModelScope.launch {
            Log.d(TAG, "fetching user playlists")
            fetchUserPlaylistsRepo()
        }
    }

    // just use this as fetchUserPlaylists()
    // do this in splash screen? have to be after login
    private fun fetchUserPlaylistsRepo(): Job {
        val job = viewModelScope.launch {
            try {
                if (userRepoPlaylists.value.isNullOrEmpty()) playlistRepository.refreshUserPlaylists()
            } catch (networkError: IOException) {
                if (userRepoPlaylists.value.isNullOrEmpty()) {
                    Log.e(TAG, "err here")
                }
                Log.e(TAG, "fetch user playlist $networkError")
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    private fun finishObserving() {
        userRepoPlaylists.removeObserver(userObserver)
        commonRepoPlaylists.removeObserver(commonObserver)
    }

    private fun finishDataFetching() {
        _status.value = SpotifyApiStatus.DONE
        _finishDataFetch.value = true
    }

    fun onFinishAckd() {
        _finishDataFetch.value = false
    }

    fun onPlaylistChosen(id: String) {
        _navigateToGame.value = id
    }

    fun onNavigationToGame() {
        _navigateToGame.value = null
    }

    fun fabClick() {
        _showUserPlaylists.value = _showUserPlaylists.value?.not()
    }


}