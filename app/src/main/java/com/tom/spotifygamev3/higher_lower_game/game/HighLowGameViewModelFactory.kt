package com.tom.spotifygamev3.higher_lower_game.game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class HighLowGameViewModelFactory(
    private val application: Application,
    private val playlist_id: String
) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HighLowGameViewModel::class.java)) {
            return HighLowGameViewModel(application, playlist_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}