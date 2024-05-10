package com.practicum.playlistmaker.domain.impl


import com.practicum.playlistmaker.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.repository.MediaPlayerRepository

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