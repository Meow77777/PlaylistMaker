package com.practicum.playlistmaker.search.data.dto

import com.practicum.playlistmaker.search.models.Track

data class TrackResponse(
    val resultCount: Int,
    val results: List<Track>
) : Response()