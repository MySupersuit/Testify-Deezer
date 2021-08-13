package com.tom.spotifygamev3.game_utils.playlist_picker

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class PlaylistPickerViewModelFactory(private val application: Application, private val gameType: String) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlaylistPickerViewModel::class.java)) {
            return PlaylistPickerViewModel(application, gameType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}