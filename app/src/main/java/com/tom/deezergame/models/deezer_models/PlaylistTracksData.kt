package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class PlaylistTracksData(
    @SerializedName("id") val id : Int,
    @SerializedName("readable") val readable : Boolean,
    @SerializedName("title") val title : String,
    @SerializedName("title_short") val title_short : String,
    @SerializedName("title_version") val title_version : String,
    @SerializedName("link") val link : String,
    @SerializedName("duration") val duration : Int,
    @SerializedName("rank") val rank : Int,
    @SerializedName("preview") val preview : String,
    @SerializedName("artist") val artist : Artist,
    @SerializedName("album") val album : Album,
)
