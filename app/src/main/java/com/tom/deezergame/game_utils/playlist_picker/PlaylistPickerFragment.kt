package com.tom.deezergame.game_utils.playlist_picker

import android.animation.LayoutTransition
import android.app.Activity
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.tom.deezergame.R
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.databinding.PlaylistPickerFragmentBinding
import timber.log.Timber
import java.lang.IllegalArgumentException

class PlaylistPickerFragment : Fragment() {

    private val viewModel: PlaylistPickerViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[PlaylistPickerViewModel::class.java]
    }

    private lateinit var viewModelFactory: PlaylistPickerViewModelFactory

    private lateinit var binding: PlaylistPickerFragmentBinding
    private lateinit var adapter: PlaylistAdapter

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
        setupSearchView()


        adapter = PlaylistAdapter(PlaylistListener { playlistId ->
            // navigate to game fragment
            viewModel.onPlaylistChosen(playlistId)
        })

        setupSwipeRefresh(binding)

        // Once playlist ID is picked navigate to the game
        // Which game we navigate to is passed into the fragment in the bundle
        viewModel.navigateToGame.observe(viewLifecycleOwner, Observer { playlistId ->
            Timber.d("$playlistId")
            playlistId?.let {
                val action = when (viewModel.gameType.value) {
                    Constants.ALBUM_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToAlbumGameFragment(
                            "$playlistId"
                        )
                    Constants.HIGH_LOW_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToHighLowGameFragment(
                            "$playlistId"
                        )
                    Constants.BEAT_INTRO_GAME_TYPE ->
                        PlaylistPickerFragmentDirections.actionPlaylistPickerFragmentToBeatTheIntroFragment(
                            "$playlistId"
                        )
                    else -> throw IllegalArgumentException("Unknown Game Type")
                }
                NavHostFragment.findNavController(this).navigate(action)
                viewModel.onNavigationToGame()
            }
        })

        binding.playlistRv.adapter = adapter

        // TODO General Playlists || Artist Playlists??
        // Load user playlists into rv if showUserPlaylists is true
//        viewModel.userPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
//            if (viewModel.showUserPlaylists.value == true) {
//                Timber.d("num_playlists: ${playlists.size}")
//                adapter.submitPlaylist(playlists)
//                binding.playlistTitle.text = getString(R.string.your_playlists)
//            }
//        })

//        Load common playlists into rv if showUserPlaylists is false
//        viewModel.commonPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
//            Timber.d("num_playlists: ${playlists.size}")
//            if (viewModel.showUserPlaylists.value == false) {
//                adapter.submitPlaylist(playlists)
//                binding.playlistTitle.text = getString(R.string.top_playlists)
//            }
//        })
        viewModel.dzPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
            Timber.d("num_playlists: ${playlists.size}")
            adapter.submitPlaylist(playlists)
            binding.playlistTitle.text = getString(R.string.top_playlists)

        })

        // Clicking the FAB changes which set of playlists the rv shows
        // - User playlists or Common playlists
//        viewModel.showUserPlaylists.observe(viewLifecycleOwner, Observer { showUserPlaylists ->
//            if (showUserPlaylists) {
//                viewModel.userPlaylists.value?.let { adapter.submitPlaylist(it) }
//                binding.playlistTitle.text = getString(R.string.your_playlists)
//            } else {
//                viewModel.commonPlaylists.value?.let { adapter.submitPlaylist(it) }
//                binding.playlistTitle.text = getString(R.string.top_playlists)
//            }
//        })

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
            // Only refresh when in top playlists mode
            if (viewModel.showSearchResults.value == false) viewModel.forceRefresh()
            else swipeContainer.isRefreshing = false
        }
        swipeContainer.setColorSchemeColors(
            ContextCompat.getColor(
                requireContext(),
                R.color.dz_red
            )
        )

    }

    private fun setupSearchView() {
        binding.searchView.maxWidth = Integer.MAX_VALUE
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    viewModel.searchTextChange(p0)
                }
                return false
            }

            // Show playlists when query is set back to null
            override fun onQueryTextChange(p0: String?): Boolean {
                if (p0 != null && p0.isBlank()) {
                    viewModel.showSearchResults(false)
                }
                return false
            }
        })

//        binding.searchView.layoutTransition = LayoutTransition()
//            .enableTransitionType(LayoutTransition.APPEARING)

        binding.searchView.setOnQueryTextFocusChangeListener { view, b ->
            Timber.d("$b ONQUERYTEXTFOCUSCHANGELISTENER")
            if (!b && binding.searchView.query.isEmpty()) {
                binding.searchView.isIconified = true
            }
        }

        viewModel.showSearchResults.observe(viewLifecycleOwner, { show ->
            if (show) {
                viewModel.searchResults.value?.let { adapter.submitPlaylist(it) }
            } else {
                viewModel.dzPlaylists.value?.let { adapter.submitPlaylist(it) }
            }
        })



//        viewModel.searchResults.observe(viewLifecycleOwner, { playlists ->
//            adapter.submitPlaylist(playlists)
//            binding.playlistTitle.text = "Search Results"
//        })

//        val searchView = binding.searchView
//        val searchSrcText : AutoCompleteTextView = searchView.findViewById(R.id.search_src_text)
//        searchSrcText.setOnFocusChangeListener { view, hasFocus ->
//            if (!hasFocus) {
//                val inputMethodManager = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
//                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
//            }
//        }
    }

}