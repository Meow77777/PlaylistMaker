package com.practicum.playlistmaker.settings.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.model.DataEmail

class SettingsViewModel(
    private val sharingInteractor: SharingInteractor,
    private val settingsInteractor: SettingsInteractor
) : ViewModel() {

    private val shareLinkLiveData = MutableLiveData<String>()
    fun getShareLinkLiveData(): LiveData<String> = shareLinkLiveData

    private val termsLinkLiveData = MutableLiveData<String>()
    fun getTermsLinkLiveData(): LiveData<String> = termsLinkLiveData

    private val supportEmailLiveData = MutableLiveData<DataEmail>()
    fun getSupportEmailLiveData(): LiveData<DataEmail> = supportEmailLiveData

    private val settingsDarkThemeLiveData = MutableLiveData<Boolean>()
    fun getSettingsDarkThemeLiveData(): LiveData<Boolean> = settingsDarkThemeLiveData

    init {
        getDarkThemeStatus()
        setShareLink()
        setTermsLink()
        setDataEmail()
    }

    fun changeDarkTheme(status: Boolean) {
        settingsInteractor.turnOnDarkTheme(status)
        getDarkThemeStatus()
    }

    private fun setShareLink() {
        shareLinkLiveData.value = sharingInteractor.getShareAppLink()
    }

    private fun setTermsLink() {
        termsLinkLiveData.value = sharingInteractor.getTermsLink()
    }

    private fun setDataEmail() {
        supportEmailLiveData.value = sharingInteractor.getSupport()
    }

    private fun getDarkThemeStatus() {
        settingsDarkThemeLiveData.value = settingsInteractor.getSwitchStatus()
    }
}