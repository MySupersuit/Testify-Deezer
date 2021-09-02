package com.tom.spotifygamev3.models

import com.tom.spotifygamev3.models.spotify_models.Track

data class BeatIntroQuestion(
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val previewUrl: String,
    val correctTrack: Track,
    var questionScore: Int = 0
) {
    val allAnswers: List<String> = (incorrectAnswers + correctAnswer).shuffled()
}