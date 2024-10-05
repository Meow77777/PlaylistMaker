package com.practicum.playlistmaker.mediateka.data.db.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.mediateka.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.search.models.Track

class PlaylistDbConverter {

    fun mapPlaylistToPlaylistEntity(playlist: Playlist): PlaylistEntity {
        val listToJson = Gson().toJson(playlist.tracks)
        return PlaylistEntity(
            id = null,
            name = playlist.name,
            description = playlist.description,
            image = playlist.image,
            tracks = listToJson
        )
    }

    fun mapPlaylistEntityToPlaylist(playlistEntity: PlaylistEntity): Playlist {
        val jsonToList = Gson().fromJson<List<Track>>(
            playlistEntity.tracks,
            object : TypeToken<MutableList<Track>>() {}.type
        )
        return Playlist(
            name = playlistEntity.name,
            description = playlistEntity.description,
            image = playlistEntity.image,
            tracks = jsonToList
        )
    }

}