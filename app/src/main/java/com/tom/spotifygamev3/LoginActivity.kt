package com.tom.spotifygamev3

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.databinding.DataBindingUtil
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tom.spotifygamev3.databinding.ActivityLoginBinding
import com.tom.spotifygamev3.utils.SessionManager

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
            .setScopes(arrayOf("streaming", "playlist-read-private"))
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
                AuthorizationResponse.Type.ERROR -> {
                    Log.e(TAG, "Auth error: " + response.error)
                    val green = ContextCompat.getColor(applicationContext, R.color.spotify_green)
                    val white = ContextCompat.getColor(applicationContext, R.color.spotify_white)
                    val snackbar = Snackbar.make(
                        binding.coordinatorLayout,
                        "Login fail - Check connection",
                        Snackbar.LENGTH_INDEFINITE
                    )
                    snackbar.setAction("OK") {
                    }
                    snackbar.setActionTextColor(white)
                    for (view in snackbar.view.allViews) {
                        view.setBackgroundColor(green)
                    }
                    snackbar.show()
                }
                else -> Log.d(TAG, "Auth result: " + response.type)
            }
        }
    }

}