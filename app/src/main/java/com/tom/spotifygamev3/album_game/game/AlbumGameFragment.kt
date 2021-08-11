package com.tom.spotifygamev3.album_game.game

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.Utils.Utils.glidePreloadImage
import com.tom.spotifygamev3.Utils.Utils.glideShowImage
import com.tom.spotifygamev3.album_game.score.AlbumScoreViewModelFactory
import com.tom.spotifygamev3.databinding.AlbumGameFragmentBinding
import com.tom.spotifygamev3.models.AlbumQuestion
import com.tom.spotifygamev3.models.spotify_models.Images

class AlbumGameFragment : Fragment() {

    private val viewModel: AlbumGameViewModel by lazy {
        ViewModelProvider(this, viewModelFactory).get(AlbumGameViewModel::class.java)
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
                doAlphaAnimation(binding.checkmark)
            }
            binding.albumScoreCounter.text =
                getString(R.string.score, newScore, Constants.ALBUM_GAME_NUM_QUESTIONS)
        })

        viewModel.numWrong.observe(viewLifecycleOwner, Observer { newWrong ->
            if (newWrong != 0) {
                doAlphaAnimation(binding.cross)
            }
            Log.d(TAG, "incorrect score observer")
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

    private fun doAlphaAnimation(imgView: ImageView) {
        val anim = AlphaAnimation(1f, 0f)
        anim.duration = 1500
        anim.fillAfter = true
        imgView.startAnimation(anim)
        imgView.visibility = View.VISIBLE
    }

    private fun gameFinished() {
        val action = AlbumGameFragmentDirections.actionAlbumGameFragmentToAlbumGameScoreFragment(
            score = viewModel.score.value ?: 0
        )
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }

    private fun preloadImage(images: List<Images>) {
        glidePreloadImage(images, requireContext())
    }

    private fun showQuestion(binding: AlbumGameFragmentBinding, albumQ: AlbumQuestion) {
        glideShowImage(albumQ.images, requireContext(), binding.albumCoverImage)

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
//        binding.albumFirstAnswerButton.text = albumQ.allAnswers[0]
//        binding.albumSecondAnswerButton.text = albumQ.allAnswers[1]
//        binding.albumThirdAnswerButton.text = albumQ.allAnswers[2]
//        binding.albumFourthAnswerButton.text = albumQ.allAnswers[3]
    }


}