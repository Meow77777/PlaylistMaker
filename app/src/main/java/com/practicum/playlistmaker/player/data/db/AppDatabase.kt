package com.practicum.playlistmaker.player.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.practicum.playlistmaker.player.data.db.dao.TrackDao

@Database(
    version = 3,
    entities = [TrackEntity::class]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun trackDao(): TrackDao

}