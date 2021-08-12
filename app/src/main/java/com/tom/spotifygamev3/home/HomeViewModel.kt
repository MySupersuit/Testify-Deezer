package com.tom.spotifygamev3.home

import android.app.Application
import androidx.lifecycle.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "HomeViewModel"

    private val _navigateToAlbumGame = MutableLiveData<Boolean>()
    val navigateToAlbumGame: LiveData<Boolean>
        get() = _navigateToAlbumGame

    private val _navigateToHighLow = MutableLiveData<Boolean>()
    val navigateToHighLow: LiveData<Boolean>
        get () = _navigateToHighLow

    fun onAlbumGameClick() {
        _navigateToAlbumGame.value = true
    }

    fun onNavigateToAlbumGame() {
        _navigateToAlbumGame.value = false
    }

    fun onHighLowClick() {
        _navigateToHighLow.value = true
    }

    fun onNavigateToHighLow() {
        _navigateToHighLow.value = false
    }


}