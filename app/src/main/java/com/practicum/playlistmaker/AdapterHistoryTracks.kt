package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class AdapterHistoryTracks(
    private val tracksHistory: MutableList<Track>,
    val listener: SongSearchAdapter.Listener
) :
    RecyclerView.Adapter<SongSearchViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return SongSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongSearchViewHolder, position: Int) {
        holder.bind(tracksHistory[position], listener)

    }

    override fun getItemCount(): Int {
        return tracksHistory.size
    }
}