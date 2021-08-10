package com.tom.spotifygamev3.network

import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.models.Album
import com.tom.spotifygamev3.models.Artists
import com.tom.spotifygamev3.models.SpotifyArtistAlbumsResponse
import com.tom.spotifygamev3.models.SpotifyPlaylistResponse
import retrofit2.Call
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
}

//object {
//    val apiService : ApiService by lazy {
//        apiService.
//    }
//}