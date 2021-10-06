package com.tom.deezergame.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tom.deezergame.models.deezer_models.UserPlaylistData
import com.tom.deezergame.models.spotify_models.Images
import com.tom.deezergame.models.spotify_models.PlaylistOwner
import com.tom.deezergame.models.spotify_models.SimplePlaylist
import timber.log.Timber

private val TAG = "DatabaseEntities"

// Change to DatabaseUserPlaylist
@Entity
data class DatabaseUserPlaylist constructor(
    @PrimaryKey
    val id: String,
    val images: List<Images>,
    val owner: PlaylistOwner,
    val name: String
)

@Entity
data class DatabaseCommonPlaylist constructor(
    @PrimaryKey
    val id: String,
    val images: List<Images>,
    val owner: PlaylistOwner,
    val name: String
)

@Entity
data class DzDatabaseUserPlaylist constructor(
    @PrimaryKey
    val id: Long,
    val title: String,
    val duration: Int,
    val nb_tracks: Int,
    val fans: Int,
    val picture: String,
    val picture_small: String,
    val picture_medium: String,
    val picture_big: String,
    val picture_xl: String,
    val tracklist: String,
)

@JvmName("asDomainModelDzDbUserPlaylist")
fun List<DzDatabaseUserPlaylist>.asDomainModel(): List<UserPlaylistData> {
    return map {
        UserPlaylistData(
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

fun List<DatabaseUserPlaylist>.asDomainModel(): List<SimplePlaylist> {
    Timber.d("getting from database I think")
    return map {
        SimplePlaylist(
            id = it.id,
            images = it.images,
            owner = it.owner,
            name = it.name
        )
    }
}

@JvmName("asDomainModelDatabaseCommonPlaylist")
fun List<DatabaseCommonPlaylist>.asDomainModel(): List<SimplePlaylist> {
    return map {
        SimplePlaylist(
            id = it.id,
            images = it.images,
            owner = it.owner,
            name = it.name
        )
    }
}