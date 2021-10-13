package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class DeezerTracks(
    @SerializedName("data") val data: List<TracksData>,
    @SerializedName("checksum") val checksum: String,
    @SerializedName("total") val total: Int,
    @SerializedName("next") val next: String
)