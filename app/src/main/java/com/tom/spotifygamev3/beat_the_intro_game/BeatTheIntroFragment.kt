package com.tom.spotifygamev3.beat_the_intro_game

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
import java.lang.Exception

class BeatTheIntroFragment : Fragment() {

    private val TAG = "BeatTheIntroFragment"

    private val viewModel: BeatTheIntroViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[BeatTheIntroViewModel::class.java]
    }

    private lateinit var viewModelFactory: BeatTheIntroViewModelFactory

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var preloadMp: CustomMediaPlayer
    private lateinit var customMp: CustomMediaPlayer
    private lateinit var mps: MutableList<CustomMediaPlayer>
    private var exitProgressThread = false
    private var localScore = 0
    private var questionIndex = 0
    private var nextQIndex = 0

    private var nextTrackPrepared = false

    private val _nextTrackPrepped = MutableLiveData<Boolean>()
    private val nextTrackPrepped: LiveData<Boolean>
        get() = _nextTrackPrepped

    private val _currentQReady = MutableLiveData<CustomMediaPlayer>()
    var currentQuestionUrl = ""

    private val trackPrepStatus: HashMap<String, Boolean> = HashMap()

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

        // set up media player
        // mediaplayer may already be set up
        // breaking mvvm model here

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .build()
            )
        }

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

        mediaPlayer.setOnPreparedListener {
            Log.d(TAG, it.trackInfo[0].toString())
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
                setupProgressBar(binding, mps[questionIndex % 2])
                Log.d(TAG, "starting")
                player.start()
                questionIndex++
            }
        })

        viewModel.nextQuestion.observe(viewLifecycleOwner, Observer { nextQ ->
            val mp = mps[nextQIndex % mps.size]
            nextQIndex++
            Log.d(TAG, "next q index $nextQIndex")
            if (nextQ == null) {
                Log.d(TAG, "next null")
                stopPlayer(mp)
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
                Log.d(TAG, "current q index " + (questionIndex % 2).toString())
                _currentQReady.value = mps[questionIndex % 2]
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

        viewModel.score.observe(viewLifecycleOwner, Observer { newScore ->
            if (newScore != 0) {
                // TODO change animation to be +$score or -$score in green and red
                if (newScore > localScore) doAlphaAnimation(binding.beatIntroCheckmark)
                else if (newScore < localScore) doAlphaAnimation(binding.beatIntroCross)
            }
                binding.beatIntroScoreCounter.text =
                    newScore.toString()
                localScore = newScore

        })

        viewModel.eventGameFinish.observe(viewLifecycleOwner, Observer { hasFinished ->
            if (hasFinished) gameFinished()
        })

        return binding.root
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
//        binding.progressBar.max = mediaPlayer.duration
        binding.progressBar.max = mp.duration
        binding.progressBar.progress = 0

        // let viewmodel know player duration so it can calculate scores
//        viewModel.playerDuration = mediaPlayer.duration
        viewModel.playerDuration = mp.duration

        exitProgressThread = false
        startProgressThread(binding, mp)
    }

    // TODO if preview plays all the way through, progress does not reset
    private fun startProgressThread(binding: BeatTheIntroFragmentBinding, mp: CustomMediaPlayer) {
        Thread {
            // update progress bar until told to exit or reaches end of audio file
            while (!exitProgressThread && binding.progressBar.progress < mp.duration) {
                binding.progressBar.progress = mp.currentPosition
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

    private fun playTrack(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            // switch to prepareAsyc() and onPrepare
            prepare()
            start()
        }
    }

    // TODO work out async implementation
    // See if stuff can go in the viewmodel or not 
    private fun playTrackAsync(url: String) {
        mediaPlayer.apply {
            setDataSource(url)
            setOnPreparedListener {
                mediaPlayer.start()
            }
            prepareAsync()
            viewModel._mpStatus.value = MediaPlayerStatus.PREPARING
        }
    }

    override fun onPause() {
        super.onPause()
        exitProgressThread = true
        stopPlayers()

    }

    private fun changeMediaUrl(url: String) {
//        if (mediaPlayer.isPlaying) mediaPlayer.stop()
        mediaPlayer.reset()
        playTrack(url)
    }

    private fun stopPlayer(mp: CustomMediaPlayer) {
        mp.reset()
        mp.release()
//        mediaPlayer.stop()
//        mediaPlayer.reset()
//        mediaPlayer.release()
    }

    private fun stopPlayers() {
        for (mp in mps) {
            stopPlayer(mp)
        }
    }

}