package com.practicum.playlistmaker.player.domain.api

import com.practicum.playlistmaker.player.models.PlayerState

interface MediaPlayerInteractor {
    fun play()
    fun pause()
    fun preparePlayer(url: String)
    fun releasePlayer()
    fun getState(): PlayerState
    fun getCurrentPosition(): Int
    fun setState(state: PlayerState)
}