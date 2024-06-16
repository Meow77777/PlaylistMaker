package com.practicum.playlistmaker.player.ui

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.practicum.playlistmaker.R
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

    private lateinit var trackUrl : String

    private val vm by viewModel<TrackInfoViewModel>{
        parametersOf(trackUrl)
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


        val trackIntent: Track = intent.getParcelableExtra("track")!!
        trackUrl = trackIntent.previewUrl.toString()


        val trackInfoDetails = TrackDetailsMapper.map(trackIntent)
        showTrackDetails(trackInfoDetails)

        vm.getPlayerState().observe(this) { state ->
            when (state) {
                State.Play -> pausePlayer()
                State.Pause -> startPlayer()
                State.Prepared -> startPlayer()
                State.Default -> {}
            }
        }

        vm.getTimerLiveData().observe(this) { time ->
            timerTextView.text = time
        }


        play.setOnClickListener {
            vm.changeState()
        }

        backButton.setOnClickListener {
            finish()
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

    private fun startPlayer() {
        play.setImageResource(R.drawable.pause)
    }

    private fun pausePlayer() {
        play.setImageResource(R.drawable.play_song_button_mediateka)
    }
}