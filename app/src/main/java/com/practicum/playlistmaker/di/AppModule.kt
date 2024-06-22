package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.player.presentation.TrackInfoViewModel
import com.practicum.playlistmaker.search.presentation.SearchTracksViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<TrackInfoViewModel> { (trackUrl: String) ->
        TrackInfoViewModel(trackUrl, mediaPlayerInteractor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }

    viewModel<SearchTracksViewModel> {
        SearchTracksViewModel(interactor = get(), application = get())
    }

}