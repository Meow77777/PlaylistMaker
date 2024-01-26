package com.practicum.playlistmaker

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CompoundButton
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonSettingsBack = findViewById<ImageView>(R.id.settingsBack)
        val buttonUserAgreement = findViewById<FrameLayout>(R.id.userAgreement)
        val buttonShare = findViewById<FrameLayout>(R.id.shareApp)
        val supportButton = findViewById<FrameLayout>(R.id.support)
        val switchThemeButton = findViewById<Switch>(R.id.switchTheme)


        buttonSettingsBack.setOnClickListener {
            finish()
        }

        switchThemeButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        buttonUserAgreement.setOnClickListener {
            val linkAgreement: String = resources.getString(R.string.agreementLink)
            val url = Uri.parse(linkAgreement)
            val intentAgreement = Intent(Intent.ACTION_VIEW, url)
            startActivity(intentAgreement)
        }

        buttonShare.setOnClickListener {
            val linkAndroid: String = resources.getString(R.string.shareLink)
            val share: String = resources.getString(R.string.shareApp)
            val intentShare = Intent(Intent.ACTION_SEND)
            intentShare.setType(resources.getString(R.string.textPlain))
            intentShare.putExtra(
                Intent.EXTRA_TEXT,
                linkAndroid
            )
            startActivity(Intent.createChooser(intentShare, share))
        }

        supportButton.setOnClickListener {
            val email: String = resources.getString(R.string.myEmail)
            val message: String = resources.getString(R.string.supportMessageText)
            val subject: String = resources.getString(R.string.supportMessageSubject)
            val intentSupport = Intent(Intent.ACTION_SENDTO)
            intentSupport.data = Uri.parse(resources.getString(R.string.mailto))
            intentSupport.putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            intentSupport.putExtra(Intent.EXTRA_TEXT, message)
            intentSupport.putExtra(Intent.EXTRA_SUBJECT, subject)
            startActivity(intentSupport)
        }

    }
}