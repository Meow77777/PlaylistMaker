package com.practicum.playlistmaker.di

import com.practicum.playlistmaker.mediateka.presentation.FragmentLikedTracksViewModel
import com.practicum.playlistmaker.mediateka.presentation.FragmentPlaylistsViewModel
import com.practicum.playlistmaker.player.presentation.TrackInfoViewModel
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.presentation.SearchTracksViewModel
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    viewModel<TrackInfoViewModel> { (track: Track) ->
        TrackInfoViewModel(track, mediaPlayerInteractor = get())
    }

    viewModel<SettingsViewModel> {
        SettingsViewModel(sharingInteractor = get(), settingsInteractor = get())
    }

    viewModel<SearchTracksViewModel> {
        SearchTracksViewModel(interactor = get(), application = get())
    }

    viewModel<FragmentLikedTracksViewModel> {
        FragmentLikedTracksViewModel(mediaPlayerInteractor = get())
    }

    viewModel<FragmentPlaylistsViewModel> {
        FragmentPlaylistsViewModel()
    }

}