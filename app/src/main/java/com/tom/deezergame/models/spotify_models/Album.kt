package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Album (

    @SerializedName("id") val id : String,
    @SerializedName("images") val images : List<Images>,
    @SerializedName("name") val name : String,
    @SerializedName("uri") val uri : String,
    @SerializedName("album_type") val album_type : String
)