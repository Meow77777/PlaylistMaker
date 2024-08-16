package com.practicum.playlistmaker.mediateka.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentLikedTracksBinding
import com.practicum.playlistmaker.mediateka.presentation.FragmentLikedTracksViewModel
import com.practicum.playlistmaker.player.ui.TrackInfoActivity
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.ui.SongSearchAdapter
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentLikedTracks : Fragment() {
    private lateinit var binding: FragmentLikedTracksBinding
    private var isClickAllowed = true
    private lateinit var recycler: RecyclerView
    private lateinit var noLikedTracksImage: ImageView
    private lateinit var noLikedTracksText: TextView

    private lateinit var adapter: SongSearchAdapter

    private val vm by viewModel<FragmentLikedTracksViewModel>()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = binding.likedTracksRecycler
        noLikedTracksImage = binding.mediatekaIsEmptyImage
        noLikedTracksText = binding.mediatekaIsEmptyText

        recycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        vm.getLikedTracksLiveData().observe(viewLifecycleOwner) { tracks ->
            if (tracks.isEmpty()) {
                showPlaceholders()
                hideTracks()
            } else {
                hidePlaceholders()
                notifyTracksAdapter(tracks)
                showTracks()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        vm.getTracks()
    }

    private fun notifyTracksAdapter(tracks: List<Track>) {
        adapter = SongSearchAdapter(tracks, object : SongSearchAdapter.Listener {
            override fun onClick(track: Track) {
                if (clickDebounce()) {
                    val trackInfoActivityIntent =
                        Intent(requireContext(), TrackInfoActivity::class.java)
                    trackInfoActivityIntent.putExtra("track", track)
                    startActivity(trackInfoActivityIntent)
                }
            }
        })
        recycler.adapter = adapter
        adapter.notifyDataSetChanged()
    }

    private fun showPlaceholders() {
        noLikedTracksImage.visibility = View.VISIBLE
        noLikedTracksText.visibility = View.VISIBLE
    }

    private fun hidePlaceholders() {
        noLikedTracksImage.visibility = View.GONE
        noLikedTracksText.visibility = View.GONE
    }

    private fun showTracks() {
        recycler.visibility = View.VISIBLE
    }

    private fun hideTracks() {
        recycler.visibility = View.GONE
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentLikedTracks()
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            viewLifecycleOwner.lifecycleScope.launch {
                delay(1000L)
                isClickAllowed = true
            }
        }
        return current
    }

}