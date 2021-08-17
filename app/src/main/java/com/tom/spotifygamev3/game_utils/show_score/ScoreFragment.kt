package com.tom.spotifygamev3.game_utils.show_score

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

class ScoreFragment : Fragment() {

    private val viewModel: ScoreViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ScoreViewModel::class.java)
    }

    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AlbumGameScoreFragmentBinding.inflate(inflater)
        val args = ScoreFragmentArgs.fromBundle(requireArguments())
        // need another arg for game coming from? so I can replay it
        viewModelFactory =
            ScoreViewModelFactory(args.score, args.numQuestions, args.gameType)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.albumScore.text =
            getString(R.string.score, viewModel.score.value, Constants.ALBUM_GAME_NUM_QUESTIONS)

        viewModel.eventPlayAgain.observe(viewLifecycleOwner, Observer { playAgain ->
            if (playAgain) {
                when (viewModel.gameType.value) {
                    Constants.ALBUM_GAME_TYPE ->
                        findNavController().navigate(
                            ScoreFragmentDirections.actionGameRestart(
                                Constants.ALBUM_GAME_TYPE
                            )
                        )

                    Constants.HIGH_LOW_GAME_TYPE ->
                        findNavController().navigate(
                            ScoreFragmentDirections.actionGameRestart(
                                Constants.HIGH_LOW_GAME_TYPE
                            )
                        )
                }

                viewModel.onPlayAgainComplete()
            }
        })

        viewModel.eventGoHome.observe(viewLifecycleOwner, Observer { goHome ->
            if (goHome) {
                findNavController().navigate(ScoreFragmentDirections.actionAlbumGameGoHome())
                viewModel.onGoHomeComplete()
            }
        })

        return binding.root

    }

}