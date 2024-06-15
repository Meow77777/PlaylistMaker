package com.practicum.playlistmaker.settings.domain

interface SettingsInteractor {
    fun getSwitchStatus() : Boolean
    fun turnOnDarkTheme(status : Boolean)
}