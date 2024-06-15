package com.practicum.playlistmaker.search.domain.entity.impl

import com.practicum.playlistmaker.search.domain.entity.Resource
import com.practicum.playlistmaker.search.domain.entity.api.TracksInteractor
import com.practicum.playlistmaker.search.domain.entity.repository.TracksRepository
import com.practicum.playlistmaker.search.models.Track
import java.util.concurrent.Executors

class TracksInteractorImpl(private val repository: TracksRepository) : TracksInteractor {

    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(expression: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            when (val resource = repository.searchTracks(expression)) {
                is Resource.Success -> {
                    consumer.consume(resource.data, null)
                }

                is Resource.Error -> {
                    consumer.consume(null, resource.message)
                }
            }
        }
    }

    override fun addTrackInHistory(track: Track) {
        repository.addTrackInHistory(track = track)
    }

    override fun getTracksList() : MutableList<Track>{
        return repository.getTracksList()
    }
}