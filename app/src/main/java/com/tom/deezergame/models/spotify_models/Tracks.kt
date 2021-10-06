package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Tracks(
    @SerializedName("total") val total: Int
)
