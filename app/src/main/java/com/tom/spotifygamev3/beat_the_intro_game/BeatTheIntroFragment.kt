package com.tom.spotifygamev3.beat_the_intro_game

import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.media.AudioAttributes
import android.media.MediaPlayer
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.BeatTheIntroFragmentBinding
import com.tom.spotifygamev3.models.BeatIntroQuestion
import com.tom.spotifygamev3.utils.Constants
import com.tom.spotifygamev3.utils.CustomMediaPlayer
import com.tom.spotifygamev3.utils.Utils.doAlphaAnimation
import com.tom.spotifygamev3.utils.Utils.glidePreloadImage
import com.tom.spotifygamev3.utils.Utils.glideShowImage
import com.tom.spotifygamev3.utils.Utils.glideShowImagePaletteBtI
import kotlin.Exception

class BeatTheIntroFragment : Fragment() {

    private val TAG = "BeatTheIntroFragment"

    private val viewModel: BeatTheIntroViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[BeatTheIntroViewModel::class.java]
    }

    private lateinit var viewModelFactory: BeatTheIntroViewModelFactory

    private lateinit var preloadMp: CustomMediaPlayer
    private lateinit var customMp: CustomMediaPlayer
    private lateinit var mps: MutableList<CustomMediaPlayer>    // list of mediaplayers
    private var exitProgressThread = false
    private var localScore = 0
    private var questionIndex = 0               // index of song question to be shown
    private var nextQIndex = 0                  // index of the song question to be preloaded

    private val _currentQReady = MutableLiveData<CustomMediaPlayer>()
    var currentQuestionUrl = ""

    private val trackPrepStatus: HashMap<String, Boolean> = HashMap()
    // mapping from preview_url to whether it has been prepared by its mediaplayer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = BeatTheIntroFragmentBinding.inflate(inflater)
        viewModelFactory =
            BeatTheIntroViewModelFactory(
                requireActivity().application,
                BeatTheIntroFragmentArgs.fromBundle(requireArguments()).playlistId
            )
        binding.lifecycleOwner = this
        binding.viewModel = viewModel
        binding.loadingProgressCl.visibility = View.VISIBLE
        binding.beatIntroGameCl.visibility = View.GONE

        // setup two media players - one to preload while the other plays
        preloadMp = CustomMediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        customMp = CustomMediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

        mps = mutableListOf(customMp, preloadMp)

        preloadMp.setOnPreparedListener {
            Log.d(TAG, "preloadMp prep listener")
            val url = preloadMp.datasource!!
            trackPrepStatus[url] = true
            if (url == currentQuestionUrl) _currentQReady.value = preloadMp
        }

        customMp.setOnPreparedListener {
            Log.d(TAG, "customMp prep listener")
            val url = customMp.datasource!!
            trackPrepStatus[url] = true
            if (url == currentQuestionUrl) _currentQReady.value = customMp
        }

        _currentQReady.observe(viewLifecycleOwner, Observer { player ->
            if (player != null) {
                Log.d(TAG, "currentQReady")
                Log.d(TAG, player.toString())
                _currentQReady.value = null
                binding.loadingProgressCl.visibility = View.GONE
                binding.beatIntroGameCl.visibility = View.VISIBLE
                stopProgressThread()
                Log.d(TAG, "question index $questionIndex")
                setupProgressBar(binding, mps[questionIndex % mps.size])
                Log.d(TAG, "starting")
                player.start()
                questionIndex++
            }
        })

        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQ ->
            // slowed it down a lot
