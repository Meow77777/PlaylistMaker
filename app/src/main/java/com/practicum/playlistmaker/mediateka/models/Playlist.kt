package com.practicum.playlistmaker.mediateka.models

import com.practicum.playlistmaker.search.models.Track

data class Playlist(
    val id: Long = 0,
    val name: String,
    val description: String,
    val image: String?,
    val tracks: MutableList<Track>
)
