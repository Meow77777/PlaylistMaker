package com.practicum.playlistmaker.mediateka.domain.api

import com.practicum.playlistmaker.mediateka.models.Playlist

interface PlaylistInteractor {
    suspend fun addPlaylist(playlist: Playlist)
    suspend fun getPlaylists() : List<Playlist>
}