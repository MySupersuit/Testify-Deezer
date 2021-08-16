package com.tom.spotifygamev3.album_game.score

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import java.lang.IllegalArgumentException

class AlbumScoreViewModelFactory(private val finalScore: Int, private val numQuestions: Int) :
    ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AlbumScoreViewModel::class.java)) {
            return AlbumScoreViewModel(finalScore, numQuestions) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}