package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName

data class TopTracksResponse(
    @SerializedName("tracks") val tracks : List<Track>
)