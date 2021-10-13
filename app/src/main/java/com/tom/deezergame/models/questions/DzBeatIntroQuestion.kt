package com.tom.deezergame.models.questions

import com.tom.deezergame.models.deezer_models.TracksData

data class DzBeatIntroQuestion(
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val previewUrl: String,
    val correctTrack: TracksData,
    var questionScore: Int = 0
) {
    val allAnswers: List<String> = (incorrectAnswers + correctAnswer).shuffled()
}
