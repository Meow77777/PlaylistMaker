package com.practicum.playlistmaker.player.domain.repository

import com.practicum.playlistmaker.player.models.PlayerState

interface MediaPlayerRepository {
    var state: PlayerState
    fun playPlayer()
    fun pausePlayer()
    fun preparePlayer(url: String)
    fun releasePlayer()
    fun getCurrentPosition(): Int
}