package com.practicum.playlistmaker.settings.domain.impl

import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository

class SettingsInteractorImpl(private val settingsRepository: SettingsRepository)  : SettingsInteractor{
    override fun getSwitchStatus(): Boolean {
        return settingsRepository.getDarkThemeStatus()
    }

    override fun turnOnDarkTheme(status : Boolean) {
        settingsRepository.changeDarkThemeStatus(status)
    }

}