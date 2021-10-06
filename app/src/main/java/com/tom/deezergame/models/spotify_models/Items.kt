package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Items (
    @SerializedName("track") val track : Track,
    var playCount : Int = -1
)