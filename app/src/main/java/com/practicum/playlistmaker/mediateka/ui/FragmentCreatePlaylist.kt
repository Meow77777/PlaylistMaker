package com.practicum.playlistmaker.mediateka.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.mediateka.presentation.FragmentCreatePlaylistViewModel
import com.practicum.playlistmaker.utils.DateTimeUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.io.FileOutputStream

class FragmentCreatePlaylist : Fragment() {

    private lateinit var binding: FragmentCreatePlaylistBinding
    private val vm by viewModel<FragmentCreatePlaylistViewModel>()
    private var imageUri: Uri? = null
    private var hasUnsavedChanges = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistButton.isEnabled = false

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                if (uri != null) {
                    //binding.addPlaylistImage.setImageURI(uri)
                    binding.addPlaylistImage.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(binding.addPlaylistImage).load(uri).fitCenter()
                        .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, requireContext())))
                        .into(binding.addPlaylistImage)

                    imageUri = uri
                    saveImageToPrivateStorage(uri)
                } else {
                    Log.d("PhotoPicker", "No media selected")
                }
            }

        binding.addPlaylistImage.setOnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.editTextNamePlaylist.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                binding.createPlaylistButton.isEnabled = !p0.isNullOrEmpty()
            }

            override fun afterTextChanged(p0: Editable?) {}
        })

        binding.createPlaylistButton.setOnClickListener {
            if (binding.createPlaylistButton.isEnabled) {
                val playlistImageUri = if (imageUri != null) imageUri.toString() else ""
                val newPlaylist = Playlist(
                    name = binding.editTextNamePlaylist.text.toString(),
                    description = binding.editTextDescriptionPlaylist.text.toString(),
                    image = playlistImageUri,
                    tracks = mutableListOf()
                )
                vm.addPlaylist(playlist = newPlaylist)

                val navController = findNavController()

                Toast.makeText(
                    requireContext(),
                    "Плейлист ${binding.editTextNamePlaylist.text.toString()} создан",
                    Toast.LENGTH_LONG
                ).show()

                navController.popBackStack()

            }
        }

        binding.backFromCreatePlaylist.setOnClickListener {
            if ((binding.editTextNamePlaylist.text?.isNotEmpty() == true) or (binding.editTextDescriptionPlaylist.text?.isNotEmpty() == true) or (binding.addPlaylistImage.drawable != null)) {
                showDialog()
            } else {
                findNavController().popBackStack()
            }
        }

        binding.editTextNamePlaylist.addTextChangedListener {
            hasUnsavedChanges = !it.isNullOrEmpty()
        }

        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (hasUnsavedChanges) {
                        showDialog()
                    } else {
                        isEnabled = false
                        requireActivity().onBackPressed()
                    }
                }
            }
        )
    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext()).setTitle(R.string.finishPlaylistCreating)
            .setMessage(R.string.dataWillLost).setNegativeButton(R.string.cancel) { _, _ ->
                //ничего не делаем
            }.setPositiveButton(R.string.complete) { _, _ ->
                findNavController().popBackStack()
            }.show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), "myalbum"
        )
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        val file = File(filePath, "first_cover.jpg")
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val outputStream = FileOutputStream(file)
        BitmapFactory.decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }

}
