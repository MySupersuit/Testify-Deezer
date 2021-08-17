package com.tom.spotifygamev3.models

import com.tom.spotifygamev3.models.spotify_models.Images
import com.tom.spotifygamev3.models.spotify_models.Items
import com.tom.spotifygamev3.models.spotify_models.Track

data class HighLowQuestion(
    val track1: Items,
    val track2: Items,
    var correct: Boolean? = null
)