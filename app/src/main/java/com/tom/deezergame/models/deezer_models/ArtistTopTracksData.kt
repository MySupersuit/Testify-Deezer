package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class ArtistTopTracksData(
    @SerializedName("id") val id : Int,
    @SerializedName("title") val title : String,
    @SerializedName("title_short") val title_short : String,
    @SerializedName("link") val link : String,
    @SerializedName("preview") val preview : String,
    @SerializedName("artist") val artist : Artist,
    @SerializedName("album") val album : Album,
    @SerializedName("type") val type : String
)
