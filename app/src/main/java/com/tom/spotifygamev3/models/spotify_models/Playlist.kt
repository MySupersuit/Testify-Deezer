package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Playlist(
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<Images>,
    @SerializedName("owner") val owner: PlaylistOwner,
    @SerializedName("name") val name: String,
    @SerializedName("tracks") val tracks: Tracks
)
