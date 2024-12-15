package com.practicum.playlistmaker.utils

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.practicum.playlistmaker.di.appModule
import com.practicum.playlistmaker.di.dataModule
import com.practicum.playlistmaker.di.domainModule
import com.practicum.playlistmaker.settings.ui.MY_PREFS
import com.practicum.playlistmaker.settings.ui.SWITCH_STATUS
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class App : Application() {

    private var darkTheme = false
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@App)
            modules(listOf(appModule, dataModule, domainModule))
        }

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
