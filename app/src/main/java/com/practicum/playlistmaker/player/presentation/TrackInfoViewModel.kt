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
    private val trackUrl: String,
    private val mediaPlayerInteractor: MediaPlayerInteractor
) : ViewModel() {

    private var currentPlayerState = PlayerState.STATE_DEFAULT
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val statePlayerLiveData = MutableLiveData<State>()
    fun getPlayerState(): LiveData<State> = statePlayerLiveData

//    private val timerLiveData = MutableLiveData<String>()
//    fun getTimerLiveData(): LiveData<String> = timerLiveData

    private val handler = Handler(Looper.getMainLooper())

    private val timer = object : Runnable {
        override fun run() {
            try {
                val time = dateFormat.format(
                    mediaPlayerInteractor.getCurrentPosition())
                statePlayerLiveData.postValue(State.Timer(time))
                handler.postDelayed(this, 300L)
            } catch (e: IllegalStateException) {
                e.printStackTrace()
            }
        }
    }
    private fun startTimerUpdate() {
        handler.postDelayed(timer, 300L)
    }

//    init {
//        mediaPlayerInteractor.preparePlayer(url = trackUrl)
//        renderState(state = State.Prepared)
//    }

    fun loadPlayer(jsonTrack: String) {
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
        handler.removeCallbacks(timer)
        handler.removeCallbacks(runnablePlayerState)
    }

    private val runnablePlayerState = object : Runnable {
        override fun run() {
            currentPlayerState = mediaPlayerInteractor.getState()
            if (currentPlayerState == PlayerState.STATE_COMPLETED) {
                playbackControl()
            }
            handler.postDelayed(this, 500L)
        }
    }
    private fun getAutoCurrentState() {
        handler.postDelayed(runnablePlayerState, 500L)
    }

    private fun play(){
        mediaPlayerInteractor.play()
        getAutoCurrentState()
        startTimerUpdate()
        renderState(state = State.isPlaying)
    }

    private fun playbackControl() {
        when (mediaPlayerInteractor.getState()) {

            PlayerState.STATE_PLAYING -> {
                mediaPlayerInteractor.pause()
                renderState(state = State.onPause)
            }

            PlayerState.STATE_PAUSED -> {
                play()
            }

            PlayerState.STATE_PREPARED -> {
                play()
            }

            PlayerState.STATE_COMPLETED -> {
                renderState(state = State.Default)
                handler.removeCallbacks(timer)
                handler.removeCallbacks(runnablePlayerState)
                mediaPlayerInteractor.setState(state = PlayerState.STATE_PREPARED)
            }
            else -> null
        }
    }

    private fun renderState(state: State) {
        statePlayerLiveData.postValue(state)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(timer)
        handler.removeCallbacks(runnablePlayerState)
    }
}