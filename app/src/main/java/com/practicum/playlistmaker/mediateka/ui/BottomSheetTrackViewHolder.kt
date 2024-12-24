package com.practicum.playlistmaker.mediateka.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.net.toUri
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.player.ui.BottomSheetAdapter
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil

class BottomSheetTrackViewHolder(itemView: View, private val listener: BottomSheetAdapterTracks.Listener) : RecyclerView.ViewHolder(itemView) {

    private val trackNameView: TextView
    private val artistNameView: TextView
    private val trackTimeView: TextView
    private val artworkUrl100View: ImageView

    init {
        trackNameView = itemView.findViewById(R.id.SongNameList)
        artistNameView = itemView.findViewById(R.id.SongAuthorList)
        trackTimeView = itemView.findViewById(R.id.SongTimeList)
        artworkUrl100View = itemView.findViewById(R.id.SongImageList)
    }

    fun bind(track: Track) {
        trackNameView.text = track.trackName
        artistNameView.text = track.artistName
        trackTimeView.text = DateTimeUtil.timeConvert(track.trackTimeMillis!!.toLong())
        Glide.with(itemView).load(track.artworkUrl100).centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(2f, itemView.context)))
            .into(artworkUrl100View)

        itemView.setOnClickListener {
            listener.onClick(track = track)
        }

    }
}