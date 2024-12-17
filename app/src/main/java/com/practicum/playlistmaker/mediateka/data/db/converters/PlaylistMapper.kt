package com.practicum.playlistmaker.mediateka.data.db.converters

import com.practicum.playlistmaker.mediateka.data.db.PlaylistEntity
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.player.data.db.TrackEntity
import com.practicum.playlistmaker.search.models.Track
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface PlaylistMapper {
    @Mapping(target = "id", ignore = true)
    fun mapPlaylistToPlaylistEntity(playlist: Playlist): PlaylistEntity
}