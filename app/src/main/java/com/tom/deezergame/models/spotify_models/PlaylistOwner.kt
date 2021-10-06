package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class PlaylistOwner(
    @SerializedName("display_name") val display_name : String,
    @SerializedName("id") val id: String
)
