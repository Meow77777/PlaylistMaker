package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.TrackInfoBinding
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.player.models.State
import com.practicum.playlistmaker.player.models.TrackDetailsInfo
import com.practicum.playlistmaker.player.presentation.TrackDetailsMapper
import com.practicum.playlistmaker.player.presentation.TrackInfoViewModel
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class TrackInfoFragment : Fragment() {


    private lateinit var previewUrlIntent: String

    private lateinit var play: ImageButton
    private lateinit var backButton: ImageView
    private lateinit var trackName: TextView
    private lateinit var authorName: TextView
    private lateinit var trackLength: TextView
    private lateinit var trackImage: ImageView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView
    private lateinit var timerTextView: TextView
    private lateinit var likeButton: ImageButton
    private lateinit var addToPlaylist: ImageButton
    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetAdapter: BottomSheetAdapter
    private lateinit var listOfPlaylists: MutableList<Playlist>
    private lateinit var playlistRecycler: RecyclerView
    private lateinit var createPlaylist: Button
    private lateinit var constraintLayout: ConstraintLayout
    private lateinit var darkFrame: FrameLayout

    private var likedStatus = false

    private lateinit var track: Track

    private val vm by viewModel<TrackInfoViewModel> {
        parametersOf(track)
    }

    private lateinit var binding: TrackInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = TrackInfoBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        play = binding.playButtonTrackInfo
        backButton = binding.backFromTrackInfo
        trackName = binding.songNameTrackInfo
        authorName = binding.authorNameTrackInfo
        trackLength = binding.songLengthTrackInfo
        trackImage = binding.mainTrackInfoImage
        collectionName = binding.songAlbumTrackInfo
        releaseDate = binding.songYearTrackInfo
        primaryGenreName = binding.songGenreTrackInfo
        country = binding.songCountryTrackInfo
        timerTextView = binding.songCurrentTimeTrackInfo
        likeButton = binding.likeSongTrackInfo
        addToPlaylist = binding.addToFavorTrackInfo
        bottomSheet = binding.standardBottomSheet
        playlistRecycler = binding.playlistRecyclerBS
        createPlaylist = binding.addNewPlaylist
        constraintLayout = binding.constraintLayout
        darkFrame = binding.darkFrame

        track = arguments?.getParcelable<Track>("track")!!

        vm.currentTrack = track

        vm.getLikedStatusLiveData().observe(viewLifecycleOwner) { status ->
            when (status) {
                true -> {
                    likeButton.setImageResource(R.drawable.red_like_mediateka)
                    likedStatus = true
                }

                false -> {
                    likeButton.setImageResource(R.drawable.like_mediateka)
                    likedStatus = false
                }
            }
        }

        likeButton.setOnClickListener {
            when (likedStatus) {
                true -> track.let { it1 -> vm.deleteTrackFromFavor(it1) }
                false -> track.let { it1 -> vm.addTrackToFavor(it1) }
            }
        }

        val trackInfoDetails = track.let { TrackDetailsMapper.map(it) }
        showTrackDetails(trackInfoDetails)

        vm.loadPlayer()

        vm.getPlayerState().observe(viewLifecycleOwner) { state ->
            when (state) {
                State.isPlaying -> showPauseButton()
                State.onPause -> showPlayButton()
                State.Default -> preparePlayer()
                is State.Timer -> timerTextView.text = state.time
            }
        }


        play.setOnClickListener {
            vm.changeState()
        }

        backButton.setOnClickListener {
            vm.pausePlayer()
            findNavController().popBackStack(
                findNavController().currentDestination?.id ?: return@setOnClickListener, true
            )
        }

        //РАБОТА С BottomSheet
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_EXPANDED, BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        // Показать затемнение
                        darkFrame.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_COLLAPSED, BottomSheetBehavior.STATE_HIDDEN -> {
                        // Скрыть затемнение
                        darkFrame.visibility = View.GONE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                darkFrame.visibility = View.GONE
            }
        })

        listOfPlaylists = mutableListOf()
        bottomSheetAdapter =
            BottomSheetAdapter(listOfPlaylists, object : BottomSheetAdapter.Listener {
                override fun onClick(playlist: Playlist) {
                    if (!playlist.tracks.any { it.trackId == track.trackId }) {
                        vm.addTrackToPlaylist(playlist, track)
                        Toast.makeText(
                            requireContext(),
                            "Добавлено в плейлист ${playlist.name}",
                            Toast.LENGTH_LONG
                        ).show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    } else {
                        Toast.makeText(
                            requireContext(),
                            "Трек уже добавлен в плейлист ${playlist.name}",
                            Toast.LENGTH_LONG
                        ).show()
                        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    }
                }
            })
        playlistRecycler.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        playlistRecycler.adapter = bottomSheetAdapter

        vm.playlists.observe(viewLifecycleOwner) { playlists ->
            listOfPlaylists.clear()
            listOfPlaylists.addAll(playlists)
            bottomSheetAdapter.notifyDataSetChanged()
        }

        addToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        createPlaylist.setOnClickListener {
            bottomSheet.visibility = View.GONE
            vm.pausePlayer()
            findNavController().navigate(R.id.fragmentCreatePlaylist)
        }
    }

    private fun showTrackDetails(trackInfoDetails: TrackDetailsInfo) {
        previewUrlIntent = trackInfoDetails.previewUrl.toString()
        trackName.text = trackInfoDetails.trackName
        authorName.text = trackInfoDetails.artistName
        trackLength.text = trackInfoDetails.trackTimeMillis
        Glide.with(trackImage)
            .load(trackInfoDetails.artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop().placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, requireContext()))).into(trackImage)
        if (!trackInfoDetails.collectionName.isNullOrEmpty()) collectionName.text =
            trackInfoDetails.collectionName
        releaseDate.text = trackInfoDetails.releaseDate!!.subSequence(IntRange(0, 3))
        primaryGenreName.text = trackInfoDetails.primaryGenreName
        country.text = trackInfoDetails.country
    }

    override fun onStart() {
        super.onStart()
        vm.getPlaylists()
    }

    override fun onDestroyView() {
        timerTextView.text = getString(R.string.songZero)
        super.onDestroyView()
        vm.releasePlayer()
    }

    override fun onPause() {
        super.onPause()
        play.setImageResource(R.drawable.play_song_button_mediateka)
        vm.pausePlayer()
    }

    private fun preparePlayer() {
        play.setImageResource(R.drawable.play_song_button_mediateka)
        timerTextView.text = getString(R.string.songZero)
    }

    private fun showPlayButton() {
        play.setImageResource(R.drawable.play_song_button_mediateka)
    }

    private fun showPauseButton() {
        play.setImageResource(R.drawable.pause)
    }
}