package com.tom.spotifygamev3.higher_lower_game.game

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.HighLowGameFragmentBinding

class HighLowGameFragment : Fragment() {

    private val viewModel: HighLowGameViewModel by lazy {
        ViewModelProvider(this).get(HighLowGameViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = HighLowGameFragmentBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.lfmTrack.observe(viewLifecycleOwner, Observer { track ->
            binding.tv.text = "${track.name} has ${track.playCount} plays"

        })

        return binding.root
    }


}