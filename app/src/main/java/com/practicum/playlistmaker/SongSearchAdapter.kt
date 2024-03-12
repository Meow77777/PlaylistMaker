package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer.TrackInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class SongSearchAdapter(
    private val tracks: List<Track>,
    sharedPreferences: SharedPreferences,
    val listener: Listener
) :
    RecyclerView.Adapter<SongSearchViewHolder>() {
    val searchHistory: SearchHistory = SearchHistory(sharedPreferences)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongSearchViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_element, parent, false)
        return SongSearchViewHolder(view)
    }

    override fun onBindViewHolder(holder: SongSearchViewHolder, position: Int) {
        holder.bind(tracks[position],listener)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    interface Listener {
        fun onClick(track: Track)

    }

}