package com.practicum.playlistmaker.mediateka.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.player.ui.BottomSheetViewHolder
import com.practicum.playlistmaker.search.models.Track

class BottomSheetAdapterTracks(private val tracks: List<Track>, private val listener: Listener) :
    RecyclerView.Adapter<BottomSheetTrackViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BottomSheetTrackViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_element, parent, false)
        return BottomSheetTrackViewHolder(view, listener)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: BottomSheetTrackViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    interface Listener {
        fun onClick(track: Track)
        fun onLongClick(track: Track)
    }
}