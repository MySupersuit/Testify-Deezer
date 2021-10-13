package com.tom.deezergame.network.deezer

import com.tom.deezergame.models.deezer_models.*
import com.tom.deezergame.network.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface DeezerApiService {
    @GET("playlist/{playlist_id}/tracks?${NetworkConstants.DZ_PARAMS}")
    suspend fun getPlaylistTracks(
        @Path(value = "playlist_id") playlist_id: String
    ): DeezerTracks

    @GET("user/{user_id}/playlists?${NetworkConstants.DZ_PARAMS_REMOVE_FIRST}")
    suspend fun getUserPlaylists(
        @Path(value = "user_id") user_id: String
    ): DeezerUserPlaylists

    @GET("artist/{artist_id}/albums?${NetworkConstants.DZ_PARAMS}")
    suspend fun getArtistAlbums(
        @Path(value = "artist_id") artist_id: String
    ): DeezerArtistAlbums

    @GET("artist/{artist_id}/top?limit=10")
    suspend fun getArtistTopTracks(
        @Path(value="artist_id") artist_id: String
    ): DeezerArtistTopTracks

    @GET("search/playlist?limit=35")
    suspend fun searchPlaylist(
        @Query(value="q") query: String
    ): DeezerUserPlaylists

    @GET("search?limit=5")
    suspend fun searchTracks(
        @Query(value="q") query: String
    ): DeezerTracks

}