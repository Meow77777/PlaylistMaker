package com.practicum.playlistmaker.mediateka.models

import com.practicum.playlistmaker.search.models.Track

data class Playlist(
    val name: String,
    val description: String,
    val image: String,
    val tracks: List<Track>
)
