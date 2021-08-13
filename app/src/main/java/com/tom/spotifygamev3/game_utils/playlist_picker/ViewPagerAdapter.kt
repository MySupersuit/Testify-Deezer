package com.tom.spotifygamev3.game_utils.playlist_picker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.tom.spotifygamev3.BlankFragment
import com.tom.spotifygamev3.home.HomeFragment

class ViewPagerAdapter(fragment: Fragment) :
        FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {

        val fragment = PlaylistPickerFragment()
        fragment.arguments = Bundle().apply {
            putInt("param1", position+1)
        }
        return fragment
    }
}