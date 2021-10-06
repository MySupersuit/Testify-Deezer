package com.tom.deezergame.album_game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AlbumGameViewModelFactory(private val application: Application, private val playlist_id: String) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumGameViewModel::class.java)) {
            return AlbumGameViewModel(application, playlist_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}