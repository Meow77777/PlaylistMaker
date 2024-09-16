package com.practicum.playlistmaker.mediateka.models

import com.practicum.playlistmaker.search.models.Track

data class Playlist(
    val name: String,
    val image: String,
    val numberOfTracks: Long,
    val listOfSongs: MutableList<Track>
)
