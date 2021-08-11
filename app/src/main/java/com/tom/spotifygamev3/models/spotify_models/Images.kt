package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Images (

    @SerializedName("height") val height : Int,
    @SerializedName("url") val url : String,
    @SerializedName("width") val width : Int
)