package com.tom.deezergame.models

import com.tom.deezergame.models.deezer_models.PlaylistTracksData

data class DzHighLowQuestion(
    val track1: PlaylistTracksData,
    val track2: PlaylistTracksData,
    var correct: Boolean? = null
)
