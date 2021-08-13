package com.tom.spotifygamev3.game_utils.playlist_picker.v2

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.databinding.SwipePlaylistPickerBinding
import com.tom.spotifygamev3.game_utils.playlist_picker.*
import java.lang.IllegalArgumentException

class PlaylistPickerFragment2 : Fragment() {

    private val TAG = "PlaylistPickerFragment"

    private val viewModel: PlaylistPickerViewModel2 by lazy {
        ViewModelProvider(this)[PlaylistPickerViewModel2::class.java]
    }

    private lateinit var viewModelFactory: PlaylistPickerViewModelFactory
    private lateinit var viewPagerAdapter: ViewPagerAdapter

//    val adapter = PlaylistRecyclerAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = SwipePlaylistPickerBinding.inflate(inflater)

//        viewModelFactory =
//            PlaylistPickerViewModelFactory(
//                requireActivity().application,
//                PlaylistPickerFragmentArgs.fromBundle(requireArguments()).gameType
//            )

        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewPagerAdapter = ViewPagerAdapter(this)

        binding.viewPager.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager) {tab, position ->
            tab.text = "OBJECT ${(position+1)}"
        }.attach()


//        val adapter = PlaylistAdapter(PlaylistListener { playlistId ->
//            // navigate to game fragment
//            viewModel.onPlaylistChosen(playlistId)
//        })

//        viewModel.navigateToGame.observe(viewLifecycleOwner, Observer { playlistId ->
//            Log.d(TAG, playlistId ?: "nulled")
//            playlistId?.let {
//
//                val action = when (viewModel.gameType.value) {
//                    Constants.ALBUM_GAME_TYPE ->
//                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToAlbumGameFragment(
//                            playlistId = playlistId
//                        )
//                    Constants.HIGH_LOW_GAME_TYPE ->
//                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToHighLowGameFragment(
//                            playlistId
//                        )
//                    else -> throw IllegalArgumentException("Unknown Game Type")
//                }
//                NavHostFragment.findNavController(this).navigate(action)
//                viewModel.onNavigationToGame()
//            }
//        })

//        binding.playlistRv.adapter = adapter
//
//        viewModel.userPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
//            adapter.submitPlaylist(playlists)
//        })

            return binding.root
    }


}