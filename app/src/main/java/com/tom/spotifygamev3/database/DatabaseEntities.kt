package com.tom.spotifygamev3.database

import android.util.Log
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.tom.spotifygamev3.models.spotify_models.Images
import com.tom.spotifygamev3.models.spotify_models.PlaylistOwner
import com.tom.spotifygamev3.models.spotify_models.SimplePlaylist
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