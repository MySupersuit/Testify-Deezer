package com.tom.deezergame.network.deezer

import com.tom.deezergame.models.deezer_models.DeezerPlaylistTracks
import com.tom.deezergame.models.deezer_models.DeezerUserPlaylists
import com.tom.deezergame.network.NetworkConstants
import retrofit2.http.GET
import retrofit2.http.Path

interface DeezerApiService {
    @GET("playlists/{playlist_id}/tracks?${NetworkConstants.DZ_PARAMS}")
    suspend fun getPlaylistTracks(
        @Path(value = "playlist_id") playlist_id: String
    ): DeezerPlaylistTracks

    @GET("user/{user_id}/playlists?${NetworkConstants.DZ_PARAMS}")
    suspend fun getUserPlaylists(
        @Path(value = "user_id") user_id: String
    ): DeezerUserPlaylists
}