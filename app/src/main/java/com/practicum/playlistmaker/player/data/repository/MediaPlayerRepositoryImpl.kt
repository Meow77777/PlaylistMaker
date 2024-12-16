package com.practicum.playlistmaker.player.data.repository

import android.media.MediaPlayer
import android.util.Log
import com.practicum.playlistmaker.player.data.db.AppDatabase
import com.practicum.playlistmaker.player.data.db.converters.TrackDbConverter
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MediaPlayerRepositoryImpl(
    private var mediaPlayer: MediaPlayer,
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : MediaPlayerRepository {

    init {
        initializeMediaPlayer()
    }

    override var state: PlayerState = PlayerState.STATE_DEFAULT
    private var currentUrl: String? = null
    private var shouldPlayAfterPrepare = false

    override fun playPlayer() {
        try {
            when (state) {
                PlayerState.STATE_PREPARED, PlayerState.STATE_PAUSED -> {
                    if (!mediaPlayer.isPlaying) {
                        mediaPlayer.start()
                        state = PlayerState.STATE_PLAYING
                    }
                }
                PlayerState.STATE_DEFAULT -> {
                    if (!currentUrl.isNullOrEmpty()) {
                        shouldPlayAfterPrepare = true
                        preparePlayer(currentUrl!!)
                    }
                }
                else -> Log.d("MediaPlayer", "playPlayer called in invalid state: $state")
            }
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error starting MediaPlayer: ${e.message}")
            handleMediaPlayerError()
        }
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setOnPreparedListener {
                state = PlayerState.STATE_PREPARED
                if (shouldPlayAfterPrepare) {
                    start()
                    state = PlayerState.STATE_PLAYING
                    shouldPlayAfterPrepare = false
                }
            }
            setOnCompletionListener {
                state = PlayerState.STATE_COMPLETED
            }
            setOnErrorListener { _, what, extra ->
                Log.e("MediaPlayer", "Error occurred: what=$what, extra=$extra")
                handleMediaPlayerError()
                true
            }
        }
    }

    private fun handleMediaPlayerError() {
        mediaPlayer.release()
        state = PlayerState.STATE_DEFAULT
        initializeMediaPlayer()
        if (!currentUrl.isNullOrEmpty()) {
            preparePlayer(currentUrl!!)
        }
    }

    override fun pausePlayer() {
        mediaPlayer.pause()
        state = PlayerState.STATE_PAUSED
    }

    override fun preparePlayer(url: String) {
        try {
            if (url.isBlank()) {
                Log.e("MediaPlayer", "URL is blank, cannot prepare player.")
                return
            }

            if (state != PlayerState.STATE_DEFAULT) {
                mediaPlayer.reset()
                state = PlayerState.STATE_DEFAULT
            }

            currentUrl = url
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error in preparePlayer: ${e.message}")
            handleMediaPlayerError()
        }
    }

    override fun releasePlayer() {
        try {
            if (state == PlayerState.STATE_PLAYING || state == PlayerState.STATE_PAUSED) {
                mediaPlayer.stop()
            }
            mediaPlayer.release()
            state = PlayerState.STATE_DEFAULT
        } catch (e: Exception) {
            Log.e("MediaPlayer", "Error releasing MediaPlayer: ${e.message}")
        }
    }

    override fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override suspend fun addTrackInFavor(track: Track) {
        appDatabase.trackDao()
            .insertTrack(trackEntity = trackDbConverter.mapTrackToTrackEntityToAdd(track))

        track.isLiked = true
    }

    override suspend fun deleteTrackFromFavor(track: Track) {
        appDatabase.trackDao()
            .deleteTrack(trackEntity = trackDbConverter.mapTrackToTrackEntityToDelete(track))
        track.isLiked = false
    }

    override suspend fun getLikedTracksFromDb(): Flow<List<Track>> = flow {
        val tracks = appDatabase.trackDao().getLikedTracks()
        emit(tracks.map { trackEntity -> trackDbConverter.mapToTrack(trackEntity) })
    }

    override suspend fun getLikedTracks(): List<Track> {
        return appDatabase.trackDao().getLikedTracks()
            .map { trackEntity -> trackDbConverter.mapToTrack(trackEntity) }

    }

    override suspend fun getTracksByTrackId(): List<Long> {
        return appDatabase.trackDao().getTracksByTrackId()
    }

    override suspend fun getIdTrackInDb(trackId: Long): Long {
        return appDatabase.trackDao().getTrackId(trackId)
    }
}