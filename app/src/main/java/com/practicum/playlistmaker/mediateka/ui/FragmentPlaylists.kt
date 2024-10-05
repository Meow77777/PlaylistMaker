package com.practicum.playlistmaker.mediateka.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentPlaylistsBinding
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.mediateka.presentation.FragmentPlaylistsViewModel
import com.practicum.playlistmaker.search.models.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class FragmentPlaylists : Fragment() {

    private lateinit var binding: FragmentPlaylistsBinding
    private val vm by viewModel<FragmentPlaylistsViewModel>()
    private lateinit var adapterPlaylist: PlaylistAdapter
    private lateinit var listOfPlaylists: List<Playlist>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val savedStateHandle = findNavController().currentBackStackEntry?.savedStateHandle

        savedStateHandle?.getLiveData<String>("playlistName")?.observe(viewLifecycleOwner) { playlistName ->
            if (playlistName != "") {
                lifecycleScope.launch {
                    binding.playlistCreatedNotify.text = playlistName
                    binding.playlistCreatedNotify.visibility= View.VISIBLE
                    delay(5000L)
                    binding.playlistCreatedNotify.visibility= View.GONE
                }

//                try {
//                    val toast = Toast(requireContext())
//                    val inflater = LayoutInflater.from(requireContext())
//                    val layout: View = inflater.inflate(R.layout.playlist_created_toast, binding.root)
//                    val textView: TextView = layout.findViewById(R.id.playlistCreatedNotify)
//                    textView.text = "Плейлист ${playlistName} создан"
//                    toast.duration = Toast.LENGTH_LONG
//                    toast.view = layout
//                    toast.show()
//                } catch (e: IllegalArgumentException){
//                    println("exc")
//                }

                savedStateHandle.remove<String>("playlistName")
            }
        }

        listOfPlaylists = mutableListOf()
        adapterPlaylist = PlaylistAdapter(listOfPlaylists)

        binding.createdPlaylists.layoutManager =
            GridLayoutManager(requireContext(), 2)
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