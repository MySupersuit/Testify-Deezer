package com.tom.spotifygamev3.models

data class BeatIntroQuestion(
    val correctAnswer: String,
    val incorrectAnswers: List<String>,
    val previewUrl: String
) {
    val allAnswers: List<String> = (incorrectAnswers + correctAnswer).shuffled()
}