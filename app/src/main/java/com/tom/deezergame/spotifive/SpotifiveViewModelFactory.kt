package com.tom.deezergame.spotifive

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.tom.deezergame.higher_lower_game.HighLowGameViewModel
import java.lang.IllegalArgumentException

class SpotifiveViewModelFactory(
    private val application: Application,
    private val artist_id: String
): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SpotifiveViewModel::class.java)) {
            return SpotifiveViewModel(application, artist_id) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}