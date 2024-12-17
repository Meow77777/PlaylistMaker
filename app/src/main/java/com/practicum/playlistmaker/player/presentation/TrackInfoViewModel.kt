package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.player.models.State
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TrackInfoViewModel(
    private val track: Track,
    private val mediaPlayerInteractor: MediaPlayerInteractor,
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private var listOfTracksId: List<Long> = listOf()
    private var job: Job? = null
    var currentTrack: Track? = null

    private val isLikedLiveData = MutableLiveData<Boolean>()
    fun getLikedStatusLiveData(): LiveData<Boolean> = isLikedLiveData

    private var currentPlayerState = PlayerState.STATE_DEFAULT
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val statePlayerLiveData = MutableLiveData<State>()
    fun getPlayerState(): LiveData<State> = statePlayerLiveData

    private val _playlists = MutableLiveData<List<Playlist>>()
    val playlists : MutableLiveData<List<Playlist>> = _playlists

    fun getPlaylists(){
        job?.cancel()
        job = viewModelScope.launch {
            val listOfPlaylist = playlistInteractor.getPlaylists()
            _playlists.value = listOfPlaylist
        }
    }

    init {
        getTracksId()
        updateLikedStatus()
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        viewModelScope.launch {
            playlist.tracks.add(track)
            playlistInteractor.updatePlaylist(playlist)
            getPlaylists()
        }
    }

    private fun updateLikedStatus() {
        if (listOfTracksId.contains(track.trackId)) {
            isLikedLiveData.postValue(true)
        } else {
            isLikedLiveData.postValue(false)
        }
    }

    fun addTrackToFavor(track: Track) {
        viewModelScope.launch {
            mediaPlayerInteractor.addTrackInFavor(track)
            isLikedLiveData.postValue(true)
        }
    }

    fun deleteTrackFromFavor(track: Track) {
        viewModelScope.launch {
            mediaPlayerInteractor.deleteTrackFromFavor(track)
            isLikedLiveData.postValue(false)
        }
    }

    private fun getTracksId(): List<Long> {
        job?.cancel()
        runBlocking {
            job = viewModelScope.launch(Dispatchers.IO) {
                listOfTracksId = mediaPlayerInteractor.getTracksByTrackId()
            }
            job?.join()
        }
        return listOfTracksId
    }

    private val handler = Handler(Looper.getMainLooper())
    private var jobTimer: Job? = null
    private var jobState: Job? = null

    private fun startTimerUpdate() {
        jobTimer?.cancel()
        jobTimer = viewModelScope.launch {
            while (isActive) {
                delay(300L)
                val time = dateFormat.format(
                    mediaPlayerInteractor.getCurrentPosition()
                )
                withContext(Dispatchers.Main) {
                    statePlayerLiveData.postValue(State.Timer(time))
                }
            }
        }

    }

    fun loadPlayer() {
        currentTrack?.let { track ->
            mediaPlayerInteractor.preparePlayer(track.previewUrl!!)
        }
    }

    fun pausePlayer(){
        mediaPlayerInteractor.pause()
    }

    fun changeState() {
        playbackControl()
    }

    fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
        jobTimer?.cancel()
        jobState?.cancel()
        renderState(State.Timer("00:00"))
    }

    private fun getAutoCurrentState() {
        jobState?.cancel()
        jobState = viewModelScope.launch {
            while (isActive) {
                currentPlayerState = mediaPlayerInteractor.getState()
                if (currentPlayerState == PlayerState.STATE_COMPLETED) {
                    renderState(state = State.Default)
                    jobTimer?.cancel()
                    jobState?.cancel()
                    mediaPlayerInteractor.setState(state = PlayerState.STATE_PREPARED)
                }
                delay(300L)
            }
        }
    }


    private fun play() {
        mediaPlayerInteractor.play()
        getAutoCurrentState()
        startTimerUpdate()
        renderState(state = State.isPlaying)
    }

    private fun playbackControl() {
        when (mediaPlayerInteractor.getState()) {

            PlayerState.STATE_PLAYING -> {
                mediaPlayerInteractor.pause()
                jobState?.cancel()
                jobTimer?.cancel()
                renderState(state = State.onPause)
            }

            PlayerState.STATE_PAUSED -> {
                play()
            }

            PlayerState.STATE_PREPARED -> {
                play()
            }

            PlayerState.STATE_COMPLETED -> {}

            else -> {}
        }
    }

    private fun renderState(state: State) {
        statePlayerLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        jobTimer?.cancel()
        jobState?.cancel()
        renderState(State.Timer("00:00"))
    }
}