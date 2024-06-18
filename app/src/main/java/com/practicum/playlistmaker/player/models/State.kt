package com.practicum.playlistmaker.player.models

sealed interface State {
    data object isPlaying: State
    data object onPause: State
    data object Default: State
    data class Timer(val time: String): State
}