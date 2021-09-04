package com.tom.spotifygamev3.higher_lower_game

import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.HighLowGameFragment3Binding
import com.tom.spotifygamev3.models.HighLowQuestion
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.utils.Utils.glidePreloadImage
import com.tom.spotifygamev3.utils.Utils.glideShowImage
import com.tom.spotifygamev3.utils.Utils.hlShowImage1
import com.tom.spotifygamev3.utils.Utils.hlShowImage2

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
            showQuestion(binding, question)
        })

        // TODO try cache all images when loading??
        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQuestion ->
            if (nextQuestion != null) {
                glidePreloadImage(nextQuestion.track1.track.album.images, requireContext())
                glidePreloadImage(nextQuestion.track2.track.album.images, requireContext())
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

        return binding.root
    }

    private fun gameFinished() {
        val action =
            HighLowGameFragmentDirections.actionHighLowGameFragmentToAlbumGameScoreFragment(
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

    private fun showModal(binding: HighLowGameFragment3Binding, question: HighLowQuestion) {
        disableAnswerButtons(binding)
        binding.modalTitle.text = if (question.correct == true) "Correct :)" else "Wrong :("

        val correctTrack =
            if (question.track1.playCount > question.track2.playCount) question.track1 else question.track2
        val wrongTrack =
            if (question.track1.playCount > question.track2.playCount) question.track2 else question.track1

        glideShowImage(correctTrack.track.album.images, requireContext(), binding.modalCorrectImage)
        binding.modalCorrectText.text =
            getString(R.string.num_streams, String.format("%,d", correctTrack.playCount))

        glideShowImage(wrongTrack.track.album.images, requireContext(), binding.modalWrongImage)
        binding.modalWrongText.text =
            getString(R.string.num_streams, String.format("%,d", wrongTrack.playCount))

        binding.modalCl.visibility = View.VISIBLE
    }

    private fun hideModal(binding: HighLowGameFragment3Binding) {
        enableAnswerButtons(binding)
        binding.modalCl.visibility = View.GONE
    }

    private fun showQuestion(binding: HighLowGameFragment3Binding, question: HighLowQuestion) {
        val track1 = question.track1.track
        val track2 = question.track2.track

//        glideShowImageLoadAnim(track1.album.images, requireContext(), binding.imageAns1)
        hlShowImage1(track1.album.images, requireContext(), binding)
        binding.artistAns1.text = track1.artists[0].name
        binding.songAns1.text = track1.name

//        glideShowImageLoadAnim(track2.album.images, requireContext(), binding.imageAns2)
        hlShowImage2(track2.album.images, requireContext(), binding)
        binding.artistAns2.text = track2.artists[0].name
        binding.songAns2.text = track2.name
    }

    private fun disableAnswerButtons(binding: HighLowGameFragment3Binding) {
        binding.cvAns1.isClickable = false
        binding.cvAns2.isClickable = false
    }

    private fun enableAnswerButtons(binding: HighLowGameFragment3Binding) {
        binding.cvAns1.isClickable = true
        binding.cvAns2.isClickable = true
    }


}