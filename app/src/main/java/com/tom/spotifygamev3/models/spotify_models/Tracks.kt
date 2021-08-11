package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Tracks(
    @SerializedName("total") val total: Int
)
