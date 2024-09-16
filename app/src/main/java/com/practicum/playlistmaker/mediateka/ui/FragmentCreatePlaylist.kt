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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.practicum.playlistmaker.databinding.FragmentCreatePlaylistBinding
import com.practicum.playlistmaker.utils.DateTimeUtil
import java.io.File
import java.io.FileOutputStream

class FragmentCreatePlaylist : Fragment() {

    private lateinit var binding: FragmentCreatePlaylistBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreatePlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.createPlaylistButton.isEnabled = false

        val pickMedia =
            registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
                //обрабатываем событие выбора пользователем фотографии
                if (uri != null) {
                    //binding.addPlaylistImage.setImageURI(uri)
                    binding.addPlaylistImage.scaleType = ImageView.ScaleType.FIT_XY
                    Glide.with(binding.addPlaylistImage)
                        .load(uri)
                        .fitCenter()
                        .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, requireContext())))
                        .into(binding.addPlaylistImage)

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
                Toast.makeText(
                    requireContext(),
                    "Плейлист ${binding.editTextNamePlaylist.text.toString()} создан",
                    Toast.LENGTH_LONG
                ).show()
                findNavController().popBackStack()
            }
        }

        binding.backFromCreatePlaylist.setOnClickListener {
            if ((binding.editTextNamePlaylist.text?.isNotEmpty() == true) or (binding.editTextDescriptionPlaylist.text?.isNotEmpty() == true) or (binding.addPlaylistImage.drawable != null)) {
                showDialog()
            } else {
                findNavController().popBackStack()
            }
        }





    }

    private fun showDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Завершить создание плейлиста?")
            .setMessage("Все несохраненные данные будут потеряны")
            .setNegativeButton("Отмена") { dialog, which ->
                //ничего не делаем
            }
            .setPositiveButton("Завершить") { dialog, which ->
                findNavController().popBackStack()
            }
            .show()
    }

    private fun saveImageToPrivateStorage(uri: Uri) {
        //создаём экземпляр класса File, который указывает на нужный каталог
        val filePath = File(
            requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "myalbum"
        )
        //создаем каталог, если он не создан
        if (!filePath.exists()) {
            filePath.mkdirs()
        }
        //создаём экземпляр класса File, который указывает на файл внутри каталога
        val file = File(filePath, "first_cover.jpg")
        // создаём входящий поток байтов из выбранной картинки
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        // создаём исходящий поток байтов в созданный выше файл
        val outputStream = FileOutputStream(file)
        // записываем картинку с помощью BitmapFactory
        BitmapFactory
            .decodeStream(inputStream)
            .compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
    }


}
