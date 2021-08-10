package com.tom.spotifygamev3.Utils

object Constants {
    const val BASE_URL = "https://api.spotify.com/v1/"
    const val TEST_PLAYLIST_URI = "1ocd7l0Q4L97N3JHNdMUfD"
    const val TRACKS_URL_PARAMS = "fields=href,next,items(track(album(id,images,name,uri,album_type),artists(id,name,uri),id,name,preview_url,uri))&market=IE"
    const val BICEP_URI = "73A3bLnfnz5BoQjb4gNCga"
    const val ALBUM_GAME_NUM_QUESTIONS = 10

    val ALPHANUM_REGEX = "[^A-Za-z0-9 ]".toRegex()
}