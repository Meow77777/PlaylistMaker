package com.practicum.playlistmaker.search.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.models.Track

class AdapterHistoryTracks(
    private val tracksHistory: MutableList<Track>,
    private val listener: SongSearchAdapter.Listener
) :
    RecyclerView.Adapter<SongSearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return SongSearchViewHolder(view,listener)
    }

    override fun onBindViewHolder(holder: SongSearchViewHolder, position: Int) {
        holder.bind(tracksHistory[position])

    }

    override fun getItemCount(): Int {
        return tracksHistory.size
    }
}