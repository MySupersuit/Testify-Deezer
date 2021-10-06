package com.tom.deezergame.network

import android.content.Context
import com.tom.deezergame.network.deezer.DeezerApiService
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.network.lastfm.LastFmApiService
import com.tom.deezergame.network.spotify.ApiService
import com.tom.deezergame.network.spotify.AuthInterceptor
import retrofit2.Retrofit
import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class ApiClient {
    private lateinit var apiService: ApiService
    private lateinit var lastFmApiService: LastFmApiService
    private lateinit var deezerApiService: DeezerApiService

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
                .baseUrl(NetworkConstants.LASTFM_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            lastFmApiService = retrofit.create(LastFmApiService::class.java)
        }
        return lastFmApiService
    }

    fun getDeezerApiService(context: Context): DeezerApiService {
        if (!::deezerApiService.isInitialized) {
            val retrofit = Retrofit.Builder()
                .baseUrl(NetworkConstants.DEEZER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
                .build()

            deezerApiService = retrofit.create(DeezerApiService::class.java)
        }
        return deezerApiService
    }

    private fun okhttpClient(context: Context): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(context))
            .build()
    }
}