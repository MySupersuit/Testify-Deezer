package com.tom.deezergame.game_utils.show_score

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ScoreViewModelFactory(
    private val application: Application,
    private val score: String,
    private val gameType: Int
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(score, gameType, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}