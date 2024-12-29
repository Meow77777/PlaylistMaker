package com.practicum.playlistmaker.mediateka.domain.api

import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.search.models.Track

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun getPlaylists() : List<Playlist>
    suspend fun updatePlaylist(playlist: Playlist)
    suspend fun deletePlaylist(playlist: Playlist)
}