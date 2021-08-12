package com.tom.spotifygamev3.higher_lower_game.game

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.album_game.game.SpotifyApiStatus
import com.tom.spotifygamev3.models.lastfm_models.LfmTrack
import com.tom.spotifygamev3.models.spotify_models.Items
import com.tom.spotifygamev3.network.ApiClient
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.lang.Exception

class HighLowGameViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "HighLowGameViewModel"

    private var apiClient: ApiClient = ApiClient()
    private var initialItems = listOf<Items>()

    private val _lfmTrack = MutableLiveData<LfmTrack>()
    val lfmTrack: LiveData<LfmTrack>
        get() = _lfmTrack

    private val _status = MutableLiveData<SpotifyApiStatus>()
    val status: LiveData<SpotifyApiStatus>
        get() = _status

    init {
        runBlocking {
            _status.value = SpotifyApiStatus.LOADING
            fetchData()
        }
    }

    private suspend fun fetchData() {
        viewModelScope.launch {
            Log.d(TAG, "fetching data")

            val job = fetchTracks()
            job.join()
            Log.d(TAG, "initialTracks fetched")

            val track1 = initialItems[0].track
            val trackName = track1.name
            val artistName = track1.artists[0].name

            val job1 = fetchTrackPlaycount(artistName, trackName)
            job1.join()
            Log.d(TAG, "${_lfmTrack.value?.name} has ${_lfmTrack.value?.playCount} plays")

        }
    }

    private fun fetchTracks(playlist_id: String = Constants.TOP_50_IRL_URI): Job {
        val job = viewModelScope.launch {
            try {
                val localItems =
                    apiClient.getApiService(getApplication()).getPlaylistTracks(playlist_id).items
                initialItems = localItems
            } catch (e: Exception) {
                _status.value = SpotifyApiStatus.ERROR
                Log.e(TAG, e.toString())
            }
        }
        return job
    }

    private fun fetchTrackPlaycount(
        artist: String = "Billie Eilish",
        trackName: String = "bad guy"
    ): Job {
        val job = viewModelScope.launch {
            try {
                _lfmTrack.value = apiClient.getLastFmApiService(getApplication())
                    .getLastFmTrackInfo(
                        "track.getInfo",
                        Constants.LASTFM_API_KEY,
                        artist,
                        trackName
                    ).track
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
        return job
    }
}