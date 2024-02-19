package com.practicum.playlistmaker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SongSearchAdapter(private val tracks: List<Track>) :
    RecyclerView.Adapter<SongSearchViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return SongSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongSearchViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

}