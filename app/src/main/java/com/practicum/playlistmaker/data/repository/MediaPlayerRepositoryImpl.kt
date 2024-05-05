package com.practicum.playlistmaker.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.repository.MediaPlayerRepository

class MediaPlayerRepositoryImpl : MediaPlayerRepository {

    private val mediaPlayer = MediaPlayer()


    override var state: PlayerState = PlayerState.DEFAULT

    override fun playPlayer() {
        mediaPlayer.start()
        state = PlayerState.PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        state = PlayerState.PAUSED
    }

    override fun preparePlayer(url: String) {
        mediaPlayer.apply {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                state = PlayerState.PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                state = PlayerState.PREPARED
            }
        }
    }

    override fun releasePlayer() {
        mediaPlayer.release()
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }


}