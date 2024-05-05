package com.practicum.playlistmaker.ui.track_info_activity

import android.os.Bundle
import android.os.Looper
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import android.os.Handler
import com.practicum.playlistmaker.Creator
import com.practicum.playlistmaker.DateTimeUtil
import com.practicum.playlistmaker.R

import com.practicum.playlistmaker.domain.models.PlayerState
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.presentation.mapper.TrackDetailsMapper
import com.practicum.playlistmaker.presentation.models.TrackDetailsInfo
import java.text.SimpleDateFormat
import java.util.Locale


class TrackInfoActivity : AppCompatActivity() {


    private lateinit var previewUrlIntent: String
    private lateinit var runnable: Runnable

    private var mainThreadHandler: Handler? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

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

    private val provideMediaPlayerInteractor = Creator.provideMediaPlayerInteractor()
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

        mainThreadHandler = Handler(Looper.getMainLooper())
        runnable = songTimer()

        val trackInfoDetails = TrackDetailsMapper.map(trackIntent)
        showTrackDetails(trackInfoDetails)

        preparePlayer()


        play.setOnClickListener {
            playbackControl()
            startTimer()
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

    private fun startTimer() {

        mainThreadHandler?.postDelayed(
            songTimer(),
            1000L
        )


    }

    private fun songTimer(): Runnable {
        return object : Runnable {
            override fun run() {

                try {
                    timerTextView.text =
                        dateFormat.format(provideMediaPlayerInteractor.getCurrentPosition())

                    mainThreadHandler?.postDelayed(this, 100L)

                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }

            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
        play.setImageResource(R.drawable.play_song_button_mediateka)
    }

    override fun onDestroy() {
        super.onDestroy()
        provideMediaPlayerInteractor.releasePlayer()
        mainThreadHandler?.removeCallbacks(runnable)
    }

    private fun preparePlayer() {
        provideMediaPlayerInteractor.preparePlayer(previewUrlIntent)
    }

    private fun startPlayer() {
        provideMediaPlayerInteractor.play()
    }

    private fun pausePlayer() {
        provideMediaPlayerInteractor.pause()
    }

    private fun playbackControl() {
        when (provideMediaPlayerInteractor.getState()) {
            PlayerState.PLAYING -> {
                pausePlayer()
                play.setImageResource(R.drawable.play_song_button_mediateka)
            }

            PlayerState.PREPARED, PlayerState.PAUSED -> {
                startPlayer()
                play.setImageResource(R.drawable.pause)

            }

            else -> {}
        }
    }
}