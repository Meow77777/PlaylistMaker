package com.practicum.playlistmaker.settings.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import com.practicum.playlistmaker.sharing.model.DataEmail

const val MY_PREFS: String = "switch_prefs"
const val SWITCH_STATUS: String = "switch_status"

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsViewModel
    private lateinit var urlShareLink: String
    private lateinit var urlTermsLink: String
    private lateinit var emailDataForm: DataEmail

    @SuppressLint("UseSwitchCompatOrMaterialCode")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        viewModel = SettingsViewModel()

        val buttonSettingsBack = findViewById<ImageView>(R.id.settingsBack)
        val buttonUserAgreement = findViewById<FrameLayout>(R.id.userAgreement)
        val buttonShare = findViewById<FrameLayout>(R.id.shareApp)
        val supportButton = findViewById<FrameLayout>(R.id.support)
        val switchThemeButton = findViewById<Switch>(R.id.switchTheme)

        viewModel.getSettingsDarkThemeLiveData().observe(this) { themeStatus ->
            switchThemeButton.isChecked = themeStatus
        }

        switchThemeButton.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                viewModel.changeDarkTheme(true)
            } else {
                viewModel.changeDarkTheme(false)
            }
        }

        buttonSettingsBack.setOnClickListener {
            finish()
        }

        viewModel.getShareLinkLiveData().observe(this) { link ->
            urlShareLink = link
        }

        viewModel.getTermsLinkLiveData().observe(this) { link ->
            urlTermsLink = link
        }

        viewModel.getSupportEmailLiveData().observe(this) { emailForm ->
            emailDataForm = emailForm
        }

        buttonUserAgreement.setOnClickListener {
            val intentAgreement = Intent(Intent.ACTION_VIEW, Uri.parse(urlTermsLink))
            startActivity(intentAgreement)
        }

        buttonShare.setOnClickListener {
            Intent(Intent.ACTION_SEND).apply {
                val share: String = resources.getString(R.string.shareApp)
                type = resources.getString(R.string.textPlain)
                putExtra(Intent.EXTRA_TEXT, urlShareLink)
                startActivity(Intent.createChooser(this, share))
            }
        }

        supportButton.setOnClickListener {
            Intent(Intent.ACTION_SENDTO).apply {
                data = emailDataForm.adress
                putExtra(Intent.EXTRA_EMAIL, arrayOf(emailDataForm.email))
                putExtra(Intent.EXTRA_TEXT, emailDataForm.message)
                putExtra(Intent.EXTRA_SUBJECT, emailDataForm.subject)
                startActivity(this)
            }
        }
    }

}