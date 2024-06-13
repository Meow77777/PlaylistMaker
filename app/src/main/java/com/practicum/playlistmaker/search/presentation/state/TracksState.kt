package com.practicum.playlistmaker.search.presentation.state

import com.practicum.playlistmaker.search.models.Track

sealed interface TracksState {

    data object Loading : TracksState

    data class Error (val message : String) : TracksState

    data class Content (val data : List<Track>) : TracksState

    data class Empty (val message: String) : TracksState

}