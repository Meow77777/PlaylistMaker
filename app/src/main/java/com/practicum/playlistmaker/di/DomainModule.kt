package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.search.domain.entity.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.entity.impl.TracksInteractorImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import org.koin.dsl.module

val domainModule = module {

    factory<TracksInteractor> {
        TracksInteractorImpl(repository = get())
    }

    factory<MediaPlayerInteractor> {
        MediaPlayerInteractorImpl(mediaPlayerRepository = get())
    }

    factory<SharingInteractor> {
        SharingInteractorImpl(sharingRepository = get())
    }

    factory <SettingsInteractor> {
        SettingsInteractorImpl(settingsRepository = get())
    }
}