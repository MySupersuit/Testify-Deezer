package com.tom.deezergame.models

import com.tom.deezergame.models.spotify_models.Items

data class HighLowQuestion(
    val track1: Items,
    val track2: Items,
    var correct: Boolean? = null
)