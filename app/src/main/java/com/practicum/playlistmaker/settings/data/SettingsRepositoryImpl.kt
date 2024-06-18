package com.practicum.playlistmaker.settings.data

import android.content.SharedPreferences
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.settings.ui.SWITCH_STATUS

class SettingsRepositoryImpl(
    private val sharedPreferences: SharedPreferences
) : SettingsRepository {


    override fun getDarkThemeStatus(): Boolean {
        return sharedPreferences.getBoolean(SWITCH_STATUS, false)
    }

    override fun changeDarkThemeStatus(status: Boolean) {
        sharedPreferences.edit().putBoolean(SWITCH_STATUS, status).apply()
    }
}