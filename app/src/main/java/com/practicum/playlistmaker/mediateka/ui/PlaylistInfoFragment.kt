package com.practicum.playlistmaker.mediateka.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistInfoFragmentBinding
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class PlaylistInfoFragment : Fragment() {

    private lateinit var binding: PlaylistInfoFragmentBinding
    private lateinit var playlist: Playlist
    private var isClickAllowed = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = PlaylistInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlist = arguments?.getParcelable<Playlist>("playlist")!!
        showPlaylistDetails(playlist)

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.standardBottomSheet)

//        bottomSheetBehavior.addBottomSheetCallback(object :
//            BottomSheetBehavior.BottomSheetCallback() {
//            override fun onStateChanged(bottomSheet: View, newState: Int) {
//                    if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED // Обязательно держим его открытым
//                    }
//            }
//
//            override fun onSlide(bottomSheet: View, slideOffset: Float) {
//
//            }
//        })

        binding.backFromPlaylistInfo.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun showPlaylistDetails(playlist: Playlist) {
        loadImageOrPlaceholder(requireContext(), binding.PlaylistImage, playlist.image)
        binding.PlaylistName.text = playlist.name
        binding.PlaylistDescription.text = playlist.description
        binding.sumLength.text = getTotalDurationString(playlist.tracks)
        binding.tracksCount.text = getTracksCountString(playlist.tracks)
        binding.playlistTracksRecyclerBS.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.playlistTracksRecyclerBS.adapter =
            BottomSheetAdapterTracks(playlist.tracks, object : BottomSheetAdapterTracks.Listener {
                override fun onClick(track: Track) {
                    if (clickDebounce()) {
                        val bundle = Bundle().apply {
                            putParcelable("track", track)
                        }
                        findNavController().navigate(
                            R.id.trackInfoFragment, bundle
                        )
                    }
                }

            })
    }

    private fun loadImageOrPlaceholder(context: Context, imageView: ImageView, imageUri: String?) {
        val placeholderRes = R.drawable.placeholder
            Glide.with(context)
                .load(playlist.image?.toUri())
                .placeholder(placeholderRes)
                .into(imageView)

    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(1000L)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun getTracksCountString(tracks: List<Track>): String {
        val tracksCount = tracks.size

        // Применяем правила склонения для слова "трек"
        val tracksWord = when {
            tracksCount % 10 == 1 && tracksCount % 100 != 11 -> "трек"  // 1 трек
            tracksCount % 10 in 2..4 && (tracksCount % 100 !in 12..14) -> "трека"  // 2-4 трека
            else -> "треков"  // 5 и более треков
        }

        // Форматируем строку для отображения
        return "$tracksCount $tracksWord"
    }

    private fun getTotalDurationString(tracks: List<Track>): String {
        // Суммируем продолжительности всех треков (преобразуем строку в Long)
        val totalDurationMillis = tracks.sumOf { it.trackTimeMillis?.toLongOrNull() ?: 0L }

        val totalMinutes = totalDurationMillis / 60000

        val minutesWord = when {
            (totalMinutes % 10).toInt() == 1 && (totalMinutes % 100).toInt() != 11 -> "минута"  // 1 минута
            totalMinutes % 10 in 2..4 && (totalMinutes % 100 !in 12..14) -> "минуты"  // 2-4 минуты
            else -> "минут"  // 5 и более минут
        }

        // Форматируем строку для отображения
        return "$totalMinutes $minutesWord"
    }

}
