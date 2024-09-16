package com.practicum.playlistmaker.mediateka.data.db.converters

import com.practicum.playlistmaker.mediateka.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediateka.models.Playlist

class PlaylistDbConverter {
    fun mapPlaylistToPlaylistEntity(playlist: Playlist): PlaylistEntity{
        return PlaylistEntity(playlistName = playlist.name,
            playlistImage = playlist.image,
            numberOfTracks = playlist.numberOfTracks,
            tracks = playlist.listOfSongs,
            id = null
            )
    }

}