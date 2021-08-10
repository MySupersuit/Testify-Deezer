package com.tom.spotifygamev3.models

data class AlbumQuestion(
    val images: List<Images>,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
) {
    val allAnswers :List<String> = (incorrectAnswers + correctAnswer).shuffled()


}