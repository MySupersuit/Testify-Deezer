package com.tom.spotifygamev3.album_game

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.databinding.AlbumGameScoreFragmentBinding

class AlbumGameScoreFragment : Fragment() {

    private val viewModel: AlbumGameScoreViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AlbumGameScoreViewModel::class.java)
    }

    private lateinit var viewModelFactory: AlbumGameScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AlbumGameScoreFragmentBinding.inflate(inflater)
        viewModelFactory =
            AlbumGameScoreViewModelFactory(AlbumGameScoreFragmentArgs.fromBundle(requireArguments()).score)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.albumScore.text =
            getString(R.string.score, viewModel.score.value, Constants.ALBUM_GAME_NUM_QUESTIONS)

        viewModel.eventPlayAgain.observe(viewLifecycleOwner, Observer { playAgain ->
            if (playAgain) {
                findNavController().navigate(AlbumGameScoreFragmentDirections.actionAlbumGameRestart())
                viewModel.onPlayAgainComplete()
            }
        })

        viewModel.eventGoHome.observe(viewLifecycleOwner, Observer { goHome ->
            if (goHome) {
                findNavController().navigate(AlbumGameScoreFragmentDirections.actionAlbumGameGoHome())
                viewModel.onGoHomeComplete()
            }
        })

        return binding.root

    }

}