package com.practicum.playlistmaker.mediateka.data.repository

import com.practicum.playlistmaker.mediateka.data.db.PlaylistDataBase
import com.practicum.playlistmaker.mediateka.data.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.mediateka.models.Playlist

class PlaylistRepositoryImpl(
    private val playlistDataBase: PlaylistDataBase,
    private val converter: PlaylistDbConverter
) : PlaylistRepository {
    override fun addPlaylist(playlist: Playlist) {
        playlistDataBase.trackDao().insertPlaylist(converter.mapPlaylistToPlaylistEntity(playlist))
    }

    override fun getListOfPlaylists(): List<Playlist> {
        TODO("Not yet implemented")
    }


}