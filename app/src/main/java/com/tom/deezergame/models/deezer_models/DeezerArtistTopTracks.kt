package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName

data class DeezerArtistTopTracks(
    @SerializedName("data") val data : List<ArtistTopTracksData>,
    @SerializedName("total") val total : Int,
    @SerializedName("next") val next : String
)
