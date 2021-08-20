package com.tom.spotifygamev3.game_utils.playlist_picker

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.album_game.SpotifyApiStatus
import com.tom.spotifygamev3.models.spotify_models.SimplePlaylist
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import okhttp3.internal.toImmutableList

class PlaylistPickerViewModel(application: Application, gameType: Int) :
    AndroidViewModel(application) {

    private val TAG = "PlaylistPickerViewModel"

//    private val playlistRepository = PlaylistRepository(getDatabase(application), getApplication())
//    val userRepoPlaylists = playlistRepository.userPlaylists

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _userPlaylists = MutableLiveData<List<SimplePlaylist>>()
    val userPlaylists: LiveData<List<SimplePlaylist>>
        get() = _userPlaylists

    private val _commonPlaylists = MutableLiveData<List<SimplePlaylist>>()
    val commonPlaylists: LiveData<List<SimplePlaylist>>
        get() = _commonPlaylists

    private val localCommonPlaylists = mutableListOf<SimplePlaylist>()

    private val _navigateToGame = MutableLiveData<String>()
    val navigateToGame: LiveData<String>
        get() = _navigateToGame

    private val _gameType = MutableLiveData<Int>()
    val gameType: LiveData<Int>
        get() = _gameType

//    private val _fabClick = MutableLiveData<Boolean>()
//    val fabClick : LiveData<Boolean>
//        get() = _fabClick

    private val _showUserPlaylists = MutableLiveData<Boolean>()
    val showUserPlaylists : LiveData<Boolean>
        get() = _showUserPlaylists

    private var apiClient: ApiClient = ApiClient()

    init {
        // something like this to observe playlist changes instead of jobs and joins
        // TODO don't forget to stop this observe forever on exiting viewmodel lifecycle
//        userRepoPlaylists.observeForever(Observer {
//            Log.d(TAG, "observe forever")
//            it.forEach { Log.d(TAG, it.name) }
//        })
//        fetchUserPlaylistsRepo()
        _showUserPlaylists.value = true
        _gameType.value = gameType
        runBlocking {
            _status.value = SpotifyApiStatus.LOADING
            fetchData()
        }
    }

    // Trying caching

    // just use this as fetchUserPlaylists()
    // do this in splash screen? have to be after login
//    private fun fetchUserPlaylistsRepo(): Job {
//        val job = viewModelScope.launch {
//            try {
//                playlistRepository.refreshUserPlaylists()
//            } catch (networkError: IOException) {
//                Log.e(TAG, networkError.toString())
//                _status.value = SpotifyApiStatus.ERROR
//            }
//        }
//        return job
//    }

    private suspend fun fetchData() {
        viewModelScope.launch {
            Log.d(TAG, "Fetching data")

            val job = fetchUserPlaylists()
//            val job = fetchUserPlaylistsRepo()
            job.join()

            Log.d(TAG, "playlists fetched")

            for (i in Constants.COMMON_PLAYLISTS) {
                val job1 = fetchCommonPlaylists(i)
                job1.join()
            }
            _commonPlaylists.value = localCommonPlaylists.toImmutableList()

            _status.value = SpotifyApiStatus.DONE
        }
    }

    private fun fetchUserPlaylists(): Job {
        val job = viewModelScope.launch {
            try {
                _userPlaylists.value =
                    apiClient.getApiService(getApplication()).getUserPlaylists().playlists
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
    }

    // TODO cache from start
    private fun fetchCommonPlaylists(playlistId: String): Job {
        val job = viewModelScope.launch {
            try {
                localCommonPlaylists.add(
                    apiClient.getApiService(getApplication()).getPlaylist(playlistId)
                )
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                _status.value = SpotifyApiStatus.ERROR
            }
        }
        return job
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