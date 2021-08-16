package com.tom.spotifygamev3.album_game.score

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.databinding.AlbumGameScoreFragmentBinding

class AlbumScoreFragment : Fragment() {

    private val viewModel: AlbumScoreViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AlbumScoreViewModel::class.java)
    }

    private lateinit var viewModelFactory: AlbumScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AlbumGameScoreFragmentBinding.inflate(inflater)
        val args = AlbumScoreFragmentArgs.fromBundle(requireArguments())
        viewModelFactory =
            AlbumScoreViewModelFactory(args.score, args.numQuestions)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.albumScore.text =
            getString(R.string.score, viewModel.score.value, Constants.ALBUM_GAME_NUM_QUESTIONS)

        viewModel.eventPlayAgain.observe(viewLifecycleOwner, Observer { playAgain ->
            if (playAgain) {
                findNavController().navigate(
                    AlbumScoreFragmentDirections.actionAlbumGameRestart(
                        Constants.ALBUM_GAME_TYPE
                    )
                )
                viewModel.onPlayAgainComplete()
            }
        })

        viewModel.eventGoHome.observe(viewLifecycleOwner, Observer { goHome ->
            if (goHome) {
                findNavController().navigate(AlbumScoreFragmentDirections.actionAlbumGameGoHome())
                viewModel.onGoHomeComplete()
            }
        })

        return binding.root

    }

}