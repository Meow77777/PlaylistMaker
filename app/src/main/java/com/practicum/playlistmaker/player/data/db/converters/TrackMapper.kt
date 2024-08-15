package com.practicum.playlistmaker.player.data.db.converters

import com.practicum.playlistmaker.player.data.db.TrackEntity
import com.practicum.playlistmaker.search.models.Track
import org.mapstruct.Mapper
import org.mapstruct.Mapping

@Mapper
interface TrackMapper {
    @Mapping(target = "id", ignore = true)
    fun mapTrackToTrackEntity(track: Track): TrackEntity
}