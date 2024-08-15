package com.practicum.playlistmaker.player.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "liked_tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long?,
    val trackId: Long?,
    val trackName: String?,
    val artistName: String?,
    val trackTimeMillis: String?,
    val artworkUrl100: String?,
    val collectionName: String?,
    val releaseDate: String?,
    val primaryGenreName: String?,
    val country: String?,
    val previewUrl: String?
)