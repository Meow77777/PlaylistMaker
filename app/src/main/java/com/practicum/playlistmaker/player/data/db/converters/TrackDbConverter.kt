package com.practicum.playlistmaker.player.data.db.converters

import com.practicum.playlistmaker.player.data.db.AppDatabase
import com.practicum.playlistmaker.player.data.db.TrackEntity
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking

class TrackDbConverter(private val appDatabase: AppDatabase) {
    fun mapToTrack(trackEntity: TrackEntity): Track {
        return Track(
            trackEntity.trackName,
            trackEntity.artistName,
            trackEntity.trackTimeMillis,
            trackEntity.artworkUrl100,
            trackEntity.collectionName,
            trackEntity.releaseDate,
            trackEntity.primaryGenreName,
            trackEntity.country,
            trackEntity.previewUrl,
            trackEntity.trackId,
            isLiked = true,
            trackEntity.id
        )
    }

    fun mapTrackToTrackEntityToAdd(track: Track): TrackEntity {
        return TrackEntity(
            id = null,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }

    fun mapTrackToTrackEntityToDelete(track: Track): TrackEntity {
        var id: Long
        val result = CoroutineScope(Dispatchers.IO).async {
            id = appDatabase.trackDao().getTrackId(track.trackId!!)
            return@async id
        }
        runBlocking {
            id = result.await()
        }
        return TrackEntity(
            id,
            track.trackId,
            track.trackName,
            track.artistName,
            track.trackTimeMillis,
            track.artworkUrl100,
            track.collectionName,
            track.releaseDate,
            track.primaryGenreName,
            track.country,
            track.previewUrl
        )
    }


}