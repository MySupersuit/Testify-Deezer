package com.tom.spotifygamev3.models

import com.google.gson.annotations.SerializedName

data class SpotifyArtistAlbumsResponse (
    @SerializedName("items") val items: List<Album>
)