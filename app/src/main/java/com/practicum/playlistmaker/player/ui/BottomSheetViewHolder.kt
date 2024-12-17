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

class BottomSheetViewHolder(itemView: View, private val listener: BottomSheetAdapter.Listener) : RecyclerView.ViewHolder(itemView) {

    private val playlistImage: ImageView = itemView.findViewById(R.id.playlistImageBS)
    private val playlistName: TextView = itemView.findViewById(R.id.playlistNameBS)
    private val playlistNumberOfTracks: TextView = itemView.findViewById(R.id.numberOfTracksBS)

    fun bind(playlist: Playlist) {
        Glide.with(itemView).load(playlist.image?.toUri())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, itemView.context)))
            .into(playlistImage)
        playlistName.text = playlist.name
        playlistNumberOfTracks.text = getTrackCountText(playlist.tracks.size)

        itemView.setOnClickListener {
            listener.onClick(playlist)
        }


    }

    private fun getTrackCountText(count: Int): String {
        val word = when {
            count % 10 == 1 && count % 100 != 11 -> "трек"     // Окончание для 1 (но не 11)
            count % 10 in 2..4 && count % 100 !in 12..14 -> "трека" // Окончание для 2, 3, 4 (но не 12-14)
            else -> "треков"                                  // Окончание для всех остальных чисел
        }
        return "$count $word"
    }
}