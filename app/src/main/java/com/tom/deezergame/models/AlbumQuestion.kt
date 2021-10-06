package com.tom.deezergame.models

import com.tom.deezergame.models.spotify_models.Images

data class AlbumQuestion(
    val images: List<Images>,
    val correctAnswer: String,
    val incorrectAnswers: List<String>
) {
    val allAnswers :List<String> = (incorrectAnswers + correctAnswer).shuffled()


}