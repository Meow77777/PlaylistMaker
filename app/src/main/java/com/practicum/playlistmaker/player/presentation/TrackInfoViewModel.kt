package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.player.models.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale

class TrackInfoViewModel(
    private val trackUrl: String,
    private val mediaPlayerInteractor: MediaPlayerInteractor
) : ViewModel() {


    private var currentPlayerState = PlayerState.STATE_DEFAULT
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val statePlayerLiveData = MutableLiveData<State>()
    fun getPlayerState(): LiveData<State> = statePlayerLiveData


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
        val track = trackUrl
        val playerRun = Runnable {
            mediaPlayerInteractor.preparePlayer(track)
        }
        handler.post(playerRun)
    }

    fun changeState() {
        playbackControl()
    }

    fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
        jobTimer?.cancel()
        jobState?.cancel()
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
    }
}