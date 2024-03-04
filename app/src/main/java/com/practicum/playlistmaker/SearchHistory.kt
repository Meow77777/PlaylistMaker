package com.practicum.playlistmaker

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistory(private val sharedPreferences: SharedPreferences) {


    var historyList = mutableListOf<Track>()


    fun addTrack(track: Track) {
        getTracksList()
        historyList.remove(track)
        historyList.add(0, track)
        if (historyList.size > 10) historyList.removeLast()
        sharedPreferences.edit()
            .putString(PoiskActivity.HISTORY_KEY, createJsonFromTracksList(historyList))
            .apply()
    }

    fun getTracksList() {
        val valueFromSharedPreferences = sharedPreferences.getString(PoiskActivity.HISTORY_KEY, "")
        historyList = if (valueFromSharedPreferences.isNullOrEmpty()) mutableListOf()
        else Gson().fromJson(
            valueFromSharedPreferences,
            object : TypeToken<MutableList<Track>>() {}.type
        )
    }

    private fun createJsonFromTracksList(tracks: MutableList<Track>): String {
        return Gson().toJson(tracks)
    }
}