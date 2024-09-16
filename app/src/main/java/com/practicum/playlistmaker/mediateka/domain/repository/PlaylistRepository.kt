package com.practicum.playlistmaker.mediateka.domain.repository

import com.practicum.playlistmaker.mediateka.models.Playlist

interface PlaylistRepository {

    fun addPlaylist(playlist: Playlist)
    fun getListOfPlaylists(): List<Playlist>

}