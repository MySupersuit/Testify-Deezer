package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class SpotifyArtistAlbumsResponse (
    @SerializedName("items") val items: List<Album>
)