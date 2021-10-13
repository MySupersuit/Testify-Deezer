package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName
import timber.log.Timber

data class TracksData(
    @SerializedName("id") val id: Int,
    @SerializedName("readable") val readable: Boolean,
    @SerializedName("title") val title: String,
    @SerializedName("title_short") val title_short: String,
    @SerializedName("title_version") val title_version: String,
    @SerializedName("link") val link: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("rank") val rank: Int,
    @SerializedName("preview") val preview: String,
    @SerializedName("artist") val artist: Artist,
    @SerializedName("album") val album: Album,
    var playCount: Int = -1
) {
    fun getImages(): List<String> {
        if (album.cover.isBlank()) {
            return listOf(
                artist.picture_big,
                artist.picture_medium,
                artist.picture_xl,
                artist.picture_small
            )
        } else {
            return listOf(album.cover_big, album.cover_medium, album.cover_xl, album.cover_small)
        }
    }
}
