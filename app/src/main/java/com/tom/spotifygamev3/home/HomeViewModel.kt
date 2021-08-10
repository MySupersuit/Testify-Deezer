package com.tom.spotifygamev3.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.models.Items
import com.tom.spotifygamev3.models.SpotifyPlaylistResponse
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "HomeViewModel"

    private val _navigateToAlbumGame = MutableLiveData<Boolean>()
    val navigateToAlbumGame: LiveData<Boolean>
        get() = _navigateToAlbumGame

    fun onAlbumGameClick() {
        _navigateToAlbumGame.value = true
    }

    fun onNavigateToAlbumGame() {
        _navigateToAlbumGame.value = false
    }


}