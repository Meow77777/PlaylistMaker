package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.mediateka.data.db.dao.PlaylistDao
import com.practicum.playlistmaker.player.data.db.TrackEntity

@Database(
    version = 1,
    entities = [PlaylistEntity::class]
)
abstract class PlaylistDataBase : RoomDatabase() {

    abstract fun trackDao(): PlaylistDao

}