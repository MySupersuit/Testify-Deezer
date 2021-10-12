package com.tom.deezergame.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.allViews
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.spotify.sdk.android.auth.AuthorizationClient
import com.tom.deezergame.LoginActivity
import com.tom.deezergame.R
import com.tom.deezergame.databinding.ChooseGameFragmentBinding
import com.tom.deezergame.utils.Constants

class HomeFragment : Fragment() {

    private val viewModel: HomeViewModel by lazy {
        ViewModelProvider(this).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = ChooseGameFragmentBinding.inflate(inflater)

        binding.lifecycleOwner = this

        binding.viewModel = viewModel

        if (!isSignedIn()) {
            binding.logOutButton.text = getString(R.string.login)
        } else {
            binding.logOutButton.text = getString(R.string.log_out)
        }

        viewModel.navigateToAlbumGame.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                        Constants.ALBUM_GAME_TYPE
                    )
                )
                viewModel.onNavigateToAlbumGame()
            }
        })

        viewModel.navigateToHighLow.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                        Constants.HIGH_LOW_GAME_TYPE
                    )
                )
                viewModel.onNavigateToHighLow()
            }
        })

        viewModel.navigateToBeatIntro.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToPlaylistPickerFragment(
                        Constants.BEAT_INTRO_GAME_TYPE
                    )
                )
                viewModel.onNavigateToBeatIntro()
            }
        })

        viewModel.logOut.observe(viewLifecycleOwner, Observer { navigate ->
            if (navigate) {
                val googleClient = GoogleSignIn.getClient(
                    requireActivity(),
                    GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN
                )
                googleClient.signOut().addOnCompleteListener {
                    val intent = Intent(requireActivity(), LoginActivity::class.java)
                    startActivity(intent)
                }

            }
        })

        viewModel.infoClick.observe(viewLifecycleOwner, { click ->
            val message = SpannableString(getString(R.string.info_content))
            Linkify.addLinks(message, Linkify.ALL)
            if (click) {
                val d = AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                    .setTitle("Info")
                    .setMessage(message)
                    .setIcon(R.mipmap.testify_deezer)
                    .show()
                d.findViewById<TextView>(android.R.id.message).movementMethod = LinkMovementMethod.getInstance()
                viewModel.onInfoEndClick()
            }

        })

        return binding.root

    }

    private fun showSignOutSnackbar(binding: ChooseGameFragmentBinding) {
        val green = ContextCompat.getColor(requireContext(), R.color.spotify_green)
        val white = ContextCompat.getColor(requireContext(), R.color.spotify_white)
        val snackbar = Snackbar.make(
            binding.main,
            "Signed Out!",
            Snackbar.LENGTH_SHORT
        )
        snackbar.setActionTextColor(white)
        for (view in snackbar.view.allViews) {
            view.setBackgroundColor(green)
        }
        snackbar.show()
    }

    private fun isSignedIn() : Boolean {
        return GoogleSignIn.getLastSignedInAccount(requireContext()) != null
    }

}