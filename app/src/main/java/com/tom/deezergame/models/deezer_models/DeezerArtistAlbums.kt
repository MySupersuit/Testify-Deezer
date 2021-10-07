package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class DeezerArtistAlbums(
    @SerializedName("data") val data : List<ArtistAlbumData>,
    @SerializedName("total") val total : Int,
    @SerializedName("next") val next : String
)
