package com.tom.spotifygamev3.home

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = HomeFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        viewModel.navigateToAlbumGame.observe(viewLifecycleOwner, Observer<Boolean> {navigate ->
            if (navigate) {
                val navController = findNavController()
                navController.navigate(R.id.action_homeFragment_to_playlistPickerFragment)
                viewModel.onNavigateToAlbumGame()
            }
        })

        viewModel.navigateToHighLow.observe(viewLifecycleOwner, Observer<Boolean> { navigate ->
            if (navigate) {
                val navController = findNavController()
                navController.navigate(R.id.action_homeFragment_to_sourcePickerFragment)
                viewModel.onNavigateToHighLow()
            }
        })

        return binding.root

    }

}