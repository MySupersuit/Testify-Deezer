package com.tom.deezergame.album_game

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.tom.deezergame.LoginActivity
import com.tom.deezergame.R
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.utils.Utils.glidePreloadImage
import com.tom.deezergame.databinding.AlbumGameFragmentBinding
import com.tom.deezergame.models.DzAlbumQuestion
import com.tom.deezergame.utils.Utils.doAlphaAnimation
import com.tom.deezergame.utils.Utils.glideShowImagePaletteV2
import timber.log.Timber

class AlbumGameFragment : Fragment() {

    private val viewModel: AlbumGameViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[AlbumGameViewModel::class.java]
    }

    private lateinit var viewModelFactory: AlbumGameViewModelFactory

    private val TAG = "AlbumGameFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AlbumGameFragmentBinding.inflate(inflater)
        viewModelFactory =
            AlbumGameViewModelFactory(
                requireActivity().application,
                AlbumGameFragmentArgs.fromBundle(requireArguments()).playlistId
            )

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        binding.mainBackground.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(R.color.spotify_black, R.color.spotify_black)
        )

        viewModel.currentQuestion.observe(viewLifecycleOwner, Observer { albumQuestion ->
            showQuestion(binding, albumQuestion)
        })

        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQuestion ->
            if (nextQuestion.correctAnswer != "" ) {
                Timber.d("preloading ${nextQuestion.correctAnswer}")
                preloadImage(nextQuestion.images)
            }
        })

        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            // Some correct animation
            if (newScore != 0) {
                doAlphaAnimation(binding.albumCheckmark)
            }
            binding.albumScoreCounter.text = newScore.toString()
        })

        viewModel.numWrong.observe(viewLifecycleOwner, Observer { newWrong ->
            if (newWrong != 0) {
                doAlphaAnimation(binding.albumCross)
            }
        })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) gameFinished()
        })

        viewModel.numAlbumsLoaded.observe(viewLifecycleOwner, Observer { numLoaded ->
            binding.loadingMessage.text = getString(
                R.string.loading_prog_message, numLoaded, Constants.ALBUM_GAME_NUM_QUESTIONS
            )
        })

        viewModel.loginClick.observe(viewLifecycleOwner, Observer { login ->
            if (login) {
                val intent = Intent(requireActivity(), LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(intent)
                requireActivity().finish()
                viewModel.onLoginClickFinish()
            }
        })

        return binding.root
    }

    private fun gameFinished() {
        val action = AlbumGameFragmentDirections.actionAlbumGameFragmentToAlbumGameScoreFragment(
            score = getString(
                R.string.score,
                viewModel.score.value ?: 0,
                Constants.ALBUM_GAME_NUM_QUESTIONS
            ),
            gameType = Constants.ALBUM_GAME_TYPE
        )
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }

    private fun preloadImage(images: List<String>) {
        glidePreloadImage(images, requireContext())
    }

    private fun showQuestion(binding: AlbumGameFragmentBinding, albumQ: DzAlbumQuestion) {
        // includes transition
        glideShowImagePaletteV2(albumQ.images, requireContext(), binding.albumCoverImage, binding)

        // Work around for artists with not enough albums
        val buttons = listOf(
            binding.albumFirstAnswerButton, binding.albumSecondAnswerButton,
            binding.albumThirdAnswerButton, binding.albumFourthAnswerButton
        )
        for (i in albumQ.allAnswers.indices) {
            buttons[i].text = albumQ.allAnswers[i]
            buttons[i].isClickable = true
        }
        for (i in albumQ.allAnswers.size..3) {
            buttons[i].text = ""
            buttons[i].isClickable = false
        }
    }


}