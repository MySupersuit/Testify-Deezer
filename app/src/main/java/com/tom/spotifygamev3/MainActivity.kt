package com.tom.spotifygamev3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.tom.spotifygamev3.database.getDatabase
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // dropping database for testing
//        val database = getDatabase(this)
//        CoroutineScope(Dispatchers.IO).launch {
//            database.clearAllTables()
//        }
    }
}