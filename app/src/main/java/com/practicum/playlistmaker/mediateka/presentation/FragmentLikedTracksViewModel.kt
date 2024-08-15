package com.practicum.playlistmaker.mediateka.presentation

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class FragmentLikedTracksViewModel(private val mediaPlayerInteractor: MediaPlayerInteractor) :
    ViewModel() {
    private var listOfLikedTracks: List<Track> = listOf()
    private var job: Job? = null


    private val likedTracksLiveData = MutableLiveData<List<Track>>()
    fun getLikedTracksLiveData(): MutableLiveData<List<Track>> = likedTracksLiveData

    fun getTracks() {
        job?.cancel()
        job = viewModelScope.launch(Dispatchers.IO) {
            listOfLikedTracks = mediaPlayerInteractor.getLikeTracks()
            likedTracksLiveData.postValue(listOfLikedTracks)
        }
    }
}