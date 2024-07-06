package com.practicum.playlistmaker.settings.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentSettingsBinding
import com.practicum.playlistmaker.settings.presentation.SettingsViewModel
import com.practicum.playlistmaker.sharing.model.DataEmail
import com.practicum.playlistmaker.utils.App
import org.koin.androidx.viewmodel.ext.android.viewModel

const val MY_PREFS: String = "switch_prefs"
const val SWITCH_STATUS: String = "switch_status"

class SettingsFragment : Fragment() {


    private lateinit var urlShareLink: String
    private lateinit var urlTermsLink: String
    private lateinit var emailDataForm: DataEmail

    private val vm by viewModel<SettingsViewModel>()

    private lateinit var binding: FragmentSettingsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val buttonUserAgreement = binding.userAgreement
        val buttonShare = binding.shareApp
        val supportButton = binding.support
        val switchThemeButton = binding.switchTheme

        vm.getSettingsDarkThemeLiveData().observe(this) { themeStatus ->
            switchThemeButton.isChecked = themeStatus
        }

        switchThemeButton.setOnCheckedChangeListener { _, checked ->
            vm.changeDarkTheme(checked)
            (activity?.application as App).switchTheme(checked)
        }

        vm.getShareLinkLiveData().observe(viewLifecycleOwner) { link ->
            urlShareLink = link
        }

        vm.getTermsLinkLiveData().observe(viewLifecycleOwner) { link ->
            urlTermsLink = link
        }

        vm.getSupportEmailLiveData().observe(viewLifecycleOwner) { emailForm ->
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