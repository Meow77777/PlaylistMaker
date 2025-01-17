package com.practicum.playlistmaker.search.presentation

import android.app.Application
import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.search.domain.entity.api.TracksInteractor
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.presentation.state.TracksState
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SearchTracksViewModel(
    private val interactor: TracksInteractor,
    application: Application
) :
    ViewModel() {

    private val SEARCH_DEBOUNCE_DELAY = 1000L


    private var latestSearchText: String? = null

    private var searchJob: Job? = null

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }
        this.latestSearchText = changedText
        searchJob?.cancel()

        searchJob = viewModelScope.launch {
            delay(SEARCH_DEBOUNCE_DELAY)
            loadData(changedText)
        }

    }

    val tracks = mutableListOf<Track>()
    private var listOfHistoryTracks = getTracksHistory()

    private val stateTracksLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateTracksLiveData

    private val historyTracksLiveData = MutableLiveData(listOfHistoryTracks)
    fun getHistoryTracksLiveData(): LiveData<MutableList<Track>> = historyTracksLiveData

    val nothingFoundText = application.resources.getString(R.string.nothingFound)
    val noConnectionText = application.resources.getString(R.string.internetProblem)

    fun addToHistory(track: Track) {
        interactor.addTrackInHistory(track = track)
        historyTracksLiveData.postValue(getTracksHistory())
    }


    fun getTracksHistory(): MutableList<Track> {
        return interactor.getTracksList()
    }

    fun clearTracksHistory() {
        interactor.clearTracksHistory()
        historyTracksLiveData.postValue(getTracksHistory())
    }

    private fun loadData(expression: String) {
        if (expression.isNotEmpty()) {

            renderState(TracksState.Loading)
            viewModelScope.launch {
                interactor.searchTracks(
                    expression = expression
                ).collect { pair ->
                    processResult(pair.first, pair.second)
                }
            }

        }
    }

    private fun processResult(foundTracks: List<Track>?, errorMessage: String?) {
        if (foundTracks != null) {
            tracks.clear()
            tracks.addAll(foundTracks)
        }

        when {
            errorMessage != null -> {
                renderState(TracksState.Error(message = noConnectionText))
            }

            tracks.isEmpty() -> {
                renderState(TracksState.Empty(message = nothingFoundText))
            }

            else -> {
                renderState(TracksState.Content(data = tracks))
            }
        }
    }

    private fun renderState(state: TracksState) {
        stateTracksLiveData.postValue(state)
    }

    fun clearLiveDataTrackState() {
        tracks.clear()
        renderState(TracksState.Content(data = tracks))
    }

    override fun onCleared() {
        super.onCleared()
        searchJob?.cancel()
    }


}
