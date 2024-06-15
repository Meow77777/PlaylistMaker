package com.practicum.playlistmaker.search.presentation

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.search.domain.entity.api.TracksInteractor
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.presentation.state.TracksState

class SearchTracksViewModel(application: Application) : AndroidViewModel(application) {

    private val tracksInteractor = Creator.provideTracksInteractor(context = application)

    companion object {
        private const val SEARCH_DEBOUNCE_DELAY = 1000L
        private val SEARCH_REQUEST_TOKEN = Any()

        fun getViewModelFactory(): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                SearchTracksViewModel(this[APPLICATION_KEY] as Application)
            }
        }
    }


    private var latestSearchText: String? = null
    private val handler = Handler(Looper.getMainLooper())

    fun searchDebounce(changedText: String) {
        if (latestSearchText == changedText) {
            return
        }

        this.latestSearchText = changedText
        handler.removeCallbacksAndMessages(SEARCH_REQUEST_TOKEN)

        val searchRunnable = Runnable { loadData(changedText) }

        val postTime = SystemClock.uptimeMillis() + SEARCH_DEBOUNCE_DELAY
        handler.postAtTime(
            searchRunnable,
            SEARCH_REQUEST_TOKEN,
            postTime,
        )
    }


    private var listOfHistoryTracks = getTracksHistory()

    private val stateTracksLiveData = MutableLiveData<TracksState>()
    fun observeState(): LiveData<TracksState> = stateTracksLiveData

    private val historyTracksLiveData = MutableLiveData(listOfHistoryTracks)
    fun getHistoryTracksLiveData(): LiveData<MutableList<Track>> = historyTracksLiveData

    val nothingFoundText = application.resources.getString(R.string.nothingFound)
    val noConnectionText = application.resources.getString(R.string.internetProblem)

    fun addToHistory(track: Track) {
        tracksInteractor.addTrackInHistory(track = track)
    }


    fun getTracksHistory(): MutableList<Track> {
        return tracksInteractor.getTracksList()
    }

    private fun loadData(expression: String) {
        if (expression.isNotEmpty()) {
            renderState(TracksState.Loading)

            tracksInteractor.searchTracks(
                expression = expression,
                object : TracksInteractor.TracksConsumer {
                    override fun consume(foundTracks: List<Track>?, errorMessage: String?) {
                        val tracks = mutableListOf<Track>()

                        if (foundTracks != null) {
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
                })
        }
    }


    private fun renderState(state: TracksState) {
        stateTracksLiveData.postValue(state)
    }

}
