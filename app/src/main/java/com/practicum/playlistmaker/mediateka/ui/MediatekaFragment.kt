package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentMediatekaBinding

class MediatekaFragment : Fragment() {

    private lateinit var binding: FragmentMediatekaBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMediatekaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listOfFragments = initFragments()
        val listOfFragmentsTitles = initTitles()

        val adapter = ViewPagerAdapter(this, requireContext(), listOfFragments)

        binding.mediatekaViewPager2.adapter = adapter

        TabLayoutMediator(binding.tabLayout, binding.mediatekaViewPager2) { tab, pos ->
            tab.text = listOfFragmentsTitles[pos]
        }.attach()

    }

    private fun initFragments(): List<Fragment> {
        return listOf(
            FragmentLikedTracks.newInstance(),
            FragmentPlaylists.newInstance()
        )
    }

    private fun initTitles(): List<String> {
        return listOf(
            getString(R.string.likedTracks),
            getString(R.string.playlists)
        )
    }

}