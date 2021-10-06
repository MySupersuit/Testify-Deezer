package com.tom.deezergame.repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.tom.deezergame.database.DzPlaylistDatabase
import com.tom.deezergame.database.PlaylistDatabase
import com.tom.deezergame.database.asDomainModel
import com.tom.deezergame.models.deezer_models.UserPlaylistData
import com.tom.deezergame.models.deezer_models.asDatabaseModel
import com.tom.deezergame.models.spotify_models.SimplePlaylist
import com.tom.deezergame.models.spotify_models.asDatabaseModel
import com.tom.deezergame.network.ApiClient
import com.tom.deezergame.network.NetworkConstants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlaylistRepository(
    private val database: PlaylistDatabase,
    private val dzDatabase: DzPlaylistDatabase,
    private val context: Context
) {

    val userPlaylists: LiveData<List<SimplePlaylist>> =
        Transformations.map(database.playlistDao.getUserPlaylists()) {
            it.asDomainModel()
        }

    val commonPlaylists: LiveData<List<SimplePlaylist>> =
        Transformations.map(database.playlistDao.getCommonPlaylists()) {
            it.asDomainModel()
        }

    val dzUserPlaylists: LiveData<List<UserPlaylistData>> =
        Transformations.map(dzDatabase.playlistDao.getUserPlaylists()) {
            it.asDomainModel()
        }

    suspend fun refreshUserPlaylists() {
        Timber.d("INITIAL LOADING")
        withContext(Dispatchers.IO) {
            try {
                val playlists = ApiClient().getDeezerApiService(context)
                    .getUserPlaylists(NetworkConstants.DZ_USER).data
                Timber.d("sized " + playlists.size)
                dzDatabase.playlistDao.insertUserPlaylists(playlists.asDatabaseModel())
                Timber.d("REFRESHED")
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    suspend fun forceRefreshPlaylists() {
        Timber.d("REFRESHING")
        withContext(Dispatchers.IO) {
            try {
                dzDatabase.playlistDao.deleteUsers()
                val playlists = ApiClient().getDeezerApiService(context)
                    .getUserPlaylists(NetworkConstants.DZ_USER).data
                Timber.d("putting ${playlists.size} in")
                dzDatabase.playlistDao.insertUserPlaylists(playlists.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    suspend fun addCommonPlaylist(id: String) {
        withContext(Dispatchers.IO) {
            try {
                val playlist = ApiClient().getApiService(context).getPlaylist(id)
                database.playlistDao.insertCommonPlaylists(playlist.asDatabaseModel())
            } catch (e: Exception) {
                Timber.e("addcommonplaylist $e")
            }
        }
    }
}