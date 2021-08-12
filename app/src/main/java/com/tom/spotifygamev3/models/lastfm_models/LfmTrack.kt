package com.tom.spotifygamev3.models.lastfm_models

import com.google.gson.annotations.SerializedName

data class LfmTrack(
    @SerializedName("name") val name : String,
    @SerializedName("playcount") val playCount : Int
)
