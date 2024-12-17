package com.practicum.playlistmaker.di

import android.content.Context
import android.media.MediaPlayer
import androidx.room.Room
import com.google.gson.Gson
import com.practicum.playlistmaker.mediateka.data.db.PlaylistDataBase
import com.practicum.playlistmaker.mediateka.data.db.converters.PlaylistDbConverter
import com.practicum.playlistmaker.mediateka.data.repository.PlaylistRepositoryImpl
import com.practicum.playlistmaker.mediateka.domain.repository.PlaylistRepository
import com.practicum.playlistmaker.player.data.db.AppDatabase
import com.practicum.playlistmaker.player.data.db.converters.TrackDbConverter
import com.practicum.playlistmaker.player.data.repository.MediaPlayerRepositoryImpl
import com.practicum.playlistmaker.player.domain.repository.MediaPlayerRepository
import com.practicum.playlistmaker.search.data.NetworkClient
import com.practicum.playlistmaker.search.data.network.RetrofitNetworkClient
import com.practicum.playlistmaker.search.data.network.TrackApi
import com.practicum.playlistmaker.search.data.repository.SearchHistory
import com.practicum.playlistmaker.search.data.repository.TracksRepositoryImpl
import com.practicum.playlistmaker.search.domain.entity.repository.TracksRepository
import com.practicum.playlistmaker.settings.data.SettingsRepositoryImpl
import com.practicum.playlistmaker.settings.domain.repository.SettingsRepository
import com.practicum.playlistmaker.settings.ui.MY_PREFS
import com.practicum.playlistmaker.sharing.data.SharingRepositoryImpl
import com.practicum.playlistmaker.sharing.domain.repository.SharingRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val dataModule = module {

    single<TrackApi> {
        Retrofit.Builder()
            .baseUrl("https://itunes.apple.com")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TrackApi::class.java)

    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, "database.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    single {
        Room.databaseBuilder(androidContext(), PlaylistDataBase::class.java, "database.db2")
            .fallbackToDestructiveMigration()
            .build()
    }

    factory <PlaylistDbConverter>{
        PlaylistDbConverter()
    }

    factory <PlaylistRepository>{
        PlaylistRepositoryImpl(playlistDataBase = get(), converter = get())
    }

    single {
        androidContext().getSharedPreferences("key_for_history_search", Context.MODE_PRIVATE)
        androidContext().getSharedPreferences(MY_PREFS, Context.MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(trackApi = get())
    }

    single<SearchHistory> {
        SearchHistory(sharedPreferences = get())
    }

    single<TracksRepository> {
        TracksRepositoryImpl(networkClient = get(), searchHistory = get())
    }

    factory<MediaPlayer> {
        MediaPlayer()
    }

    factory<TrackDbConverter> {
        TrackDbConverter(appDatabase = get())
    }

    factory<MediaPlayerRepository> {
        MediaPlayerRepositoryImpl(
            mediaPlayer = get(),
            appDatabase = get(),
            trackDbConverter = get()
        )
    }

    single<SharingRepository> {
        SharingRepositoryImpl(application = get())
    }

    single<SettingsRepository> {
        SettingsRepositoryImpl(sharedPreferences = get())
    }

}