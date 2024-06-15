package com.practicum.playlistmaker.creator

import android.app.Application
import android.content.Context
import com.practicum.playlistmaker.player.data.repository.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.api.MediaPlayerInteractor
import com.practicum.playlistmaker.player.domain.impl.MediaPlayerInteractorImpl
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.repository.SearchHistory
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.entity.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.entity.impl.TracksInteractorImpl
import com.practicum.playlistmaker.search.domain.entity.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.SettingsInteractor
import com.practicum.playlistmaker.settings.domain.impl.SettingsInteractorImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.SharingInteractor
import com.practicum.playlistmaker.sharing.domain.impl.SharingInteractorImpl
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository

object Creator {

    private lateinit var application: Application

    fun setApplication(application: Application){
        this.application = application
    }
    private fun getMediaPlayerRepository(): MediaPlayerRepository {
        return MediaPlayerRepositoryImpl()
    }

    fun provideMediaPlayerInteractor(): MediaPlayerInteractor {
        return MediaPlayerInteractorImpl(getMediaPlayerRepository())
    }

    private fun getTracksRepository(context: Context): TracksRepository {
        return TracksRepositoryImpl(
            RetrofitNetworkClient(),
            SearchHistory(
                context.getSharedPreferences(
                    "key_for_history_search",
                    Context.MODE_PRIVATE
                )
            )
        )
    }

    fun provideTracksInteractor(context: Context): TracksInteractor {
        return TracksInteractorImpl(getTracksRepository(context = context))
    }

    private fun getSharingRepository(): SharingRepository {
        return SharingRepositoryImpl(application)
    }

    fun provideSharingInteractor(): SharingInteractor {
        return SharingInteractorImpl(getSharingRepository())
    }

    private fun getSettingsRepository() : SettingsRepository{
        return SettingsRepositoryImpl(application)
    }

    fun provideSettingsInteractor() : SettingsInteractor{
        return SettingsInteractorImpl(getSettingsRepository())
    }

}