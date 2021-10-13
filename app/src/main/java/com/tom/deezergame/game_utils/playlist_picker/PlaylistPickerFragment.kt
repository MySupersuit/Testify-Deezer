package com.tom.deezergame.game_utils.playlist_picker

import android.animation.LayoutTransition
import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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

        viewModel.dzPlaylists.observe(viewLifecycleOwner, Observer { playlists ->
            Timber.d("num_playlists: ${playlists.size}")
            adapter.submitPlaylist(playlists)
            binding.playlistTitle.text = getString(R.string.top_playlists)

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
        val searchText = binding.searchView.findViewById<TextView>(R.id.search_src_text)
        searchText.setTextColor(ContextCompat.getColor(requireContext(), R.color.dz_black))
        searchText.typeface = ResourcesCompat.getFont(requireContext(), R.font.mabry_deezer_regular)
        val TRIGGER_SEARCH = 100
        val TRIGGER_DELAY : Long = 800

        val handler = Handler(Looper.getMainLooper(), Handler.Callback { msg ->
            if (msg.what == TRIGGER_SEARCH) {
                val query = binding.searchView.query.toString()
                if (query.isNotBlank()) viewModel.searchTextChange(query)
            }
            true
        })

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
                } else { // if not blank, search after some time\
                    handler.removeMessages(TRIGGER_SEARCH)
                    handler.sendEmptyMessageDelayed(TRIGGER_SEARCH, TRIGGER_DELAY)
                }
                return false
            }
        })


        binding.searchView.setOnQueryTextFocusChangeListener{ view, b ->
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

    }

}