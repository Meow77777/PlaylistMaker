package com.practicum.playlistmaker.mediateka.domain.impl

import com.practicum.playlistmaker.mediateka.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.domain.repository.PlaylistRepository

class PlaylistInteractorImpl(private val playlistRepository: PlaylistRepository) : PlaylistInteractor {
}