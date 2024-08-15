package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow

interface MediaPlayerInteractor {
    fun play()
    fun pause()
    fun preparePlayer(url: String)
    fun releasePlayer()
    fun getState(): PlayerState
    fun getCurrentPosition(): Int
    fun setState(state: PlayerState)

    suspend fun addTrackInFavor(track: Track)
    suspend fun deleteTrackFromFavor(track: Track)
    suspend fun getLikedTracksFromDb(): Flow<List<Track>>

    suspend fun getLikeTracks(): List<Track>
    suspend fun getTracksByTrackId(): List<Long>
}