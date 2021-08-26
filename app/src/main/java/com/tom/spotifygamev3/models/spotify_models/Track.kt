package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class Track (
    @SerializedName("album") val album : Album,
    @SerializedName("artists") val artists : List<Artists>,
    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("preview_url") val preview_url : String?,
    @SerializedName("uri") val uri : String
)