//            glidePreloadImage(nextQ.correctTrack.album.images, requireContext())

            val mp = mps[nextQIndex % mps.size]
            nextQIndex++
            Log.d(TAG, "next q index $nextQIndex")
            if (nextQ == null) {
                Log.d(TAG, "next null")
                stopReleasePlayer(mp)
                mps.remove(mp)
            } else {
                if (currentQuestionUrl == "") currentQuestionUrl = nextQ.previewUrl
                Log.d(TAG, "next Q")
                trackPrepStatus[nextQ.previewUrl] = false
                mp.reset()
                mp.setDataSource(nextQ.previewUrl)
                Log.d(TAG, "preparing async")
                mp.prepareAsync()
            }
        })

        // TODO figure out duration call errors
        // Tidy up a bit
        viewModel.currentQuestion.observe(viewLifecycleOwner, Observer { question ->
            binding.beatIntroGameCl.visibility = View.GONE
            binding.loadingProgressCl.visibility = View.VISIBLE
            currentQuestionUrl = question.previewUrl
            val prepped = trackPrepStatus[currentQuestionUrl]
            Log.d(TAG, "prepped $prepped")
            if (prepped == true) {
                Log.d(TAG, "current q index " + (questionIndex % mps.size).toString())
                _currentQReady.value = mps[questionIndex % mps.size]
            }
            showQuestion(binding, question)
        })


        viewModel.numTracksLoaded.observe(viewLifecycleOwner, Observer { numLoaded ->
            Log.d(TAG, numLoaded.toString())
            binding.beatIntroLoadingMessage.text = getString(
                R.string.loading_prog_message,
                numLoaded,
                Constants.BEAT_INTRO_NUM_QUESTIONS
            )
            // numLoaded only gets to 9 for some reason
            if (numLoaded >= Constants.BEAT_INTRO_NUM_QUESTIONS) {
                binding.beatIntroLoadingMessage.text = getString(R.string.buffering)
            }
        })

        viewModel.showModal.observe(viewLifecycleOwner, Observer { question ->
            if (question != null) {
                Log.d(TAG, "q index showmodal $questionIndex")
                // stop previous player
                stopPlayer(mps[(questionIndex - 1) % mps.size])
                showModal(binding, question)
            } else {
                hideModal(binding)
            }
        })

        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            binding.beatIntroScoreCounter.text =
                newScore.toString()
            localScore = newScore

        })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) gameFinished()
        })

        return binding.root
    }

    private fun stopPlayer(mp: CustomMediaPlayer) {
        mp.reset()
    }

    private fun showQuestion(binding: BeatTheIntroFragmentBinding, question: BeatIntroQuestion) {
        // Make buttons with no answer not clickable
        val buttons = listOf(
            binding.answer1, binding.answer2,
            binding.answer3, binding.answer4
        )
        for (i in question.allAnswers.indices) {
            buttons[i].text = question.allAnswers[i]
            buttons[i].isClickable = true
        }
        for (i in question.allAnswers.size..3) {
            buttons[i].text = ""
            buttons[i].isClickable = false
        }
    }

    private fun gameFinished() {

        val action = BeatTheIntroFragmentDirections.actionBeatTheIntroFragmentToGameScoreFragment(
            gameType = Constants.BEAT_INTRO_GAME_TYPE,
            score = (viewModel.score.value ?: 0).toString()
        )
        NavHostFragment.findNavController(this).navigate(action)
        viewModel.onGameFinishComplete()
    }

    private fun setupProgressBar(binding: BeatTheIntroFragmentBinding, mp: CustomMediaPlayer) {

        binding.progressBar.max = mp.duration
        binding.progressBar.setProgressCompat(0, true)

        // let viewmodel know player duration so it can calculate scores
        viewModel.playerDuration = mp.duration

        exitProgressThread = false
        startProgressThread(binding, mp)
    }

    // TODO if preview plays all the way through, progress does not reset
    private fun startProgressThread(binding: BeatTheIntroFragmentBinding, mp: CustomMediaPlayer) {
//        binding.progressBar.progress = mp.currentPosition
        Thread {
            // update progress bar until told to exit or reaches end of audio file
            while (!exitProgressThread && binding.progressBar.progress < mp.duration) {
                binding.progressBar.setProgressCompat(mp.currentPosition, true)
                viewModel.playerPosition = mp.currentPosition
                try {
                    Thread.sleep(15)
                } catch (e: Exception) {
                    Log.e(TAG, e.toString())
                }
            }
        }.start()
    }

    private fun stopProgressThread() {
        exitProgressThread = true
    }

    override fun onPause() {
        super.onPause()
        exitProgressThread = true
        stopPlayers()

    }

    private fun stopReleasePlayer(mp: CustomMediaPlayer) {
        mp.reset()
        mp.release()
    }

    private fun stopPlayers() {
        for (mp in mps) {
            stopReleasePlayer(mp)
        }
    }

    private fun showModal(binding: BeatTheIntroFragmentBinding, question: BeatIntroQuestion) {
        disableAnswerButtons(binding)

        // glide show image palette BtI
        glideShowImagePaletteBtI(
            question.correctTrack.album.images,
            requireContext(),
            binding.modalImage,
            binding.modalCl,
            binding.modalSong,
            binding.modalArtist,
            binding.modalTitle,
            binding.modalScoreUpdate,
            binding.modalButton
        )
        if (question.questionScore >= 0) {
            binding.modalTitle.text = getString(R.string.correct_smiley)
            binding.modalScoreUpdate.text = getString(R.string.pos_score, question.questionScore)
        } else {
            binding.modalTitle.text = getString(R.string.wrong_frowny)
            binding.modalScoreUpdate.text = question.questionScore.toString()
        }
        binding.modalArtist.text = question.correctTrack.artists[0].name
        binding.modalSong.text = question.correctTrack.name

        binding.modalCv.visibility = View.VISIBLE
    }

    private fun hideModal(binding: BeatTheIntroFragmentBinding) {
        enableAnswerButtons(binding)
        binding.modalCv.visibility = View.GONE
    }

    private fun disableAnswerButtons(binding: BeatTheIntroFragmentBinding) {
        val buttons = listOf(binding.answer1, binding.answer2, binding.answer3, binding.answer4)
        for (button in buttons) {
            button.isClickable = false
        }
    }

    private fun enableAnswerButtons(binding: BeatTheIntroFragmentBinding) {
        val buttons = listOf(binding.answer1, binding.answer2, binding.answer3, binding.answer4)
        for (button in buttons) {
            button.isClickable = true
        }
    }

}