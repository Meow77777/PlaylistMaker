package com.practicum.playlistmaker.sharing.domain

import android.content.Intent
import com.practicum.playlistmaker.sharing.model.DataEmail

interface SharingInteractor {
    fun getShareAppLink() : String
    fun getTermsLink() : String
    fun getSupport() : DataEmail
}