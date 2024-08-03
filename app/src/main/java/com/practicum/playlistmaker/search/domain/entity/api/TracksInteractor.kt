package com.practicum.playlistmaker.search.domain.entity.api

import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksInteractor {
    fun searchTracks(expression: String): Flow<Pair<List<Track>?, String?>>

    fun addTrackInHistory(track: Track)
    fun getTracksList() : MutableList<Track>

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }

    fun clearTracksHistory()
}