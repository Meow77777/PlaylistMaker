package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.player.models.State
import java.text.SimpleDateFormat
import java.util.Locale

class TrackInfoViewModel(
    trackUrl: String,
    private val mediaPlayerInteractor: MediaPlayerInteractor
) : ViewModel() {


    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val statePlayerLiveData = MutableLiveData<State>()
    fun getPlayerState(): LiveData<State> = statePlayerLiveData

    private val timerLiveData = MutableLiveData<String>()
    fun getTimerLiveData(): LiveData<String> = timerLiveData

    private val handler = Handler(Looper.getMainLooper())

    private fun updateTimer() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                try {
                    timerLiveData.postValue(dateFormat.format(mediaPlayerInteractor.getCurrentPosition()))
                    handler.postDelayed(this, 100L)
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }, 1000L)
    }

    init {
        playbackControl()
        mediaPlayerInteractor.preparePlayer(url = trackUrl)
    }

    fun changeState() {
        playbackControl()
    }

    fun releasePlayer() {
        mediaPlayerInteractor.releasePlayer()
    }

    private fun playbackControl() {
        when (mediaPlayerInteractor.getState()) {
            PlayerState.PLAYING -> {
                updateTimer()
                mediaPlayerInteractor.pause()
                renderState(state = State.Play)
            }

            PlayerState.PAUSED -> {
                mediaPlayerInteractor.play()
                renderState(state = State.Pause)
            }

            PlayerState.PREPARED -> {
                updateTimer()
                mediaPlayerInteractor.play()
                renderState(state = State.Prepared)
            }

            PlayerState.DEFAULT -> {
                renderState(state = State.Default)
            }
        }
    }

    private fun renderState(state: State) {
        statePlayerLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}