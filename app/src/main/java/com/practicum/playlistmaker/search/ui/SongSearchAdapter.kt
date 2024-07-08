package com.practicum.playlistmaker.search.ui

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.data.repository.SearchHistory
import com.practicum.playlistmaker.search.models.Track

class SongSearchAdapter(
    private val tracks: List<Track>,
    private val listener: Listener
) :
    RecyclerView.Adapter<SongSearchViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return SongSearchViewHolder(view, listener)
    }

    override fun onBindViewHolder(holder: SongSearchViewHolder, position: Int) {
        holder.bind(tracks[position])
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    interface Listener {
        fun onClick(track: Track)
    }

}