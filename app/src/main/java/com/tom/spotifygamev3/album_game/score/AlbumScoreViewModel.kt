package com.tom.spotifygamev3.album_game.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class AlbumScoreViewModel(finalScore: Int, numQuestions: Int) : ViewModel() {
    private val TAG = "AlbumGameScoreViewModel"

    private val _score = MutableLiveData<Int>()
    val score: LiveData<Int>
        get() = _score

    private val _numQuestions = MutableLiveData<Int>()
    val numQuestions: LiveData<Int>
        get() = _numQuestions

    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    private val _eventGoHome = MutableLiveData<Boolean>()
    val eventGoHome: LiveData<Boolean>
        get() = _eventGoHome

    init {
        Log.d(TAG, "finalScore: $finalScore")
        _score.value = finalScore
        _numQuestions.value = numQuestions
    }

    fun onPlayAgain() {
        _eventPlayAgain.value = true
    }

    fun onPlayAgainComplete() {
        _eventPlayAgain.value = false
    }

    fun onGoHome() {
        _eventGoHome.value = true
    }

    fun onGoHomeComplete() {
        _eventGoHome.value = false
    }
}