package com.tom.spotifygamev3.album_game

import android.graphics.drawable.Drawable
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.Utils.Utils.glidePreloadImage
import com.tom.spotifygamev3.Utils.Utils.glideShowImage
import com.tom.spotifygamev3.databinding.AlbumGameFragmentBinding
import com.tom.spotifygamev3.home.HomeViewModel
import com.tom.spotifygamev3.models.AlbumQuestion
import com.tom.spotifygamev3.models.Images

class AlbumGameFragment : Fragment() {

    private val viewModel: AlbumGameViewModel by lazy {
        ViewModelProvider(this).get(AlbumGameViewModel::class.java)
    }

    private val TAG = "AlbumGameFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = AlbumGameFragmentBinding.inflate(inflater)

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
            binding.albumScoreCounter.text =
                getString(R.string.score, newScore, Constants.ALBUM_GAME_NUM_QUESTIONS)
        })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) gameFinished()
        })

        viewModel.numAlbumsLoaded.observe(viewLifecycleOwner, Observer { numLoaded ->
            binding.loadingMessage.text = getString(
                R.string.loading_prog_message, numLoaded, Constants.ALBUM_GAME_NUM_QUESTIONS)
        })

        return binding.root
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
        binding.albumFirstAnswerButton.text = albumQ.allAnswers[0]
        binding.albumSecondAnswerButton.text = albumQ.allAnswers[1]
        binding.albumThirdAnswerButton.text = albumQ.allAnswers[2]
        binding.albumFourthAnswerButton.text = albumQ.allAnswers[3]
    }


}