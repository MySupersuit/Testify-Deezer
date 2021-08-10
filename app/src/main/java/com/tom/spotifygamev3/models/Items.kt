package com.tom.spotifygamev3.models

import com.google.gson.annotations.SerializedName

data class Items (
    @SerializedName("track") val track : Track
)