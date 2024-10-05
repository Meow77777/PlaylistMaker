package com.practicum.playlistmaker.player.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.mediateka.ui.FragmentCreatePlaylist
import com.practicum.playlistmaker.player.models.State
import com.practicum.playlistmaker.player.models.TrackDetailsInfo
import com.practicum.playlistmaker.player.presentation.TrackDetailsMapper
import com.practicum.playlistmaker.player.presentation.TrackInfoViewModel
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf


class TrackInfoActivity : AppCompatActivity() {


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
    private lateinit var listOfPlaylists: List<Playlist>
    private lateinit var playlistRecycler: RecyclerView
    private lateinit var createPlaylist : Button
    private lateinit var fragmentContainer : FrameLayout
    private lateinit var constraintLayout: ConstraintLayout

    private var likedStatus = false

    private lateinit var trackIntent: Track

    private val vm by viewModel<TrackInfoViewModel> {
        parametersOf(trackIntent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_info)

        play = findViewById(R.id.playButtonTrackInfo)
        backButton = findViewById(R.id.backFromTrackInfo)
        trackName = findViewById(R.id.songNameTrackInfo)
        authorName = findViewById(R.id.authorNameTrackInfo)
        trackLength = findViewById(R.id.songLengthTrackInfo)
        trackImage = findViewById(R.id.mainTrackInfoImage)
        collectionName = findViewById(R.id.songAlbumTrackInfo)
        releaseDate = findViewById(R.id.songYearTrackInfo)
        primaryGenreName = findViewById(R.id.songGenreTrackInfo)
        country = findViewById(R.id.songCountryTrackInfo)
        timerTextView = findViewById(R.id.songCurrentTimeTrackInfo)
        likeButton = findViewById(R.id.likeSongTrackInfo)
        addToPlaylist = findViewById(R.id.addToFavorTrackInfo)
        bottomSheet = findViewById(R.id.standard_bottom_sheet)
        playlistRecycler = findViewById(R.id.playlistRecyclerBS)
        createPlaylist = findViewById(R.id.addNewPlaylist)
        fragmentContainer = findViewById(R.id.fragment_container)
        constraintLayout = findViewById(R.id.constraintLayout)

        trackIntent = intent.getParcelableExtra("track")!!

        vm.getLikedStatusLiveData().observe(this) { status ->
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
                true -> vm.deleteTrackFromFavor(trackIntent)
                false -> vm.addTrackToFavor(trackIntent)
            }
        }

        val trackInfoDetails = TrackDetailsMapper.map(trackIntent)
        showTrackDetails(trackInfoDetails)

        vm.loadPlayer()

        vm.getPlayerState().observe(this) { state ->
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
            finish()
        }

        //РАБОТА С BottomSheet
        listOfPlaylists = mutableListOf()
        bottomSheetAdapter = BottomSheetAdapter(listOfPlaylists)
        playlistRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        vm.playlists.observe(this) { playlists ->
            if (playlists.isEmpty()) {

            } else {
                listOfPlaylists = playlists
                println(listOfPlaylists)
                bottomSheetAdapter = BottomSheetAdapter(listOfPlaylists)
                playlistRecycler.adapter = bottomSheetAdapter
                bottomSheetAdapter.notifyDataSetChanged()
            }
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        addToPlaylist.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HALF_EXPANDED
        }

        createPlaylist.setOnClickListener {
            constraintLayout.visibility = View.GONE
            bottomSheet.visibility = View.GONE
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, FragmentCreatePlaylist())
                .addToBackStack(null)
                .commit()
        }

    }

    private fun showTrackDetails(trackInfoDetails: TrackDetailsInfo) {
        previewUrlIntent = trackInfoDetails.previewUrl.toString()
        trackName.text = trackInfoDetails.trackName
        authorName.text = trackInfoDetails.artistName
        trackLength.text = trackInfoDetails.trackTimeMillis
        Glide.with(trackImage)
            .load(trackInfoDetails.artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, this))).into(trackImage)
        if (!trackInfoDetails.collectionName.isNullOrEmpty()) collectionName.text =
            trackInfoDetails.collectionName
        releaseDate.text = trackInfoDetails.releaseDate!!.subSequence(IntRange(0, 3))
        primaryGenreName.text = trackInfoDetails.primaryGenreName
        country.text = trackInfoDetails.country
    }

    override fun onDestroy() {
        super.onDestroy()
        vm.releasePlayer()
    }

    override fun onStart() {
        super.onStart()
        vm.getPlaylists()
    }

    private fun preparePlayer() {
        play.setImageResource(R.drawable.play_song_button_mediateka)
        timerTextView.text = "00:00"
    }

    private fun showPlayButton() {
        play.setImageResource(R.drawable.play_song_button_mediateka)
    }

    private fun showPauseButton() {
        play.setImageResource(R.drawable.pause)
    }
}