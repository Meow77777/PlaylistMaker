package com.practicum.playlistmaker.presentation.mapper

import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.models.TrackDetailsInfo
import java.text.SimpleDateFormat
import java.util.Locale

object TrackDetailsMapper {
    fun map(track: Track): TrackDetailsInfo {
        return TrackDetailsInfo(
            trackName = track.trackName,
            artistName = track.artistName,
            trackTimeMillis = trackTimeConvert(track.trackTimeMillis!!.toLong()),
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )


    }

    private fun trackTimeConvert(time: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

}
