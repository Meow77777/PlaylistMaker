package com.practicum.playlistmaker.player.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.utils.DateTimeUtil

class BottomSheetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImageBS)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistNameBS)
    private val playlistNumberOfTracks: TextView = itemView.findViewById(R.id.numberOfTracksBS)

    fun bind(playlist: Playlist) {
        Glide.with(itemView).load(playlist.image.toUri())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, itemView.context)))
            .into(playlistImage)
        playlistName.text = playlist.name
        playlistNumberOfTracks.text = playlist.tracks.size.toString()

//        itemView.setOnClickListener {
//            clickListener.onClick(track = model)
//        }

    }
}