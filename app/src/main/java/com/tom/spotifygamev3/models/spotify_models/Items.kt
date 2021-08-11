package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Items (
    @SerializedName("track") val track : Track
)