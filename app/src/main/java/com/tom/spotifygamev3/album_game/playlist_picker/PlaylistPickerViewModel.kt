package com.tom.spotifygamev3.album_game.playlist_picker

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.album_game.game.SpotifyApiStatus
import com.tom.spotifygamev3.models.spotify_models.Playlist
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlaylistPickerViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "PlaylistPickerViewModel"

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    private val _playlists = MutableLiveData<List<Playlist>>()
        val playlists: LiveData<List<Playlist>>
        get() = _playlists

    private val _navigateToGame = MutableLiveData<String>()
    val navigateToGame : LiveData<String>
        get() = _navigateToGame


//    private var playlists = listOf<Playlist>()

    private var apiClient: ApiClient = ApiClient()

    init {
        runBlocking {
            _status.value = SpotifyApiStatus.LOADING
            fetchData()
        }
    }

    private suspend fun fetchData() {
        viewModelScope.launch {
            Log.d(TAG, "Fetching data")

            val job = fetchUserPlaylists()
            job.join()
            Log.d(TAG, "playlists fetched")

            _status.value = SpotifyApiStatus.DONE

        }
    }

    private fun fetchUserPlaylists() : Job {
        val job = viewModelScope.launch {
            try {
                _playlists.value = apiClient.getApiService(getApplication()).getUserPlaylists().playlists
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
}