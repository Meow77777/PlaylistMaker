package com.practicum.playlistmaker.mediateka.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.practicum.playlistmaker.search.models.Track

@Entity(tableName = "created_playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val name: String,
    val description : String,
    val image: String,
    val tracks : String
)