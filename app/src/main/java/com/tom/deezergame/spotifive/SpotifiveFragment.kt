package com.tom.deezergame.spotifive

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import com.tom.deezergame.R
import com.tom.deezergame.databinding.SpotifiveFragmentBinding
import timber.log.Timber

class SpotifiveFragment : Fragment() {

    private val viewModel: SpotifiveViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[SpotifiveViewModel::class.java]
    }

    private lateinit var viewModelFactory: SpotifiveViewModelFactory
    private lateinit var handler: Handler
    private val TRIGGER_AUTO_COMPLETE = 100
    private val AUTO_COMPLETE_DELAY = 300

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = SpotifiveFragmentBinding.inflate(inflater)
        viewModelFactory =
            SpotifiveViewModelFactory(
                requireActivity().application,
                SpotifiveFragmentArgs.fromBundle(requireArguments()).artistId
            )
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        viewModel.searchResults.observe(viewLifecycleOwner, { trackList ->
            Timber.d("setting tracks")
            val adpt = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, trackList)
            binding.autoComplete.setAdapter(adpt)
        })

        // android auto complete text view custom
        // https://stackoverflow.com/questions/33047156/how-to-create-custom-baseadapter-for-autocompletetextview
//        val autoSuggestAdapter = AutoSuggestAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line)
//        binding.autoComplete.threshold = 2
//        binding.autoComplete.setAdapter(autoSuggestAdapter)
//        binding.autoComplete.setOnItemClickListener { parent, view, position, id ->
//            binding.text.text = autoSuggestAdapter.getObject(position)
//        }
//
//        binding.autoComplete.addTextChangedListener {
//
//        }
//
//        handler = Handler(Looper.getMainLooper(), Handler.Callback { msg ->
//            if (msg.what == TRIGGER_AUTO_COMPLETE) {
//                if (!TextUtils.isEmpty(binding.autoComplete.text)) {
//                    // Make api call
//                }
//            }
//            true
//        })

        return binding.root
    }


}