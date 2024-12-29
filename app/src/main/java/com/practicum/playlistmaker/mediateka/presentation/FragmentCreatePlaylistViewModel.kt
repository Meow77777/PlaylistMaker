package com.practicum.playlistmaker.mediateka.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.models.Playlist
import kotlinx.coroutines.launch

class FragmentCreatePlaylistViewModel(private val playlistInteractor: PlaylistInteractor) :
    ViewModel() {

    fun addPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.addPlaylist(playlist)
        }
    }

    fun updatePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.updatePlaylist(playlist)
        }
    }
}