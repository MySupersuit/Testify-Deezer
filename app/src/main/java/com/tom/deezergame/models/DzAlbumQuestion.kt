package com.tom.deezergame.models

data class DzAlbumQuestion(
    val images: List<String>,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
) {
    val allAnswers : List<String> = (incorrectAnswers + correctAnswer).shuffled()
}
