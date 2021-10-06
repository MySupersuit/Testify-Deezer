package com.tom.deezergame

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName

    // TODO Figure out palette - get colour from artwork for background
    // https://developer.android.com/training/material/palette-colors

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (BuildConfig.DEBUG) {
            Timber.uprootAll()
            Timber.plant(Timber.DebugTree())
        }

        // dropping database for testing
//        val database = getDatabase(this)
//        CoroutineScope(Dispatchers.IO).launch {
//            database.clearAllTables()
//        }
    }
}