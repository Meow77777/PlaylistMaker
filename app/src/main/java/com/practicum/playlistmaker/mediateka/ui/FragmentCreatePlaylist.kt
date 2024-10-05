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
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
    private lateinit var imageUri: Uri

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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
                val newPlaylist = Playlist(
                    name = binding.editTextNamePlaylist.text.toString(),
                    description = binding.editTextDescriptionPlaylist.text.toString(),
                    image = imageUri.toString(),
                    tracks = mutableListOf()
                )
                vm.addPlaylist(playlist = newPlaylist)

                val navController = findNavController()

                // Получаем SavedStateHandle у предыдущего фрагмента через NavController
                navController.previousBackStackEntry?.savedStateHandle?.set(
                    "playlistName",
                    binding.editTextNamePlaylist.text.toString()
                )

                // Возврат на предыдущий фрагмент
                navController.popBackStack()

//                parentFragmentManager.setFragmentResult("playlistCreated", Bundle().apply {
//                    putString("playlistName", binding.editTextNamePlaylist.text.toString())
//                })
//                Toast.makeText(
//                    requireContext(),
//                    "Плейлист ${binding.editTextNamePlaylist.text.toString()} создан",
//                    Toast.LENGTH_LONG
//                ).show()
//                val playlistsFragment =
//                    parentFragmentManager.findFragmentById(R.id.createdPlaylists) as? FragmentPlaylists
//                playlistsFragment?.arguments = Bundle().apply {
//                    putBoolean("showToast", true)
//                    putString("playlistName", binding.editTextNamePlaylist.text.toString())
//                }
//                findNavController().popBackStack()

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
