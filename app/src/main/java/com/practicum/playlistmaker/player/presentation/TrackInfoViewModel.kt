package com.practicum.playlistmaker.player.presentation

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.player.models.State
import java.text.SimpleDateFormat
import java.util.Locale

class TrackInfoViewModel(
    trackUrl: String
) : ViewModel() {


    private val provideMediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val statePlayerLiveData = MutableLiveData<State>()
    fun getPlayerState(): LiveData<State> = statePlayerLiveData

    private val timerLiveData = MutableLiveData<String>()
    fun getTimerLiveData() : LiveData<String> = timerLiveData

    private val handler = Handler(Looper.getMainLooper())

    private fun updateTimer(){
        handler.postDelayed(object : Runnable{
            override fun run() {
                try {
                    timerLiveData.postValue(dateFormat.format(provideMediaPlayerInteractor.getCurrentPosition()))
                    handler.postDelayed(this,100L)
                }catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }, 1000L)
    }

    init {
        playbackControl()
        provideMediaPlayerInteractor.preparePlayer(url = trackUrl)
    }

    fun changeState(){
        playbackControl()
    }

    fun releasePlayer(){
        provideMediaPlayerInteractor.releasePlayer()
    }

    private fun playbackControl() {
        when (provideMediaPlayerInteractor.getState()) {
            PlayerState.PLAYING -> {
                updateTimer()
                provideMediaPlayerInteractor.pause()
                renderState(state = State.Play)
            }
            PlayerState.PAUSED -> {
                provideMediaPlayerInteractor.play()
                renderState(state = State.Pause)
            }
            PlayerState.PREPARED -> {
                updateTimer()
                provideMediaPlayerInteractor.play()
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

    companion object{
        fun factory(trackUrl: String): ViewModelProvider.Factory {
            return viewModelFactory {
                initializer {
                    TrackInfoViewModel(trackUrl)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacksAndMessages(null)
    }
}