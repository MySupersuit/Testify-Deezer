package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class UserPlaylistsResponse (
    @SerializedName("items") val playlists: List<SimplePlaylist>
)