package com.practicum.playlistmaker.player.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.practicum.playlistmaker.player.data.db.TrackEntity

@Dao
interface TrackDao {
    @Insert(entity = TrackEntity::class, onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete(entity = TrackEntity::class)
    suspend fun deleteTrack(trackEntity: TrackEntity)

    @Query("SELECT * FROM LIKED_TRACKS ORDER BY id DESC")
    suspend fun getLikedTracks(): List<TrackEntity>

    @Query("SELECT trackId FROM LIKED_TRACKS")
    suspend fun getTracksByTrackId(): List<Long>

    @Query("SELECT id FROM liked_tracks WHERE trackId = :trackIdNum ")
    suspend fun getTrackId(trackIdNum: Long): Long
}