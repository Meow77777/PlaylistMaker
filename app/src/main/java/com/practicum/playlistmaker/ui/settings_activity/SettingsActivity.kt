package com.practicum.playlistmaker.ui.settings_activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.App
import com.practicum.playlistmaker.R

const val MY_PREFS: String = "switch_prefs"
const val SWITCH_STATUS: String = "switch_status"

class SettingsActivity : AppCompatActivity() {

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val buttonSettingsBack = findViewById<ImageView>(R.id.settingsBack)
        val buttonUserAgreement = findViewById<FrameLayout>(R.id.userAgreement)
        val buttonShare = findViewById<FrameLayout>(R.id.shareApp)
        val supportButton = findViewById<FrameLayout>(R.id.support)
        val switchThemeButton = findViewById<Switch>(R.id.switchTheme)

        val switchStatus: Boolean


        val prefsSetting = getSharedPreferences(MY_PREFS, MODE_PRIVATE)
        val editorSettings = prefsSetting.edit()

        switchStatus = prefsSetting.getBoolean(SWITCH_STATUS, false)


        switchThemeButton.isChecked = switchStatus

        switchThemeButton.setOnCheckedChangeListener { switcher, checked ->
            if (checked) {
                editorSettings.putBoolean(SWITCH_STATUS, true)
                editorSettings.apply()
                switchThemeButton.isChecked = true
            } else {
                editorSettings.putBoolean(SWITCH_STATUS, false)
                editorSettings.apply()
                switchThemeButton.isChecked = false
            }
            (applicationContext as App).switchTheme(checked)
        }

        buttonSettingsBack.setOnClickListener {
            finish()
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


}