package com.tom.deezergame.models.questions

data class DzAlbumQuestion(
    val images: List<String>,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
) {
    val allAnswers : List<String> = (incorrectAnswers + correctAnswer).shuffled()
}
