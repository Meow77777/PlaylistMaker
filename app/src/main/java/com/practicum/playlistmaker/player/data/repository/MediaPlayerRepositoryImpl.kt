package com.practicum.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.player.models.PlayerState

class MediaPlayerRepositoryImpl(private val mediaPlayer: MediaPlayer) : MediaPlayerRepository {

    override var state: PlayerState = PlayerState.STATE_DEFAULT

    override fun playPlayer() {
        mediaPlayer.start()
        state = PlayerState.STATE_PLAYING
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        state = PlayerState.STATE_PAUSED
    }

    override fun preparePlayer(url: String) {
        mediaPlayer.apply {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                state = PlayerState.STATE_PREPARED
            }
            mediaPlayer.setOnCompletionListener {
                state = PlayerState.STATE_COMPLETED
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