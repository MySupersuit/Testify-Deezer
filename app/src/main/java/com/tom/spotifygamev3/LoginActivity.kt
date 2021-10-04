package com.tom.spotifygamev3

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.databinding.DataBindingUtil
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import com.tom.spotifygamev3.databinding.ActivityLoginBinding
import com.tom.spotifygamev3.utils.SessionManager
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    val CLIENT_ID = "927975ba561f48788c70a03ead116f5b"
    val REDIRECT_URI = "com.tom.spotifygame://callback"
    private val TAG = "Spotify " + LoginActivity::class.simpleName

    private val REQUEST_CODE_SPOTIFY = 1337
    private val REQUEST_CODE_GOOGLE = 1338
    private val AUTH_TOKEN = "AUTH_TOKEN"

    private lateinit var binding: ActivityLoginBinding
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sessionManager = SessionManager(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.loginButton.setOnClickListener {
            showLoadingCircle(binding)
            signInSilently()
            loginSpotify()
        }
    }

    override fun onStart() {
        super.onStart()
        val googleAccount = GoogleSignIn.getLastSignedInAccount(this);
    }

    private fun showLoadingCircle(binding: ActivityLoginBinding) {
        binding.main.visibility = View.GONE
        binding.loginLoadingCl.visibility = View.VISIBLE
    }

    private fun hideLoadingCircle(binding: ActivityLoginBinding) {
        binding.main.visibility = View.VISIBLE
        binding.loginLoadingCl.visibility = View.GONE
    }

    private fun signInSilently() {
        val signInOptions = GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        val account = GoogleSignIn.getLastSignedInAccount(this)
        if (account != null) {
            val signedInAccount = account
            Timber.d("logged in with ${signedInAccount.displayName ?: signedInAccount.account?.name}")
        } else { // not signed in before
            val signInClient = GoogleSignIn.getClient(this, signInOptions)
            signInClient
                .silentSignIn()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val signedInAccount = task.result
                    } else {
                        loginGoogle()
                    }
                }
        }
    }

    private fun loginGoogle() {
        Timber.d("logingoogle")
        val signInOptions = GoogleSignInOptions.Builder(
            GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
        )
            .requestIdToken("551033295127-us4lunebnfgpso16edjor3eiir7571u3.apps.googleusercontent.com")
            .requestEmail()
            .requestProfile()
            .build()
        val googleClient = GoogleSignIn.getClient(this, signInOptions)
        val signInIntent = googleClient.signInIntent
        startSignInIntent(signInIntent)
    }

    private fun startSignInIntent(intent: Intent) {
        startActivityForResult(intent, REQUEST_CODE_GOOGLE)
    }

    private fun loginSpotify() {
        val req = AuthorizationRequest.Builder(
            CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI
        )
            .setScopes(arrayOf("playlist-read-private"))
            .build()

//        AuthorizationClient.openLoginInBrowser(this, req);
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE_SPOTIFY, req)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        hideLoadingCircle(binding)

        if (requestCode == REQUEST_CODE_SPOTIFY) {
            val response = AuthorizationClient.getResponse(resultCode, data)

            when (response.type) {
                AuthorizationResponse.Type.TOKEN -> {
//                    Timber.d("Login Success. Token: ${response.accessToken}")
                    Timber.d("Login Success. Token: ${response.accessToken}")
                    sessionManager.saveAuthToken(response.accessToken)

                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                AuthorizationResponse.Type.ERROR -> {
                    Timber.e( "Auth error: " + response.error)
                    showErrorSnackbar()
                }
                else -> Timber.d("Auth result: " + response.type)
            }
        } else if (requestCode == REQUEST_CODE_GOOGLE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                Timber.d("auth with google ${account.displayName}")
            } catch (e: ApiException) {
                Log.w(TAG, "google sign in failed", e)
            }
        }
    }

    private fun showErrorSnackbar() {
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

}