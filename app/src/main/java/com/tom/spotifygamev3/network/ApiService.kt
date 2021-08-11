package com.tom.spotifygamev3.network

import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.models.spotify_models.Artists
import com.tom.spotifygamev3.models.spotify_models.SpotifyArtistAlbumsResponse
import com.tom.spotifygamev3.models.spotify_models.SpotifyPlaylistResponse
import com.tom.spotifygamev3.models.spotify_models.UserPlaylistsResponse
import retrofit2.http.GET
import retrofit2.http.Path



interface ApiService {
    @GET("playlists/{playlist_id}/tracks?${Constants.TRACKS_URL_PARAMS}")
    suspend fun getPlaylistTracks(
        @Path(value = "playlist_id") playlist_id: String
    ) : SpotifyPlaylistResponse

    @GET("artists/{artist_id}/related-artists")
    suspend fun getRelatedArtists(
        @Path(value="artist_id") artist_id: String
    ) : List<Artists>

    @GET("artists/{artist_id}/albums")
    suspend fun getArtistAlbums(
        @Path(value="artist_id") artist_id: String
    ) : SpotifyArtistAlbumsResponse

    @GET("me/playlists?${Constants.PLAYLISTS_URL_PARAMS}")
    suspend fun getUserPlaylists() : UserPlaylistsResponse
}

//object {
//    val apiService : ApiService by lazy {
//        apiService.
//    }
//}