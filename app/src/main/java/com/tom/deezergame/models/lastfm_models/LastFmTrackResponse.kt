package com.tom.deezergame.models.lastfm_models

import com.google.gson.annotations.SerializedName

data class LastFmTrackResponse(
    @SerializedName("track") val track : LfmTrack
)
