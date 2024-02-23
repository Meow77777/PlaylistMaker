package com.practicum.playlistmaker

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate

class App : Application() {

    var darkTheme = false
    override fun onCreate() {
        super.onCreate()
        val sharedPreferences = getSharedPreferences(MY_PREFS, MODE_PRIVATE)
        val switchStatus = sharedPreferences.getBoolean(SWITCH_STATUS, false)

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
