package com.tom.spotifygamev3.album_game.playlist_picker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.album_game.game.AlbumGameViewModel
import com.tom.spotifygamev3.databinding.PlaylistPickerFragmentBinding

class PlaylistPickerFragment : Fragment() {

    private val TAG = "PlaylistPickerFragment"

    private val viewModel: PlaylistPickerViewModel by lazy {
        ViewModelProvider(this)[PlaylistPickerViewModel::class.java]
    }

//    val adapter = PlaylistRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = PlaylistPickerFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val adapter = PlaylistAdapter(PlaylistListener { playlistId ->
            Toast.makeText(context, "${playlistId} clicked", Toast.LENGTH_SHORT).show()
            // navigate to game fragment
            viewModel.onPlaylistChosen(playlistId)
        })

        viewModel.navigateToGame.observe(viewLifecycleOwner, Observer { playlistId ->
            Log.d(TAG, playlistId?:"nulled")
            playlistId?.let {
                val action =
                    PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToAlbumGameFragment(
                        playlistId = playlistId
                    )
                NavHostFragment.findNavController(this).navigate(action)
                viewModel.onNavigationToGame()
            }
        })

        binding.playlistRv.adapter = adapter

        viewModel.playlists.observe(viewLifecycleOwner, Observer { playlists ->
            adapter.submitPlaylist(playlists)
        })

        return binding.root
    }


}