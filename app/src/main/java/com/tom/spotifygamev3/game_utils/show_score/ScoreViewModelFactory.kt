package com.tom.spotifygamev3.game_utils.show_score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class ScoreViewModelFactory(
    private val finalScore: Int,
    private val numQuestions: Int,
    private val gameType: Int
) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ScoreViewModel::class.java)) {
            return ScoreViewModel(finalScore, numQuestions, gameType) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}