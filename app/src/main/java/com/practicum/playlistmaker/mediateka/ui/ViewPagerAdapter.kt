package com.practicum.playlistmaker.mediateka.ui

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragment: Fragment,
    context: Context,
    private val listOfFragments: List<Fragment>
) :
    FragmentStateAdapter(fragment) {
    override fun getItemCount(): Int {
        return listOfFragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return listOfFragments[position]
    }
}