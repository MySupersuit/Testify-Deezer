package com.tom.spotifygamev3.higher_lower_game

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.Utils.Utils.glideShowImage
import com.tom.spotifygamev3.Utils.Constants
import com.tom.spotifygamev3.Utils.Utils.glidePreloadImage
import com.tom.spotifygamev3.Utils.Utils.glideShowImageLoadAnim
import com.tom.spotifygamev3.databinding.HighLowGameFragmentBinding
import com.tom.spotifygamev3.higher_lower_game.game.HighLowGameFragmentArgs
import com.tom.spotifygamev3.higher_lower_game.game.HighLowGameFragmentDirections
import com.tom.spotifygamev3.models.HighLowQuestion

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
        val binding = HighLowGameFragmentBinding.inflate(inflater)
        viewModelFactory =
            HighLowGameViewModelFactory(
                requireActivity().application,
                HighLowGameFragmentArgs.fromBundle(requireArguments()).playlistId
            )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        // TODO grab artist images from API call
        // TODO make HighLowQuestion model and make them in view model
        viewModel.currentQuestion.observe(viewLifecycleOwner, Observer { question ->
             showQuestion(binding, question)
        })

        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQuestion ->
            if (nextQuestion != null) {
                glidePreloadImage(nextQuestion.track1.track.album.images, requireContext())
                glidePreloadImage(nextQuestion.track2.track.album.images, requireContext())
            }
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
                score = viewModel.score.value ?: 0,
                numQuestions = Constants.HIGH_LOW_NUM_QUESTIONS,
                gameType = Constants.HIGH_LOW_GAME_TYPE
            )
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }

    private fun showModal(binding: HighLowGameFragmentBinding, question: HighLowQuestion) {
        binding.modalTitle.text = if (question.correct == true) "Correct :)" else "Wrong :("

        val correctTrack =
            if (question.track1.playCount > question.track2.playCount) question.track1 else question.track2
        val wrongTrack =
            if (question.track1.playCount > question.track2.playCount) question.track2 else question.track1

        glideShowImage(correctTrack.track.album.images, requireContext(), binding.modalCorrectImage)
        binding.modalCorrectText.text = getString(R.string.num_streams, String.format("%,d", correctTrack.playCount))

        glideShowImage(wrongTrack.track.album.images, requireContext(), binding.modalWrongImage)
        binding.modalWrongText.text = getString(R.string.num_streams, String.format("%,d", wrongTrack.playCount))

        binding.modalCl.visibility = View.VISIBLE
    }

    private fun hideModal(binding: HighLowGameFragmentBinding) {
        binding.modalCl.visibility = View.GONE
    }

    private fun showQuestion(binding: HighLowGameFragmentBinding, question: HighLowQuestion) {
        val track1 = question.track1.track
        val track2 = question.track2.track

        glideShowImageLoadAnim(track1.album.images, requireContext(), binding.imageAns1)
        binding.artistAns1.text = track1.artists[0].name
        binding.songAns1.text = track1.name

        glideShowImageLoadAnim(track2.album.images, requireContext(), binding.imageAns2)
        binding.artistAns2.text = track2.artists[0].name
        binding.songAns2.text = track2.name
    }


}