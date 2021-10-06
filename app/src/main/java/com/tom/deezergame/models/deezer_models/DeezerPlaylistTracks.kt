package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class DeezerPlaylistTracks(
    @SerializedName("data") val data: List<PlaylistTracksData>,
    @SerializedName("checksum") val checksum: String,
    @SerializedName("total") val total: Int,
    @SerializedName("next") val next: String
)