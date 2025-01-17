package com.practicum.playlistmaker.mediateka.data.repository

import com.practicum.playlistmaker.mediateka.data.db.PlaylistDataBase
import com.practicum.playlistmaker.mediateka.data.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(
    private val playlistDataBase: PlaylistDataBase,
    private val converter: PlaylistDbConverter
) : PlaylistRepository {
    override suspend fun addPlaylist(playlist: Playlist) {
        val playlistEntity = converter.mapPlaylistToPlaylistEntity(playlist)
        playlistDataBase.trackDao().insertPlaylist(playlistEntity)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        val playlistEntityList = playlistDataBase.trackDao().getCreatedPlaylists()
        val playlistList = playlistEntityList.map { playlistEntity ->
            converter.mapPlaylistEntityToPlaylist(playlistEntity)
        }
        return playlistList
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        withContext(Dispatchers.IO) {
            val playlistEntity = converter.mapPlaylistToPlaylistEntity(playlist)
            playlistDataBase.trackDao().updatePlaylist(playlistEntity)
        }
    }

    override suspend fun deletePlaylist(playlist: Playlist) {
        val playlistEntity = converter.mapPlaylistToPlaylistEntity(playlist)
        playlistDataBase.trackDao().deletePlaylist(playlistEntity)
    }
}