package com.practicum.playlistmaker.search.data.repository

import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.dto.TrackResponse
import com.practicum.playlistmaker.search.data.dto.TracksSearchRequest
import com.practicum.playlistmaker.search.domain.entity.Resource
import com.practicum.playlistmaker.search.domain.entity.repository.TracksRepository
import com.practicum.playlistmaker.search.models.Track

class TracksRepositoryImpl(private val networkClient: NetworkClient, private val searchHistory: SearchHistory) : TracksRepository {
    override fun searchTracks(expression: String): Resource<List<Track>> {
        val response = networkClient.doRequest(TracksSearchRequest(expression))
        return when (response.resultCode) {
            -1 -> Resource.Error("Проверьте подключение к интернету")
            200 -> {
                val stored = searchHistory.historyList
                Resource.Success((response as TrackResponse).results.map {
                    Track(
                        it.trackName,
                        it.artistName,
                        it.trackTimeMillis,
                        it.artworkUrl100,
                        it.collectionName,
                        it.releaseDate,
                        it.primaryGenreName,
                        it.country,
                        it.previewUrl,
                        isInHistory = stored.contains(it)
                    )
                })
            }
            else -> {
                Resource.Error("Ошибка сервера")
            }
        }
    }

    override fun addTrackInHistory(track: Track) {
        searchHistory.addTrack(track = track)
    }

    override fun getTracksList() : MutableList<Track>{
        return searchHistory.getTracksList()
    }

}