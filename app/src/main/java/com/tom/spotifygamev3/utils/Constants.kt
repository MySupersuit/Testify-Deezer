package com.tom.spotifygamev3.utils

object Constants {
    const val BASE_URL = "https://api.spotify.com/v1/"
    const val LASTFM_BASE_URL = "https://ws.audioscrobbler.com/2.0/"
    const val LASTFM_API_KEY = "f504a46e92218226ee4517f47f04d7db"

    const val SPOTIFY_CLIENT_ID = "927975ba561f48788c70a03ead116f5b"
    const val SPOTIFY_REDIRECT_URL = "com.tom.spotifygame://callback"

    const val TEST_PLAYLIST_URI = "1ocd7l0Q4L97N3JHNdMUfD"
    const val TRACKS_URL_PARAMS =
        "fields=href,next,items(track(album(id,images,name,uri,album_type),artists(id,name,uri),id,name,preview_url,uri))"
    const val BICEP_URI = "73A3bLnfnz5BoQjb4gNCga"
    const val ALBUM_GAME_NUM_QUESTIONS = 10
    const val HIGH_LOW_NUM_QUESTIONS = 10
    const val BEAT_INTRO_NUM_QUESTIONS = 10
    const val SMALL_IMAGE_SIZE = 60

    const val TOP_50_IRL = "37i9dQZEVXbKM896FDX8L1"
    const val TODAYS_TOP_HITS = "37i9dQZF1DXcBWIGoYBM5M"
    const val GLOBAL_TOP_50 = "37i9dQZEVXbMDoHDwVN2tF"
    const val RAP_CAVIAR = "37i9dQZF1DX0XUsuxWHRQd"
    const val SONGS_CAR = "37i9dQZF1DWWMOmoXKqHTD"
    const val ALL_OUT_00S = "37i9dQZF1DX4o1oenSJRJd"
    const val ROCK_CLASSICS = "37i9dQZF1DWXRqgorJj26U"

    val COMMON_PLAYLISTS = listOf(
        TOP_50_IRL,
        TODAYS_TOP_HITS,
        GLOBAL_TOP_50,
        RAP_CAVIAR,
        SONGS_CAR,
        ALL_OUT_00S,
        ROCK_CLASSICS
    )

    const val ALBUM_GAME_TYPE = 0
    const val HIGH_LOW_GAME_TYPE = 1
    const val BEAT_INTRO_GAME_TYPE = 2

    const val USER_PLAYLISTS_URL_PARAMS = "limit=50"
    const val SIMPLE_PLAYLIST_PARAMS = "fields=id,images,owner,name"
    const val FULL_PLAYLISTS_PARAMS = "fields=id,images,owner,name,tracks"
    const val TOP_TRACKS_PARAMS = "market=IE"

    val ALPHANUM_REGEX = "[^A-Za-z0-9 ]".toRegex()
    val PARANTHESES_REGEX = """\(([^()]*)\)""".toRegex()
    val SINGLE_SPACE_REGEX = "\\s+".toRegex()

    val UNACCENT_REGEX = "\\p{InCombiningDiacriticalMarks}+".toRegex()
}