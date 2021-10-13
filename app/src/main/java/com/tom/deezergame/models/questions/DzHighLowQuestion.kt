package com.tom.deezergame.models.questions

import com.tom.deezergame.models.deezer_models.TracksData

data class DzHighLowQuestion(
    val track1: TracksData,
    val track2: TracksData,
    var correct: Boolean? = null
)
