package com.tom.spotifygamev3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.tom.spotifygamev3.database.getDatabase
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    // TODO Figure out palette - get colour from artwork for background
    // https://developer.android.com/training/material/palette-colors

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // dropping database for testing
//        val database = getDatabase(this)
//        CoroutineScope(Dispatchers.IO).launch {
//            database.clearAllTables()
//        }
    }
}