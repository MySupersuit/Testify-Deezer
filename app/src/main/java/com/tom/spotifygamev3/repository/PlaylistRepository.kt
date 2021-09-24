package com.tom.spotifygamev3.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tom.spotifygamev3.database.PlaylistDatabase
import com.tom.spotifygamev3.database.asDomainModel
import com.tom.spotifygamev3.models.spotify_models.SimplePlaylist
import com.tom.spotifygamev3.models.spotify_models.asDatabaseModel
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepository(private val database: PlaylistDatabase, private val context: Context) {

    private val TAG = "PlaylistRepository"

    val userPlaylists: LiveData<List<SimplePlaylist>> =
        Transformations.map(database.playlistDao.getUserPlaylists()) {
            it.asDomainModel()
        }

    val commonPlaylists: LiveData<List<SimplePlaylist>> =
        Transformations.map(database.playlistDao.getCommonPlaylists()) {
            it.asDomainModel()
        }

    suspend fun refreshUserPlaylists() {
        Log.d(TAG, "REFRESHING")
        withContext(Dispatchers.IO) {
            try {
                val playlists = ApiClient().getApiService(context).getUserPlaylists().playlists
                database.playlistDao.insertAllUserPlaylists(playlists.asDatabaseModel())
                Log.d(TAG, "REFRESHED")
            } catch (e: Exception) {
                Log.e(TAG, "refreshUserPlaylists $e")
            }
        }
    }

    suspend fun addCommonPlaylist(id: String) {
        withContext(Dispatchers.IO) {
            try {
                val playlist = ApiClient().getApiService(context).getPlaylist(id)
                database.playlistDao.insertCommonPlaylists(playlist.asDatabaseModel())
            } catch (e: Exception) {
                Log.e(TAG, "addcommonplaylist $e")
            }
        }
    }
}