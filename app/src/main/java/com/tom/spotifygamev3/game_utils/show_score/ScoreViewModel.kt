package com.tom.spotifygamev3.game_utils.show_score

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.tom.spotifygamev3.R
import timber.log.Timber
import kotlin.properties.Delegates

class ScoreViewModel(score: String, gameType: Int, application: Application) : AndroidViewModel(
    application
) {
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

    private val _submitScore = MutableLiveData<Int>()
    val submitScore: LiveData<Int>
        get() = _submitScore

    private val _showLeaderboard = MutableLiveData<Boolean>()
    val showLeaderboard: LiveData<Boolean>
        get() = _showLeaderboard

    var submitted : Boolean = false

    init {
        Timber.d("finalScore: $score")
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

    fun submitScore() {
        val parsedScore = score.value?.let { getScoreFromString(it) }
        _submitScore.value = Integer.parseInt(parsedScore ?: "0")
    }

    fun submitScoreFinish() {
        submitted = true
        _submitScore.value = null
    }

    private fun getScoreFromString(string: String) : String {
        if (string.contains("/")) {
            return string.split("/")[0]
        }
        return string
    }

    fun leaderboardClick() {
        _showLeaderboard.value = true
    }

    fun leaderboardClickFinish() {
        _showLeaderboard.value = false
    }
}