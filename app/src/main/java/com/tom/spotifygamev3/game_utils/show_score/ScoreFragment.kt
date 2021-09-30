package com.tom.spotifygamev3.game_utils.show_score

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.material.snackbar.Snackbar
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.GameScoreFragmentBinding
import com.tom.spotifygamev3.utils.Constants
import timber.log.Timber

class ScoreFragment : Fragment() {

    private val TAG = "ScoreFragment"
    private val RC_LEADERBOARD = 9005

    private val viewModel: ScoreViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(ScoreViewModel::class.java)
    }

    private lateinit var viewModelFactory: ScoreViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = GameScoreFragmentBinding.inflate(inflater)
        val args = ScoreFragmentArgs.fromBundle(requireArguments())

        viewModelFactory =
            ScoreViewModelFactory(requireActivity().application, args.score, args.gameType)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.albumScore.text = viewModel.score.value

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

                    Constants.BEAT_INTRO_GAME_TYPE ->
                        findNavController().navigate(
                            ScoreFragmentDirections.actionGameRestart(
                                Constants.BEAT_INTRO_GAME_TYPE
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

        viewModel.submitScore.observe(viewLifecycleOwner, Observer { score ->
            if (score != null) {
                val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                if (account == null) {
                    Timber.d("no account")
                    return@Observer
                }

                val leaderboardId = getLeaderboardId()

                Timber.d("submitting ${viewModel.submitted}")
                if (!viewModel.submitted) {
                    Games.getLeaderboardsClient(requireContext(), account)
                        .submitScore(leaderboardId, score.toLong())
                    showSubmissionSnackbar(binding, "Submitted")
                } else {
                    showSubmissionSnackbar(binding, "Already submitted")
                }
                viewModel.submitScoreFinish()
            }
        })

        viewModel.showLeaderboard.observe(viewLifecycleOwner, Observer { show ->
            if (show) {
                val account = GoogleSignIn.getLastSignedInAccount(requireContext())
                if (account == null) {
                    Timber.d("no account")
                    return@Observer
                }
                val leaderboardId = getLeaderboardId()

                Games.getLeaderboardsClient(requireContext(), account)
                    .getLeaderboardIntent(leaderboardId)
                    .addOnSuccessListener { intent ->
                        startActivityForResult(intent, RC_LEADERBOARD)
                    }
                viewModel.leaderboardClickFinish()
            }

        })

        return binding.root

    }

    private fun getLeaderboardId(): String {
        return when (viewModel.gameType.value) {
            Constants.ALBUM_GAME_TYPE -> getString(R.string.album_leaderboard)
            Constants.BEAT_INTRO_GAME_TYPE -> getString(R.string.beat_intro_leaderboard)
            Constants.HIGH_LOW_GAME_TYPE -> getString(R.string.high_low_leaderboard)
            else -> "0"
        }
    }

    private fun showSubmissionSnackbar(binding: GameScoreFragmentBinding, message: String) {
        val green = ContextCompat.getColor(requireContext(), R.color.spotify_green)
        val white = ContextCompat.getColor(requireContext(), R.color.spotify_white)
        val snackbar = Snackbar.make(
            binding.main,
            message,
            Snackbar.LENGTH_SHORT
        )
        for (view in snackbar.view.allViews) {
            view.setBackgroundColor(green)
        }
        snackbar.show()
    }

}