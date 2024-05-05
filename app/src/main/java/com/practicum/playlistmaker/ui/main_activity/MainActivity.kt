package com.practicum.playlistmaker.ui.main_activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.practicum.playlistmaker.ui.mediateka_activity.MediatekaActivity
import com.practicum.playlistmaker.ui.search_activity.PoiskActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.ui.settings_activity.SettingsActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.poisk_button)
        val buttonMediateka = findViewById<Button>(R.id.mediateka_button)
        val buttonSettings = findViewById<Button>(R.id.settings_button)

        buttonSearch.setOnClickListener {
            val poiskIntent = Intent(this, PoiskActivity::class.java)
            startActivity(poiskIntent)
        }

        buttonMediateka.setOnClickListener {
            val mediatekaIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(mediatekaIntent)
        }

        buttonSettings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}