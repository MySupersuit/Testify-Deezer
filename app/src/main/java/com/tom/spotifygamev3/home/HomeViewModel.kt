package com.tom.spotifygamev3.home

import android.app.Application
import androidx.lifecycle.*

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