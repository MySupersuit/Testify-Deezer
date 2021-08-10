package com.tom.spotifygamev3.models

import com.google.gson.annotations.SerializedName

data class Artists (

    @SerializedName("id") val id : String,
    @SerializedName("name") val name : String,
    @SerializedName("uri") val uri : String
)