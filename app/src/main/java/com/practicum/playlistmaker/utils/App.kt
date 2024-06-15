package com.practicum.playlistmaker.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.creator.Creator
import com.practicum.playlistmaker.settings.ui.MY_PREFS
import com.practicum.playlistmaker.settings.ui.SWITCH_STATUS

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE)
        val switchStatus = sharedPreferences.getBoolean(SWITCH_STATUS, false)
        Creator.setApplication(this)
        switchTheme(switchStatus)
    }


    fun switchTheme(darkThemeEnabled: Boolean) {
        darkTheme = darkThemeEnabled
        AppCompatDelegate.setDefaultNightMode(
            if (darkThemeEnabled) {
                AppCompatDelegate.MODE_NIGHT_YES
            } else {
                AppCompatDelegate.MODE_NIGHT_NO
            }
        )
    }
}
