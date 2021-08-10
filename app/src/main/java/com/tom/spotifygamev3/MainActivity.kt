package com.tom.spotifygamev3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.Utils.SessionManager
import com.tom.spotifygamev3.models.SpotifyPlaylistResponse
import com.tom.spotifygamev3.network.ApiClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}