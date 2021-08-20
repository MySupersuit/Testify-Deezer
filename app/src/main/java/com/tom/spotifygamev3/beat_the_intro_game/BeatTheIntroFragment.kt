package com.tom.spotifygamev3.beat_the_intro_game

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.tom.spotifygamev3.R

class BeatTheIntroFragment : Fragment() {

    companion object {
        fun newInstance() = BeatTheIntroFragment()
    }

    private lateinit var viewModel: BeatTheIntroViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.beat_the_intro_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(BeatTheIntroViewModel::class.java)
        // TODO: Use the ViewModel
    }

}