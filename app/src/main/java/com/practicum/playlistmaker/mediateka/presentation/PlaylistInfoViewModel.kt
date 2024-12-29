package com.practicum.playlistmaker.mediateka.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.practicum.playlistmaker.mediateka.domain.api.PlaylistInteractor
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class PlaylistInfoViewModel(
    private val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val _totalDuration = MutableSharedFlow<String>()
    val totalDuration: SharedFlow<String> = _totalDuration

    private val _tracksCount = MutableSharedFlow<String>()
    val totalCount: SharedFlow<String> = _tracksCount

    private val _tracksCountInt = MutableSharedFlow<Int>()
    val totalCountInt: SharedFlow<Int> = _tracksCountInt

    fun deletePlaylist(playlist: Playlist) {
        viewModelScope.launch {
            playlistInteractor.deletePlaylist(playlist)
        }
    }

    fun deleteTrack(playlist: Playlist, track: Track){
        viewModelScope.launch {
            if (playlist.tracks.contains(track)){
                playlist.tracks.remove(track)
                playlistInteractor.updatePlaylist(playlist)

                val totalLength = getTotalDurationString(playlist.tracks)
                _totalDuration.emit(totalLength)

                val totalCount = getTracksCountString(playlist.tracks)
                _tracksCount.emit(totalCount)

                val totalCountInt = playlist.tracks.size
                _tracksCountInt.emit(totalCountInt)
            }
        }
    }

    private fun getTotalDurationString(tracks: List<Track>): String {
        // Суммируем продолжительности всех треков (преобразуем строку в Long)
        val totalDurationMillis = tracks.sumOf { it.trackTimeMillis?.toLongOrNull() ?: 0L }

        val totalMinutes = totalDurationMillis / 60000

        val minutesWord = when {
            (totalMinutes % 10).toInt() == 1 && (totalMinutes % 100).toInt() != 11 -> "минута"  // 1 минута
            totalMinutes % 10 in 2..4 && (totalMinutes % 100 !in 12..14) -> "минуты"  // 2-4 минуты
            else -> "минут"  // 5 и более минут
        }

        // Форматируем строку для отображения
        return "$totalMinutes $minutesWord"
    }

    private fun getTracksCountString(tracks: List<Track>): String {
        val tracksCount = tracks.size

        // Применяем правила склонения для слова "трек"
        val tracksWord = when {
            tracksCount % 10 == 1 && tracksCount % 100 != 11 -> "трек"  // 1 трек
            tracksCount % 10 in 2..4 && (tracksCount % 100 !in 12..14) -> "трека"  // 2-4 трека
            else -> "треков"  // 5 и более треков
        }

        // Форматируем строку для отображения
        return "$tracksCount $tracksWord"
    }

}