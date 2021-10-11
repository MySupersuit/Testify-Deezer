package com.tom.deezergame.home

import android.app.Application
import androidx.lifecycle.*

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "HomeViewModel"

    private val _navigateToAlbumGame = MutableLiveData<Boolean>()
    val navigateToAlbumGame: LiveData<Boolean>
        get() = _navigateToAlbumGame

    private val _navigateToHighLow = MutableLiveData<Boolean>()
    val navigateToHighLow: LiveData<Boolean>
        get() = _navigateToHighLow

    private val _navigateToBeatIntro = MutableLiveData<Boolean>()
    val navigateToBeatIntro: LiveData<Boolean>
        get() = _navigateToBeatIntro

    private val _logOut = MutableLiveData<Boolean>()
    val logOut: LiveData<Boolean>
        get() = _logOut

    private val _infoClick = MutableLiveData<Boolean>()
    val infoClick: LiveData<Boolean>
        get() = _infoClick

    fun onLogOut() {
        _logOut.value = true
    }

    fun onLogOutFinish() {
        _logOut.value = false
    }

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

    fun onBeatIntroClick() {
        _navigateToBeatIntro.value = true
    }

    fun onNavigateToBeatIntro() {
        _navigateToBeatIntro.value = false
    }

    fun onInfoClick() {
        _infoClick.value = true
    }

    fun onInfoEndClick() {
        _infoClick.value = false
    }


}