package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class SpotifyArtistAlbumsResponse (
    @SerializedName("items") val items: List<Album>
)