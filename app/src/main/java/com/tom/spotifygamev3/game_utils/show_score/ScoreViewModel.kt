package com.tom.spotifygamev3.game_utils.show_score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScoreViewModel(score: String, gameType: Int) : ViewModel() {
    private val TAG = "AlbumGameScoreViewModel"

    private val _score = MutableLiveData<String>()
    val score: LiveData<String>
        get() = _score

    private val _eventPlayAgain = MutableLiveData<Boolean>()
    val eventPlayAgain: LiveData<Boolean>
        get() = _eventPlayAgain

    private val _eventGoHome = MutableLiveData<Boolean>()
    val eventGoHome: LiveData<Boolean>
        get() = _eventGoHome

    private val _gameType = MutableLiveData<Int>()
    val gameType: LiveData<Int>
        get() = _gameType

    init {
        Log.d(TAG, "finalScore: $score")
        _score.value = score
        _gameType.value = gameType
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