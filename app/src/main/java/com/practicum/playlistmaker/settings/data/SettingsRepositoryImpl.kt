package com.practicum.playlistmaker.settings.data

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.settings.ui.MY_PREFS
import com.practicum.playlistmaker.settings.ui.SWITCH_STATUS
import com.practicum.playlistmaker.utils.App

class SettingsRepositoryImpl(private val application: Application) : SettingsRepository {

    private val sharedPreferences = application.getSharedPreferences(MY_PREFS, MODE_PRIVATE)

    override fun getDarkThemeStatus(): Boolean {
        return sharedPreferences.getBoolean(SWITCH_STATUS,true)
    }

    override fun changeDarkThemeStatus(status: Boolean) {
        if (status) {
            sharedPreferences.edit().putBoolean(SWITCH_STATUS, true).apply()
        } else {
            sharedPreferences.edit().putBoolean(SWITCH_STATUS, false).apply()
        }
        (application as App).switchTheme(status)
    }
}