package com.tom.spotifygamev3.models.spotify_models

import com.google.gson.annotations.SerializedName
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.database.DatabaseCommonPlaylist
import com.tom.spotifygamev3.database.DatabaseUserPlaylist

data class SimplePlaylist(
    @SerializedName("id") val id: String,
    @SerializedName("images") val images: List<Images>,
    @SerializedName("owner") val owner: PlaylistOwner,
    @SerializedName("name") val name: String
) {
    fun getReversedImages() : List<Images> {
        val reversed = images.reversed()
        if (reversed[0].height == Constants.SMALL_IMAGE_SIZE) {
            return reversed.subList(1,reversed.size-1)
        }
        return reversed
    }
}

fun SimplePlaylist.asDatabaseModel(): DatabaseCommonPlaylist {
    return DatabaseCommonPlaylist(
        id = this.id,
        images = this.images,
        owner = this.owner,
        name = this.name
    )
}

fun List<SimplePlaylist>.asDatabaseModel(): List<DatabaseUserPlaylist> {
    return this.map {
        DatabaseUserPlaylist(
            id = it.id,
            images = it.images,
            owner = it.owner,
            name = it.name
        )
    }

}
