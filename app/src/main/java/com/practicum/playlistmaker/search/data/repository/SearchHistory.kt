package com.practicum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.ui.SearchFragment

class SearchHistory(private val sharedPreferences: SharedPreferences) {


    var historyList = mutableListOf<Track>()


    fun addTrack(track: Track) {
        getTracksList()
        historyList.remove(track)
        historyList.add(0, track)
        if (historyList.size > 10) historyList.removeLast()
        sharedPreferences.edit()
            .putString(SearchFragment.HISTORY_KEY, createJsonFromTracksList(historyList))
            .apply()
    }

    fun getTracksList(): MutableList<Track> {
        val valueFromSharedPreferences = sharedPreferences.getString(SearchFragment.HISTORY_KEY, "")
        historyList = if (valueFromSharedPreferences.isNullOrEmpty()) mutableListOf()
        else Gson().fromJson(
            valueFromSharedPreferences,
            object : TypeToken<MutableList<Track>>() {}.type
        )
        return historyList
    }

    private fun createJsonFromTracksList(tracks: MutableList<Track>): String {
        return Gson().toJson(tracks)
    }
}