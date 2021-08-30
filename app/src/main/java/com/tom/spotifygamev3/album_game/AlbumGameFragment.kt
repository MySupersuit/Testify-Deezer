package com.tom.spotifygamev3.album_game

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.palette.graphics.Palette
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.utils.Utils.glidePreloadImage
import com.tom.spotifygamev3.utils.Utils.glideShowImage
import com.tom.spotifygamev3.databinding.AlbumGameFragmentBinding
import com.tom.spotifygamev3.models.AlbumQuestion
import com.tom.spotifygamev3.models.spotify_models.Images
import com.tom.spotifygamev3.utils.Utils.doAlphaAnimation
import com.tom.spotifygamev3.utils.Utils.glideShowImagePalette

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

        viewModel.currentQuestion.observe(viewLifecycleOwner, Observer { albumQuestion ->
            showQuestion(binding, albumQuestion)
        })

        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQuestion ->
            if (nextQuestion != null) {
                preloadImage(nextQuestion.images)
            }
        })

        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            // Some correct animation
            if (newScore != 0) {
                doAlphaAnimation(binding.albumCheckmark)
            }
            binding.albumScoreCounter.text =
                getString(R.string.score, newScore, Constants.ALBUM_GAME_NUM_QUESTIONS)
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

    private fun preloadImage(images: List<Images>) {
        glidePreloadImage(images, requireContext())
    }

    private fun showQuestion(binding: AlbumGameFragmentBinding, albumQ: AlbumQuestion) {
        // includes transition
        glideShowImagePalette(albumQ.images, requireContext(), binding.albumCoverImage, binding)

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