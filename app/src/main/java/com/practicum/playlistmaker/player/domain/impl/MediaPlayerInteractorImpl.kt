package com.practicum.playlistmaker.player.domain.impl


import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow

class MediaPlayerInteractorImpl(private val mediaPlayerRepository: MediaPlayerRepository) :
    MediaPlayerInteractor {
    override fun play() {
        mediaPlayerRepository.playPlayer()
    }

    override fun pause() {
        mediaPlayerRepository.pausePlayer()
    }

    override fun preparePlayer(url: String) {
        mediaPlayerRepository.preparePlayer(url)
    }

    override fun releasePlayer() {
        mediaPlayerRepository.releasePlayer()
    }


    override fun getState(): PlayerState {
        return mediaPlayerRepository.state
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayerRepository.getCurrentPosition()
    }

    override fun setState(state: PlayerState) {
        mediaPlayerRepository.state = state
    }

    override suspend fun addTrackInFavor(track: Track) {
        mediaPlayerRepository.addTrackInFavor(track)
    }

    override suspend fun deleteTrackFromFavor(track: Track) {
        mediaPlayerRepository.deleteTrackFromFavor(track)
    }

    override suspend fun getLikedTracksFromDb(): Flow<List<Track>> {
        return mediaPlayerRepository.getLikedTracksFromDb()
    }

    override suspend fun getLikeTracks(): List<Track> {
        return mediaPlayerRepository.getLikedTracks()
    }

    override suspend fun getTracksByTrackId(): List<Long> {
        return mediaPlayerRepository.getTracksByTrackId()
    }
}