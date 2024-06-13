package com.practicum.playlistmaker.player.domain.impl


import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.player.models.PlayerState

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
}