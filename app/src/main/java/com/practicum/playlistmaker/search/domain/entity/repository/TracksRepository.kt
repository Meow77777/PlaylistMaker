package com.practicum.playlistmaker.search.domain.entity.repository

import com.practicum.playlistmaker.search.domain.entity.Resource
import com.practicum.playlistmaker.search.models.Track

interface TracksRepository {
    fun searchTracks(expression : String) : Resource<List<Track>>
    fun addTrackInHistory(track: Track)
    fun getTracksList() : MutableList<Track>
}