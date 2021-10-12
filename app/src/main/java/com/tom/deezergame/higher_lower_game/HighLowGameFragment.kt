package com.tom.deezergame.higher_lower_game

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.tom.deezergame.LoginActivity
import com.tom.deezergame.R
import com.tom.deezergame.album_game.AlbumGameFragmentDirections
import com.tom.deezergame.databinding.AlbumGameFragmentBinding
import com.tom.deezergame.databinding.HighLowGameFragment3Binding
import com.tom.deezergame.models.DzHighLowQuestion
import com.tom.deezergame.utils.Constants
import com.tom.deezergame.utils.Utils.glidePreloadImage
import com.tom.deezergame.utils.Utils.glideShowImage
import com.tom.deezergame.utils.Utils.hlShowImage1
import com.tom.deezergame.utils.Utils.hlShowImage2
import timber.log.Timber

class HighLowGameFragment : Fragment() {

    private val TAG = "HighLowGameFragment"

    private val viewModel: HighLowGameViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[HighLowGameViewModel::class.java]
    }

    private lateinit var viewModelFactory: HighLowGameViewModelFactory

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = HighLowGameFragment3Binding.inflate(inflater)
        viewModelFactory =
            HighLowGameViewModelFactory(
                requireActivity().application,
                HighLowGameFragmentArgs.fromBundle(requireArguments()).playlistId
            )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.modalCl.visibility = View.GONE

        val black = ContextCompat.getColor(requireContext(), R.color.spotify_black)

        binding.bground1.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(black, black)
        )
        binding.bground2.background = GradientDrawable(
            GradientDrawable.Orientation.TOP_BOTTOM,
            intArrayOf(black, black)
        )

        // TODO grab artist images from API call
        viewModel.currentQuestion.observe(viewLifecycleOwner, Observer { question ->
            Timber.d("curr question")
            showQuestion(binding, question)
        })

        // TODO try cache all images when loading??
        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQuestion ->
            Timber.d("next question")
            if (nextQuestion != null) {
//                Glide.with(requireContext()).clear(binding.modalCorrectImage)
//                Glide.with(requireContext()).clear(binding.modalWrongImage)
//                Glide.with(requireContext()).clear(binding.imageAns1)
//                Glide.with(requireContext()).clear(binding.imageAns2)
                glidePreloadImage(nextQuestion.track1.getImages(), requireContext())
                glidePreloadImage(nextQuestion.track2.getImages(), requireContext())
            }
        })

        viewModel.numQsLoaded.observe(viewLifecycleOwner, Observer { loaded ->
            binding.loadingMessage.text = getString(
                R.string.loading_prog_message,
                loaded, Constants.HIGH_LOW_NUM_QUESTIONS * 2
            )
        })

        viewModel.score.observe(viewLifecycleOwner, Observer { score ->
            binding.highLowScoreTv.text = score.toString()
        })

        viewModel.showModal.observe(viewLifecycleOwner, Observer { question ->
            if (question != null) {
                showModal(binding, question)
            } else {
                hideModal(binding)
            }
        })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) gameFinished()
        })

        viewModel.loginClick.observe(viewLifecycleOwner, Observer { login ->
            if (login) {
//                val intent = Intent(requireActivity(), LoginActivity::class.java)
//                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(intent)
//                requireActivity().finish()
//                viewModel.onLoginClickFinish()
                NavHostFragment.findNavController(this).navigate(
                    HighLowGameFragmentDirections.actionHighLowGameFragmentToPlaylistPickerFragment(
                        gameType = Constants.HIGH_LOW_GAME_TYPE
                    )
                )
            }
        })

        viewModel.enoughQuestions.observe(viewLifecycleOwner, { enoughQs ->
            if (!enoughQs) {
                showErrorSnackbar(binding)
                viewModel.onEnoughQsHandle()
            }
        })

        return binding.root
    }

    private fun gameFinished() {
        val action =
            HighLowGameFragmentDirections.actionHighLowGameFragmentToGameScoreFragment(
                score = getString(
                    R.string.score,
                    viewModel.score.value ?: 0,
                    Constants.HIGH_LOW_NUM_QUESTIONS
                ),
                gameType = Constants.HIGH_LOW_GAME_TYPE
            )
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }

    private fun showModal(binding: HighLowGameFragment3Binding, question: DzHighLowQuestion) {
        disableAnswerButtons(binding)
        binding.modalTitle.text = if (question.correct == true) "Correct!" else "Wrong!"

        val correctTrack =
            if (question.track1.playCount > question.track2.playCount) question.track1 else question.track2
        val wrongTrack =
            if (question.track1.playCount > question.track2.playCount) question.track2 else question.track1


        glideShowImage(correctTrack.getImages(), requireContext(), binding.modalCorrectImage)
//        glideShowImageModal(correctTrack.getImages(), requireContext(), binding)
        binding.modalCorrectText.text =
            getString(R.string.num_streams, String.format("%,d", correctTrack.playCount))

        glideShowImage(wrongTrack.getImages(), requireContext(), binding.modalWrongImage)
        binding.modalWrongText.text =
            getString(R.string.num_streams, String.format("%,d", wrongTrack.playCount))

        binding.modalCl.visibility = View.VISIBLE
    }

    private fun hideModal(binding: HighLowGameFragment3Binding) {
        enableAnswerButtons(binding)
        binding.modalCl.visibility = View.GONE
    }

    private fun showQuestion(binding: HighLowGameFragment3Binding, question: DzHighLowQuestion) {
        val track1 = question.track1
        val track2 = question.track2

//        glideShowImageLoadAnim(track1.album.images, requireContext(), binding.imageAns1)
//        hlShowImage1(track1.album.images, requireContext(), binding)
        hlShowImage1(track1.getImages(), requireContext(), binding)
        binding.artistAns1.text = track1.artist.name
        binding.songAns1.text = track1.title_short

//        glideShowImageLoadAnim(track2.album.images, requireContext(), binding.imageAns2)
        hlShowImage2(track2.getImages(), requireContext(), binding)
        binding.artistAns2.text = track2.artist.name
        binding.songAns2.text = track2.title_short
    }

    private fun disableAnswerButtons(binding: HighLowGameFragment3Binding) {
        binding.cvAns1.isClickable = false
        binding.cvAns2.isClickable = false
    }

    private fun enableAnswerButtons(binding: HighLowGameFragment3Binding) {
        binding.cvAns1.isClickable = true
        binding.cvAns2.isClickable = true
    }

    private fun showErrorSnackbar(binding: HighLowGameFragment3Binding) {
        val red = ContextCompat.getColor(requireContext(), R.color.dz_red)
        val white = ContextCompat.getColor(requireContext(), R.color.spotify_white)
        val snackbar = Snackbar.make(
            binding.mainCl,
            "Not enough songs in playlist",
            Snackbar.LENGTH_LONG
        )
        snackbar.setAction("OK") {
        }
        snackbar.setActionTextColor(white)
        for (view in snackbar.view.allViews) {
            view.setBackgroundColor(red)
        }
        snackbar.show()
    }

}