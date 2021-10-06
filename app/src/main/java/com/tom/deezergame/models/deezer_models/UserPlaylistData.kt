package com.tom.deezergame.models.deezer_models

import com.google.gson.annotations.SerializedName
import com.tom.deezergame.database.DzDatabaseUserPlaylist

data class UserPlaylistData(
    @SerializedName("id") val id: Long,
    @SerializedName("title") val title: String,
    @SerializedName("duration") val duration: Int,
    @SerializedName("nb_tracks") val nb_tracks: Int,
    @SerializedName("fans") val fans: Int,
    @SerializedName("picture") val picture: String,
    @SerializedName("picture_small") val picture_small: String,
    @SerializedName("picture_medium") val picture_medium: String,
    @SerializedName("picture_big") val picture_big: String,
    @SerializedName("picture_xl") val picture_xl: String,
    @SerializedName("tracklist") val tracklist: String,
) {
    fun getImages(): List<String> {
        return listOf(picture_medium, picture_big, picture_small, picture_xl)
    }
}

fun List<UserPlaylistData>.asDatabaseModel(): List<DzDatabaseUserPlaylist> {
    return this.map {
        DzDatabaseUserPlaylist(
            id = it.id,
            title = it.title,
            nb_tracks = it.nb_tracks,
            fans = it.fans,
            picture = it.picture,
            picture_small = it.picture_small,
            picture_medium = it.picture_medium,
            picture_big = it.picture_big,
            picture_xl = it.picture_xl,
            tracklist = it.tracklist,
            duration = it.duration
        )
    }
}

//@SerializedName("id") val id: Long,
//@SerializedName("title") val title: String,
//@SerializedName("duration") val duration: Int,
//@Transient val public: Boolean,
//@Transient val is_loved_track: Boolean,
//@Transient val collaborative: Boolean,
//@SerializedName("nb_tracks") val nb_tracks: Int,
//@SerializedName("fans") val fans: Int,
//@SerializedName("link") val link: String,
//@SerializedName("picture") val picture: String,
//@SerializedName("picture_small") val picture_small: String,
//@SerializedName("picture_medium") val picture_medium: String,
//@SerializedName("picture_big") val picture_big: String,
//@SerializedName("picture_xl") val picture_xl: String,
//@Transient val checksum: String,
//@SerializedName("tracklist") val tracklist: String,
//@Transient val creation_date: String,
//@Transient val md5_image: String,
//@Transient val picture_type: String,
//@Transient val time_add: Int,
//@Transient val time_mod: Int,
//@Transient val creator: Creator,
//@Transient val type: String
