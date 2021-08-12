package com.tom.spotifygamev3.higher_lower_game.playlist_picker

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.SourcePickerFragmentBinding

class SourcePickerFragment : Fragment() {

    private val TAG = "SourcePickerFragment"

    private val viewModel: SourcePickerViewModel by lazy {
        ViewModelProvider(this).get(SourcePickerViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SourcePickerFragmentBinding.inflate(inflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.navigateToGame.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(R.id.action_sourcePickerFragment_to_highLowGameFragment)
                viewModel.onAdvanceComplete()
            }
        })

        return binding.root
    }

}