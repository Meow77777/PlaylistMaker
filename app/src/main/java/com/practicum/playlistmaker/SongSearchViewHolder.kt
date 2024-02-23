package com.practicum.playlistmaker

import android.content.Context
import android.util.TypedValue
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners

class SongSearchViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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


    fun bind(model: Track) {
        trackNameView.text = model.trackName
        artistNameView.text = model.artistName
        trackTimeView.text = DateTimeUtil.timeConvert(model.trackTimeMillis.toLong())
        Glide.with(itemView).load(model.artworkUrl100).centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(dpToPx(2f, itemView.context)))
            .into(artworkUrl100View)

    }

    private fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }


}