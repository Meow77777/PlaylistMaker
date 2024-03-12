package com.practicum.playlistmaker

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken

class TrackInfoActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.track_info)

        val backButton = findViewById<ImageView>(R.id.backFromTrackInfo)
        val trackName = findViewById<TextView>(R.id.songNameTrackInfo)
        val authorName = findViewById<TextView>(R.id.authorNameTrackInfo)
        val trackLength = findViewById<TextView>(R.id.songLengthTrackInfo)
        val trackImage = findViewById<ImageView>(R.id.mainTrackInfoImage)
        val collectionName = findViewById<TextView>(R.id.songAlbumTrackInfo)
        val releaseDate = findViewById<TextView>(R.id.songYearTrackInfo)
        val primaryGenreName = findViewById<TextView>(R.id.songGenreTrackInfo)
        val country = findViewById<TextView>(R.id.songCountryTrackInfo)

        val trackNameIntent = intent.getStringExtra("trackName")
        val authorNameIntent = intent.getStringExtra("authorName")
        val trackLengthIntent = intent.getStringExtra("trackLength")
        val trackImageIntent = intent.getStringExtra("trackImage")
        val collectionNameIntent = intent.getStringExtra("collectionName")
        val releaseDateIntent = intent.getStringExtra("releaseDate")
        val primaryGenreNameIntent = intent.getStringExtra("primaryGenreName")
        val countryIntent = intent.getStringExtra("country")

        trackName.text = trackNameIntent
        authorName.text = authorNameIntent
        trackLength.text = DateTimeUtil.timeConvert(trackLengthIntent!!.toLong())
        Glide.with(trackImage).load(trackImageIntent!!.replaceAfterLast('/', "512x512bb.jpg"))
            .centerCrop()
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, this)))
            .into(trackImage)
        if (!collectionNameIntent.isNullOrEmpty()) collectionName.text = collectionNameIntent
        releaseDate.text = releaseDateIntent!!.subSequence(IntRange(0, 3))
        primaryGenreName.text = primaryGenreNameIntent
        country.text = countryIntent

        backButton.setOnClickListener {
            finish()
        }


    }
}