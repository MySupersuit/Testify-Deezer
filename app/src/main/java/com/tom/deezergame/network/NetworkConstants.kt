package com.tom.deezergame.network

object NetworkConstants {
    const val BASE_URL = "https://api.spotify.com/v1/"
    const val LASTFM_BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    const val LASTFM_API_KEY = "f504a46e92218226ee4517f47f04d7db"
    const val DEEZER_BASE_URL = "https://api.deezer.com/"

    const val DZ_POP_ALLSTARS = "1282483245"
    const val DZ_USER = "4611918582"
    const val DZ_PARAMS_REMOVE_FIRST = "limit=100&index=1" // gets rid of empty loved tracks playlist
    const val DZ_PARAMS = "limit=100"
    const val DZ_RADIOHEAD = "399"
}