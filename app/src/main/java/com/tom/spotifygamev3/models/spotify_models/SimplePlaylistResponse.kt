package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class SimplePlaylistResponse(
    @SerializedName("name") val name : String
)
