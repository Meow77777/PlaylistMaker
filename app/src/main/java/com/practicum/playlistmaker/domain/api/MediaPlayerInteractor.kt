package com.practicum.playlistmaker.domain.api

import com.practicum.playlistmaker.domain.models.PlayerState

interface MediaPlayerInteractor {
    fun play()
    fun pause()
    fun preparePlayer(url: String)
    fun releasePlayer()
    fun getState(): PlayerState
    fun getCurrentPosition(): Int
}