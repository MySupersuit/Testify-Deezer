package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class SpotifyPlaylistResponse (
    @SerializedName("href") val href: String,
    @SerializedName("items") val items : List<Items>,
    @SerializedName("next") val next : String
)
