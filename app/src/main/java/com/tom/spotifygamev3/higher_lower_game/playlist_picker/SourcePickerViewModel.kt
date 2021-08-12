package com.tom.spotifygamev3.higher_lower_game.playlist_picker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SourcePickerViewModel : ViewModel() {

    private val _navigateToGame = MutableLiveData<Boolean>()
    val navigateToGame : LiveData<Boolean>
        get() = _navigateToGame

    fun onAdvance() {
        _navigateToGame.value = true
    }

    fun onAdvanceComplete() {
        _navigateToGame.value = false
    }
}