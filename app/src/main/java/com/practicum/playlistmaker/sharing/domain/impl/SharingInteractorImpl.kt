package com.practicum.playlistmaker.sharing.domain.impl

import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import com.practicum.playlistmaker.sharing.model.DataEmail

class SharingInteractorImpl(private val sharingRepository: SharingRepository) : SharingInteractor {
    override fun getShareAppLink(): String {
        return sharingRepository.getShareAppLink()
    }

    override fun getTermsLink(): String {
        return sharingRepository.getTermsLink()
    }

    override fun getSupport(): DataEmail {
        return sharingRepository.getSupportEmailData()
    }


}