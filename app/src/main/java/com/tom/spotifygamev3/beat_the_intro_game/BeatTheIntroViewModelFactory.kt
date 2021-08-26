package com.tom.spotifygamev3.beat_the_intro_game

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tom.spotifygamev3.album_game.AlbumGameViewModel
import java.lang.IllegalArgumentException

class BeatTheIntroViewModelFactory(private val application: Application, private val playlist_id: String) :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BeatTheIntroViewModel::class.java)) {
            return BeatTheIntroViewModel(application, playlist_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }


}