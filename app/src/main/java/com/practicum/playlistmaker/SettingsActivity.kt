package com.practicum.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.BoringLayout
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

        val switchStatus: Boolean
        val darkStatus: Boolean

        val prefsSetting = getSharedPreferences(MY_PREFS, MODE_PRIVATE)
        val editorSettings = prefsSetting.edit()

        switchStatus = prefsSetting.getBoolean(SWITCH_STATUS, false)
        darkStatus = prefsSetting.getBoolean(DARK_THEME_STATUS, false)

        switchThemeButton.isChecked = switchStatus

        if (darkStatus) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        buttonSettingsBack.setOnClickListener {
            finish()
        }

        switchThemeButton.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                editorSettings.putBoolean(SWITCH_STATUS, true)
                editorSettings.putBoolean(DARK_THEME_STATUS, true)
                editorSettings.apply()
                switchThemeButton.isChecked = true
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                editorSettings.putBoolean(SWITCH_STATUS, false)
                editorSettings.putBoolean(DARK_THEME_STATUS, false)
                editorSettings.apply()
                switchThemeButton.isChecked = false
            }
        }

        buttonUserAgreement.setOnClickListener {
            val linkAgreement: String = resources.getString(R.string.agreementLink)
            val url = Uri.parse(linkAgreement)
            val intentAgreement = Intent(Intent.ACTION_VIEW, url)
            startActivity(intentAgreement)
        }

        buttonShare.setOnClickListener {

            Intent(Intent.ACTION_SEND).apply {
                val linkAndroid: String = resources.getString(R.string.shareLink)
                val share: String = resources.getString(R.string.shareApp)
                type = resources.getString(R.string.textPlain)
                putExtra(Intent.EXTRA_TEXT, linkAndroid)
                startActivity(Intent.createChooser(this, share))
            }
        }

        supportButton.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                val email: String = resources.getString(R.string.myEmail)
                val message: String = resources.getString(R.string.supportMessageText)
                val subject: String = resources.getString(R.string.supportMessageSubject)
                data = Uri.parse(resources.getString(R.string.mailto))
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_TEXT, message)
                putExtra(Intent.EXTRA_SUBJECT, subject)
                startActivity(this)
            }
        }

    }

    companion object {
        const val MY_PREFS: String = "switch_prefs"
        const val SWITCH_STATUS: String = "switch_status"
        const val DARK_THEME_STATUS: String = "light_theme_status"
    }
}