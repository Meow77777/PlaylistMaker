package com.practicum.playlistmaker.mediateka.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.models.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentPlaylistsViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists : MutableLiveData<List<Playlist>> = _playlists

    var job : Job? = null

    fun getPlaylists(){
        job?.cancel()
        job = viewModelScope.launch {

            val listOfPlaylist = playlistInteractor.getPlaylists()
            playlists.value = listOfPlaylist
            }
    }
}