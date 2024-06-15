package com.practicum.playlistmaker.player.presentation

import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.player.models.TrackDetailsInfo
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
