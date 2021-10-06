package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class Album(
    @SerializedName("id") val id : Int,
    @SerializedName("title") val title : String,
    @SerializedName("cover") val cover : String,
    @SerializedName("cover_small") val cover_small : String,
    @SerializedName("cover_medium") val cover_medium : String,
    @SerializedName("cover_big") val cover_big : String,
    @SerializedName("cover_xl") val cover_xl : String,
    @SerializedName("md5_image") val md5_image : String,
    @SerializedName("tracklist") val tracklist : String,
    @SerializedName("type") val type : String
)
