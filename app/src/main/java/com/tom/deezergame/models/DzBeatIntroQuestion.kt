package com.tom.deezergame.models

import com.tom.deezergame.models.deezer_models.PlaylistTracksData

data class DzBeatIntroQuestion(
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val previewUrl: String,
    val correctTrack: PlaylistTracksData,
    var questionScore: Int = 0
) {
    val allAnswers: List<String> = (incorrectAnswers + correctAnswer).shuffled()
}
