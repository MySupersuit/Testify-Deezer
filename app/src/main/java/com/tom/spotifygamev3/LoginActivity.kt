package com.tom.spotifygamev3

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tom.spotifygamev3.Utils.SessionManager
import com.tom.spotifygamev3.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    private val CLIENT_ID = "927975ba561f48788c70a03ead116f5b"
    private val REDIRECT_URI = "com.tom.spotifygame://callback"
    private val TAG = "Spotify " + LoginActivity::class.simpleName

    private val REQUEST_CODE = 1337
    private val AUTH_TOKEN = "AUTH_TOKEN"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sessionManager = SessionManager(this)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginButton.setOnClickListener {
            login()
        }
    }

    private fun login() {
        val req = AuthorizationRequest.Builder(
            CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI
        )
            .setScopes(arrayOf("streaming"))
            .build()

        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, req)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            val response = AuthorizationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
                    Log.d(TAG, "Login Success. Token: ${response.accessToken}")
                    sessionManager.saveAuthToken(response.accessToken)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                AuthorizationResponse.Type.ERROR -> Log.e(TAG, "Auth error: " + response.error)

                else -> Log.d(TAG, "Auth result: " + response.type)
            }
        }
    }
}