package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName
import com.tom.spotifygamev3.Utils.Constants

data class Playlist(
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<Images>,
    @SerializedName("owner") val owner: PlaylistOwner,
    @SerializedName("name") val name: String,
    @SerializedName("tracks") val tracks: Tracks
) {
    fun getReversedImages() : List<Images> {
        val reversed = images.reversed()
        if (reversed[0].height == Constants.SMALL_IMAGE_SIZE) {
            return reversed.subList(1,reversed.size-1)
        }
        return reversed
    }
}
