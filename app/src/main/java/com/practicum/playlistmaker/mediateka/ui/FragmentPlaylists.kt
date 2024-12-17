package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.mediateka.presentation.FragmentPlaylistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val vm by viewModel<FragmentPlaylistsViewModel>()
    private lateinit var adapterPlaylist: PlaylistAdapter
    private lateinit var listOfPlaylists: List<Playlist>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listOfPlaylists = mutableListOf()
        adapterPlaylist = PlaylistAdapter(listOfPlaylists)

        binding.createdPlaylists.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.createdPlaylists.adapter = adapterPlaylist

        vm.playlists.observe(viewLifecycleOwner) { playlists ->
            if (playlists.isEmpty()) {
                showPlaceholders()
            } else {
                listOfPlaylists = playlists
                adapterPlaylist = PlaylistAdapter(listOfPlaylists)
                binding.createdPlaylists.adapter = adapterPlaylist
                adapterPlaylist.notifyDataSetChanged()
                showPlaylists()
            }
        }

        binding.addNewPlaylist.setOnClickListener {
            findNavController().navigate(R.id.fragmentCreatePlaylist)
        }
    }

    override fun onStart() {
        super.onStart()
        vm.getPlaylists()
    }

    private fun showPlaceholders() {
        binding.mediatekaIsEmpty.visibility = View.VISIBLE
        binding.noCreatedPlaylists.visibility = View.VISIBLE
        binding.createdPlaylists.visibility = View.GONE
    }

    private fun showPlaylists() {
        binding.mediatekaIsEmpty.visibility = View.GONE
        binding.noCreatedPlaylists.visibility = View.GONE
        binding.createdPlaylists.visibility = View.VISIBLE
    }

    companion object {
        @JvmStatic
        fun newInstance() = FragmentPlaylists()
    }
}