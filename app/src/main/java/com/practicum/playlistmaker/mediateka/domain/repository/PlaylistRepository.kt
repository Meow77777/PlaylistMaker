package com.practicum.playlistmaker.mediateka.domain.repository

import com.practicum.playlistmaker.mediateka.models.Playlist

interface PlaylistRepository {

    suspend fun addPlaylist(playlist: Playlist)
    suspend fun getPlaylists(): List<Playlist>
    suspend fun updatePlaylist(playlist: Playlist)
}