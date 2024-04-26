package com.practicum.playlistmaker

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Looper
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import android.os.Handler
import android.widget.ThemedSpinnerAdapter
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.properties.Delegates

class TrackInfoActivity : AppCompatActivity() {
    private lateinit var play: ImageButton
    private lateinit var timerTextView: TextView

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
    }

    private lateinit var previewUrlIntent: String
    private var playerState = STATE_DEFAULT
    private var mediaPlayer = MediaPlayer()

    private var mainThreadHandler: Handler? = null
    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_info)

        play = findViewById(R.id.playButtonTrackInfo)
        val backButton = findViewById<ImageView>(R.id.backFromTrackInfo)
        val trackName = findViewById<TextView>(R.id.songNameTrackInfo)
        val authorName = findViewById<TextView>(R.id.authorNameTrackInfo)
        val trackLength = findViewById<TextView>(R.id.songLengthTrackInfo)
        val trackImage = findViewById<ImageView>(R.id.mainTrackInfoImage)
        val collectionName = findViewById<TextView>(R.id.songAlbumTrackInfo)
        val releaseDate = findViewById<TextView>(R.id.songYearTrackInfo)
        val primaryGenreName = findViewById<TextView>(R.id.songGenreTrackInfo)
        val country = findViewById<TextView>(R.id.songCountryTrackInfo)
        timerTextView = findViewById(R.id.songCurrentTimeTrackInfo)


        val trackIntent: Track = intent.getParcelableExtra("track")!!

        mainThreadHandler = Handler(Looper.getMainLooper())

        previewUrlIntent = trackIntent.previewUrl.toString()
        trackName.text = trackIntent.trackName
        authorName.text = trackIntent.artistName
        trackLength.text = DateTimeUtil.timeConvert(trackIntent.trackTimeMillis!!.toLong())
        Glide.with(trackImage)
            .load(trackIntent.artworkUrl100!!.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, this)))
            .into(trackImage)
        if (!trackIntent.collectionName.isNullOrEmpty()) collectionName.text =
            trackIntent.collectionName
        releaseDate.text = trackIntent.releaseDate!!.subSequence(IntRange(0, 3))
        primaryGenreName.text = trackIntent.primaryGenreName
        country.text = trackIntent.country

        preparePlayer()

        play.setOnClickListener {
            playbackControl()
            startTimer()
        }

        backButton.setOnClickListener {
            finish()

        }


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
                    if ((mediaPlayer.currentPosition <= mediaPlayer.duration) and (mediaPlayer.isPlaying)) {

                        timerTextView.text = dateFormat.format(mediaPlayer.currentPosition)

                        mainThreadHandler?.postDelayed(this, 500L)
                    }

                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun preparePlayer() {
        mediaPlayer.setDataSource(previewUrlIntent)
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            play.isEnabled = true
            playerState = STATE_PREPARED
        }
        mediaPlayer.setOnCompletionListener {
            playerState = STATE_PREPARED
            timerTextView.text = getString(R.string.songZero)
            play.setImageResource(R.drawable.play_song_button_mediateka)
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()
        playerState = STATE_PLAYING
        play.setImageResource(R.drawable.pause)
    }

    private fun pausePlayer() {
        mediaPlayer.pause()
        playerState = STATE_PAUSED
        play.setImageResource(R.drawable.play_song_button_mediateka)
    }

    private fun playbackControl() {
        when (playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }

            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }
}