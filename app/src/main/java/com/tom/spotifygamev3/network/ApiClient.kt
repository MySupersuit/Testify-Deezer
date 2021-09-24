package com.tom.spotifygamev3.network

import android.content.Context
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.network.lastfm.LastFmApiService
import com.tom.spotifygamev3.network.spotify.ApiService
import com.tom.spotifygamev3.network.spotify.AuthInterceptor
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

class ApiClient {
    private lateinit var apiService: ApiService
    private lateinit var lastFmApiService: LastFmApiService

    fun getApiService(context: Context): ApiService {
        if (!::apiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okhttpClient(context))
                .build()

            apiService = retrofit.create(ApiService::class.java)
        }
        return apiService
    }

    
    fun getLastFmApiService(context: Context): LastFmApiService {
        if (!::lastFmApiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(Constants.LASTFM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            lastFmApiService = retrofit.create(LastFmApiService::class.java)
        }
        return lastFmApiService
    }

    private fun okhttpClient(context: Context) : OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}