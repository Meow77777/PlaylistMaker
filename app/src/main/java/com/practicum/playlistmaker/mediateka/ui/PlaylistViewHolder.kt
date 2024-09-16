package com.practicum.playlistmaker.mediateka.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.utils.DateTimeUtil

class PlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistImage: ImageView
    private val playlistName: TextView
    private val playlistNumberOfTracks: TextView

    init {
        playlistImage = itemView.findViewById(R.id.playlistImage)
        playlistName = itemView.findViewById(R.id.playlistName)
        playlistNumberOfTracks = itemView.findViewById(R.id.numberOfTracks)
    }

    fun bind(playlist: Playlist) {
        Glide.with(itemView).load(playlist.image).centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(2f, itemView.context)))
            .into(playlistImage)
        playlistName.text = playlist.name
        playlistNumberOfTracks.text = playlist.numberOfTracks.toString()

//        itemView.setOnClickListener {
//            clickListener.onClick(track = model)
//        }

    }

}