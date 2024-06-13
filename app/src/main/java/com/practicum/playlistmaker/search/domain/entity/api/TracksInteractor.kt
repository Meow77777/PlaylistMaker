package com.practicum.playlistmaker.search.domain.entity.api

import com.practicum.playlistmaker.search.models.Track

interface TracksInteractor {
    fun searchTracks(expression: String, consumer: TracksConsumer)

    fun addTrackInHistory(track: Track)
    fun getTracksList() : MutableList<Track>

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?, errorMessage: String?)
    }
}