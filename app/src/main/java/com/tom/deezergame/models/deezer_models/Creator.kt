package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class Creator(
    @SerializedName("id") val id : Int,
    @SerializedName("name") val name : String,
    @SerializedName("tracklist") val tracklist : String,
    @SerializedName("type") val type : String
)
