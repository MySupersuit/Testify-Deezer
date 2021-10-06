package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class UserPlaylistsResponse (
    @SerializedName("items") val playlists: List<SimplePlaylist>
)