package com.practicum.playlistmaker.search.domain.entity.repository

import com.practicum.playlistmaker.search.domain.entity.Resource
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow

interface TracksRepository {
    fun searchTracks(expression : String) : Flow<Resource<List<Track>>>
    fun addTrackInHistory(track: Track)
    fun getTracksList() : MutableList<Track>
    fun clearTracksHistory()
}