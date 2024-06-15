package com.practicum.playlistmaker.search.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.ui.SearchActivity
import com.practicum.playlistmaker.search.data.repository.SearchHistory

class LocalStorage(private val sharedPreferences: SharedPreferences) {
    private companion object {
        const val FAVORITES_KEY = "FAVORITES_KEY"
    }

    fun addToHistory(track: Track) {
        changeFavorites(track = track, remove = false)
    }

    fun removeFromFavorites(track: Track) {
        changeFavorites(track = track, remove = true)
    }

    fun getSavedFavorites(): String? {
        return sharedPreferences.getString(SearchActivity.HISTORY_KEY, "")
    }

    private fun changeFavorites(track: Track, remove: Boolean) {
        val mutableSet = SearchHistory(sharedPreferences = sharedPreferences).historyList
        val modified = if (remove) mutableSet.remove(track) else mutableSet.add(track)
        if (modified) sharedPreferences.edit().putString(FAVORITES_KEY, mutableSet.toString()).apply()
    }
}