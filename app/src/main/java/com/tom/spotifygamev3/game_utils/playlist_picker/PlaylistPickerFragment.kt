package com.tom.spotifygamev3.game_utils.playlist_picker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.databinding.PlaylistPickerFragmentBinding
import java.lang.IllegalArgumentException

class PlaylistPickerFragment : Fragment() {

    private val TAG = "PlaylistPickerFragment"

    private val viewModel: PlaylistPickerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlaylistPickerViewModel::class.java]
    }

    private lateinit var viewModelFactory: PlaylistPickerViewModelFactory

    private lateinit var binding: PlaylistPickerFragmentBinding
    private lateinit var adapter: PlaylistAdapter

//    val adapter = PlaylistRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = PlaylistPickerFragmentBinding.inflate(inflater)

        viewModelFactory =
            PlaylistPickerViewModelFactory(
                requireActivity().application,
                PlaylistPickerFragmentArgs.fromBundle(requireArguments()).gameType
            )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        adapter = PlaylistAdapter(PlaylistListener { playlistId ->
            // navigate to game fragment
            viewModel.onPlaylistChosen(playlistId)
        })

        setupSwipeRefresh(binding)

        // Once playlist ID is picked navigate to the game
        // Which game we navigate to is passed into the fragment in the bundle
        viewModel.navigateToGame.observe(viewLifecycleOwner, Observer { playlistId ->
            Log.d(TAG, playlistId ?: "nulled")
            playlistId?.let {
                val action = when (viewModel.gameType.value) {
                    Constants.ALBUM_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToAlbumGameFragment(
                            playlistId
                        )
                    Constants.HIGH_LOW_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToHighLowGameFragment(
                            playlistId
                        )
                    Constants.BEAT_INTRO_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToBeatTheIntroFragment(
                            playlistId
                        )
                    else -> throw IllegalArgumentException("Unknown Game Type")
                }
                NavHostFragment.findNavController(this).navigate(action)
                viewModel.onNavigationToGame()
            }
        })

        binding.playlistRv.adapter = adapter

        // Load user playlists into rv if showUserPlaylists is true
        viewModel.userPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
            if (viewModel.showUserPlaylists.value == true) {
                Log.d(TAG, "num_playlists: ${playlists.size}")
                adapter.submitPlaylist(playlists)
                binding.playlistTitle.text = getString(R.string.your_playlists)
            }
        })

//        Load common playlists into rv if showUserPlaylists is false
        viewModel.commonPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
            Log.d(TAG, "num_playlists: ${playlists.size}")
            if (viewModel.showUserPlaylists.value == false) {
                adapter.submitPlaylist(playlists)
                binding.playlistTitle.text = getString(R.string.top_playlists)
            }
        })

        // Clicking the FAB changes which set of playlists the rv shows
        // - User playlists or Common playlists
        viewModel.showUserPlaylists.observe(viewLifecycleOwner, Observer { showUserPlaylists ->
            if (showUserPlaylists) {
                viewModel.userPlaylists.value?.let { adapter.submitPlaylist(it) }
                binding.playlistTitle.text = getString(R.string.your_playlists)
            } else {
                viewModel.commonPlaylists.value?.let { adapter.submitPlaylist(it) }
                binding.playlistTitle.text = getString(R.string.top_playlists)
            }
        })

        viewModel.finishDataFetch.observe(viewLifecycleOwner, Observer { finished ->
            if (finished) {
                viewModel.onFinishAckd()
                binding.swipeRefresh.isRefreshing = false
            }
        })

        return binding.root
    }

    private fun setupSwipeRefresh(binding: PlaylistPickerFragmentBinding) {
        val swipeContainer = binding.swipeRefresh
        swipeContainer.setOnRefreshListener {
            viewModel.forceRefresh()
        }
        swipeContainer.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.spotify_green
            )
        )

    }

}