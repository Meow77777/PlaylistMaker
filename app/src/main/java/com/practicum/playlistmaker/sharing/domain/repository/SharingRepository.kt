package com.practicum.playlistmaker.sharing.domain.repository

import com.practicum.playlistmaker.sharing.model.DataEmail

interface SharingRepository {
    fun getShareAppLink(): String
    fun getTermsLink(): String
    fun getSupportEmailData(): DataEmail
}