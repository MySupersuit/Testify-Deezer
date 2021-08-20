package com.tom.spotifygamev3.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tom.spotifygamev3.models.spotify_models.Images
import com.tom.spotifygamev3.models.spotify_models.PlaylistOwner

class DataConverter {

    @TypeConverter
    fun fromImagesList(images: List<Images>) : String {
        val gson = Gson()
        val type = object : TypeToken<List<Images>>() {}.type
        return gson.toJson(images, type)
    }

    @TypeConverter
    fun toImagesList(value : String): List<Images> {
        val gson = Gson()
        val type = object : TypeToken<List<Images>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromPlaylistOwner(owner: PlaylistOwner) : String {
        val gson = Gson()
        val type = object : TypeToken<PlaylistOwner>() {}.type
        return gson.toJson(owner, type)
    }

    @TypeConverter
    fun toPlaylistOwner(value: String): PlaylistOwner {
        val gson = Gson()
        val type = object: TypeToken<PlaylistOwner>() {}.type
        return gson.fromJson(value, type)
    }
}