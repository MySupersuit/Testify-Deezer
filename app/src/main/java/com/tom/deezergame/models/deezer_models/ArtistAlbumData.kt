package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class ArtistAlbumData(
    @SerializedName("id") val id : Int,
    @SerializedName("title") val title : String,
    @SerializedName("link") val link : String,
    @SerializedName("cover") val cover : String,
    @SerializedName("cover_small") val cover_small : String,
    @SerializedName("cover_medium") val cover_medium : String,
    @SerializedName("cover_big") val cover_big : String,
    @SerializedName("cover_xl") val cover_xl : String,
    @SerializedName("genre_id") val genre_id : Int,
    @SerializedName("fans") val fans : Int,
    @SerializedName("release_date") val release_date : String,
    @SerializedName("tracklist") val tracklist : String,
)
