package com.practicum.playlistmaker.sharing.data

import android.app.Application
import android.net.Uri
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import com.practicum.playlistmaker.sharing.model.DataEmail

class SharingRepositoryImpl(private val application: Application) : SharingRepository {
    override fun getShareAppLink(): String {
        return application.resources.getString(R.string.shareLink)
    }

    override fun getTermsLink(): String {
        return application.resources.getString(R.string.agreementLink)
    }

    override fun getSupportEmailData(): DataEmail {
        return DataEmail(
            adress = Uri.parse(application.resources.getString(R.string.mailto)),
            email = application.resources.getString(R.string.myEmail),
            message = application.resources.getString(R.string.supportMessageText),
            subject = application.resources.getString(R.string.supportMessageSubject)
        )
    }

}