package com.tom.deezergame.models.spotify_models

import com.google.gson.annotations.SerializedName

data class TopTracksResponse(
    @SerializedName("tracks") val tracks : List<Track>
)