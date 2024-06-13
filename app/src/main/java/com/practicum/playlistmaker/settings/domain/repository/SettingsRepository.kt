package com.practicum.playlistmaker.settings.domain.repository

interface SettingsRepository {
    fun getDarkThemeStatus(): Boolean
    fun changeDarkThemeStatus(status: Boolean)
}