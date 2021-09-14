package com.tom.spotifygamev3.network.spotify

import android.content.Context
import com.tom.spotifygamev3.utils.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor{
    private val sessionManager = SessionManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val requestBuilder = chain.request().newBuilder()

        sessionManager.fetchAuthToken()?.let {
            requestBuilder.addHeader("Authorization", "Bearer $it")
//            requestBuilder.addHeader("Authorization", "Bearer bad_auth_code")
        }

        return chain.proceed(requestBuilder.build())
    }
}