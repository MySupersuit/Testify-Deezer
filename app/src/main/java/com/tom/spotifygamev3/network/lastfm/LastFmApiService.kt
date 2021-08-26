package com.tom.spotifygamev3.network.lastfm

import com.tom.spotifygamev3.models.lastfm_models.LastFmTrackResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface LastFmApiService {

    @GET(".")
    suspend fun getLastFmTrackInfo(
        @Query(value = "method")  method : String,
        @Query(value = "api_key") api_key : String,
        @Query(value = "artist") artistName: String,
        @Query(value = "track") trackName: String,
        @Query(value = "format") format : String="json"
    ): LastFmTrackResponse


}