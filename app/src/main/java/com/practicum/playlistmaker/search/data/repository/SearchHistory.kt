package com.practicum.playlistmaker.search.data.repository

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.practicum.playlistmaker.search.models.Track

class SearchHistory(private val sharedPreferences: SharedPreferences) {

    companion object {
        const val HISTORY_KEY = "key_for_history_search"
        const val PREFS_HISTORY = "prefs_history"
    }

    var historyList = mutableListOf<Track>()

    fun clearTracksHistory() {
        historyList.clear()
        sharedPreferences.edit()
            .putString(HISTORY_KEY, Gson().toJson(historyList))
            .apply()
    }

    fun addTrack(track: Track) {
        getTracksList()

        historyList.remove(track)

        historyList.add(0, track)

        if (historyList.size > 10) historyList.removeLast()
        sharedPreferences.edit()
            .putString(HISTORY_KEY, createJsonFromTracksList(historyList))
            .apply()
    }

    fun getTracksList(): MutableList<Track> {
        val valueFromSharedPreferences = sharedPreferences.getString(HISTORY_KEY, "")
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