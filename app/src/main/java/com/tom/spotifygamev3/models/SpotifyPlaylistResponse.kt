package com.tom.spotifygamev3.models

import com.google.gson.annotations.SerializedName

data class SpotifyPlaylistResponse (
    @SerializedName("href") val href: String,
    @SerializedName("items") val items : List<Items>,
    @SerializedName("next") val next : String
)
