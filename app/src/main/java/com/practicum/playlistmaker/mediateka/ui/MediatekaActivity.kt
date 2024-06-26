package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMediatekaBinding

class MediatekaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMediatekaBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMediatekaBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val listOfFragments = initFragments()
        val listOfFragmentsTitles = initTitles()

        val adapter = ViewPagerAdapter(this, listOfFragments)

        binding.mediatekaViewPager2.adapter = adapter


        TabLayoutMediator(binding.tabLayout, binding.mediatekaViewPager2) { tab, pos ->
            tab.text = listOfFragmentsTitles[pos]
        }.attach()

        binding.mediatekaBackButton.setOnClickListener {
            finish()
        }


    }

    private fun initFragments(): List<Fragment> {
        return listOf(
            FragmentLikedTracks.newInstance(),
            FragmentPlaylists.newInstance()
        )
    }

    private fun initTitles(): List<String> {
        return listOf(
            application.getString(R.string.likedTracks),
            application.getString(R.string.playlists)
        )
    }

}