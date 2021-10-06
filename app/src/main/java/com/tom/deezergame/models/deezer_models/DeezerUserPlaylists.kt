package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class DeezerUserPlaylists(
    @SerializedName("data") val data: List<UserPlaylistData>,
    @SerializedName("checksum") val checksum: String,
    @SerializedName("total") val total: Int,
    @SerializedName("next") val next: String
)