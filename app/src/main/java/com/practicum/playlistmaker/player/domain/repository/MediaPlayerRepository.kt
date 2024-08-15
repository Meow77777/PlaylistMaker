package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow

interface MediaPlayerRepository {
    var state: PlayerState
    fun playPlayer()
    fun pausePlayer()
    fun preparePlayer(url: String)
    fun releasePlayer()
    fun getCurrentPosition(): Int

    suspend fun addTrackInFavor(track: Track)
    suspend fun deleteTrackFromFavor(track: Track)
    suspend fun getLikedTracksFromDb(): Flow<List<Track>>

    suspend fun getLikedTracks(): List<Track>
    suspend fun getTracksByTrackId(): List<Long>
    suspend fun getIdTrackInDb(trackId: Long): Long
}