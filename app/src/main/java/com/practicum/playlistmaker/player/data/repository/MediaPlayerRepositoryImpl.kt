package com.practicum.playlistmaker.player.data.repository

import android.media.MediaPlayer
import com.practicum.playlistmaker.player.data.db.AppDatabase
import com.practicum.playlistmaker.player.data.db.converters.TrackDbConverter
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.player.models.PlayerState
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class MediaPlayerRepositoryImpl(
    private val mediaPlayer: MediaPlayer,
    private val appDatabase: AppDatabase,
    private val trackDbConverter: TrackDbConverter
) : MediaPlayerRepository {

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