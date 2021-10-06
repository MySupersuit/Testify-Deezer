package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Images (

    @SerializedName("height") val height : Int,
    @SerializedName("url") val url : String,
    @SerializedName("width") val width : Int
)