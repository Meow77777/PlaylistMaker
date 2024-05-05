package com.practicum.playlistmaker.domain.repository

import com.practicum.playlistmaker.domain.models.PlayerState

interface MediaPlayerRepository {
    var state: PlayerState
    fun playPlayer()
    fun pausePlayer()
    fun preparePlayer(url: String)
    fun releasePlayer()
    fun getCurrentPosition(): Int
}