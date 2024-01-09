package com.practicum.playlistmaker

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val poisk = findViewById<Button>(R.id.poisk_button)
        val mediateka = findViewById<Button>(R.id.mediateka_button)
        val settings = findViewById<Button>(R.id.settings_button)

        val poiskIntent = Intent(this, PoiskActivity::class.java)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                startActivity(poiskIntent)
            }
        }
        poisk.setOnClickListener(imageClickListener)

        mediateka.setOnClickListener {
            val mediatekaIntent = Intent(this, MediatekaActivity::class.java)
            startActivity(mediatekaIntent)
        }

        settings.setOnClickListener {
            val settingsIntent = Intent(this, SettingsActivity::class.java)
            startActivity(settingsIntent)
        }
    }
}