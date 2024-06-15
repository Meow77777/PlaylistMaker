package com.practicum.playlistmaker.player.models

sealed interface State {
    data object Play: State
    data object Pause: State
    data object Default: State
    data object Prepared : State
}