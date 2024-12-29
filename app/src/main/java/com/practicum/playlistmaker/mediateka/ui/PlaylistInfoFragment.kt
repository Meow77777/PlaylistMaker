package com.practicum.playlistmaker.mediateka.ui

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.PlaylistInfoFragmentBinding
import com.practicum.playlistmaker.mediateka.models.Playlist
import com.practicum.playlistmaker.mediateka.presentation.PlaylistInfoViewModel
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.utils.DateTimeUtil
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistInfoFragment : Fragment() {

    private lateinit var binding: PlaylistInfoFragmentBinding
    private lateinit var playlist: Playlist
    private lateinit var bottomSheetEdit: LinearLayout
    private var isClickAllowed = true

    private val vm by viewModel<PlaylistInfoViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = PlaylistInfoFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bottomSheetEdit = binding.bottomSheetEditPlaylistInfo

        arguments?.getParcelable<Playlist>("updated_playlist")?.let {
            playlist = it
            updatePlaylistUI(it)
        }

        playlist = arguments?.getParcelable<Playlist>("playlist")!!
        showPlaylistDetails(playlist)

        initObservers()

        val location = IntArray(2)
        binding.buttonsLayout.getLocationOnScreen(location)
        val buttonsLayoutY = location[1] // Y-координата кнопки относительно экрана

        val displayMetrics = resources.displayMetrics
        val screenHeightPx = displayMetrics.heightPixels

        val distanceFromBottomPx = screenHeightPx - buttonsLayoutY - binding.buttonsLayout.height

        val finalPeekHeightPx = distanceFromBottomPx
        val finalPeekHeightDp = finalPeekHeightPx / displayMetrics.density

        val bottomSheetBehavior = BottomSheetBehavior.from(binding.bottomSheetPlaylistInfo)
        bottomSheetBehavior.peekHeight = finalPeekHeightDp.toInt()

        bottomSheetBehavior.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {}
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                // Если свайп вниз, добавляем отступ
                if (slideOffset < 0) {
                    bottomSheetBehavior.peekHeight = finalPeekHeightDp.toInt()
                } else if (slideOffset > 0) {
                    // Если свайп вверх, возвращаем к полному раскрытию или нормализуем peekHeight
                    if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
                        bottomSheetBehavior.peekHeight = finalPeekHeightDp.toInt()
                    }
                }
            }
        })

        val bottomSheetBehaviorEdit = BottomSheetBehavior.from(bottomSheetEdit)

        bottomSheetBehaviorEdit.addBottomSheetCallback(object :
            BottomSheetBehavior.BottomSheetCallback() {

            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        binding.darkFrame.visibility = View.VISIBLE
                    }

                    BottomSheetBehavior.STATE_HIDDEN -> {
                        binding.darkFrame.visibility = View.GONE
                    }

                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.darkFrame.visibility = View.GONE
            }
        })

        binding.sharePlaylist.setOnClickListener {
            sharePlaylist(requireContext(), playlist)
        }

        binding.editPlaylist.setOnClickListener {
            binding.bottomSheetEditPlaylistInfo.visibility = View.VISIBLE
            bottomSheetBehaviorEdit.state = BottomSheetBehavior.STATE_HALF_EXPANDED
            binding.playlistInfoSheet.playlistNameSheet.text = playlist.name
            binding.playlistInfoSheet.playlistCountTracksSheet.text =
                getTracksCountString(playlist.tracks)
            Glide.with(requireContext())
                .load(playlist.image?.toUri())
                .placeholder(R.drawable.placeholder)
                .transform(RoundedCorners(DateTimeUtil.dpToPx(2f, requireContext())))
                .into(binding.playlistInfoSheet.playlistImageSheet)
        }

        binding.share.setOnClickListener {
            sharePlaylist(requireContext(), playlist)
        }

        binding.edit.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("playlist_to_edit", playlist)
            }
            findNavController().navigate(R.id.fragmentCreatePlaylist, bundle)
        }

        binding.delete.setOnClickListener {
            showDeletePlaylistDialog(requireContext())
        }

        binding.backFromPlaylistInfo.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updatePlaylistUI(updatedPlaylist: Playlist) {
        // Обновляем UI с новыми данными
        binding.PlaylistName.text = updatedPlaylist.name
        binding.PlaylistDescription.text = updatedPlaylist.description
        Glide.with(requireContext()).load(updatedPlaylist.image?.toUri())
            .placeholder(R.drawable.placeholder)
            .transform(RoundedCorners(DateTimeUtil.dpToPx(8f, requireContext())))
            .into(binding.PlaylistImage)
    }

    private fun sharePlaylist(context: Context, playlist: Playlist) {
        // Проверка, есть ли хотя бы один трек в плейлисте
        if (playlist.tracks.isEmpty()) {
            Toast.makeText(
                requireContext(), R.string.no_tracks_in_playlist, Toast.LENGTH_LONG
            ).show()
            return
        }

        val playlistInfo = buildPlaylistInfo(playlist)

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, playlistInfo)
        }
        binding.darkFrame.visibility = View.VISIBLE
        context.startActivity(Intent.createChooser(shareIntent, getString(R.string.share_playlist)))
    }

    private fun buildPlaylistInfo(playlist: Playlist): String {
        val header = "${playlist.name}\n${playlist.description}\n${binding.tracksCount.text}\n"

        val tracksList = playlist.tracks.mapIndexed { index, track ->
            val trackName = track.trackName
            val artistName = track.artistName
            val trackDuration = track.trackTimeMillis?.let { formatDuration(it) }
            "${index + 1}. $artistName - $trackName ($trackDuration)"
        }.joinToString("\n")

        return header + tracksList
    }

    private fun formatDuration(durationMillis: String): String {
        val duration = durationMillis.toLong()
        val minutes = duration / 60000
        val seconds = (duration % 60000) / 1000
        return String.format("%02d:%02d", minutes, seconds)
    }

    fun showDeleteTrackDialog(context: Context, track: Track) {
        AlertDialog.Builder(context).setTitle(R.string.delete_track)
            .setMessage(R.string.delete_track_question)
            .setPositiveButton(R.string.delete) { dialog, _ ->
                vm.deleteTrack(
                    playlist = playlist, track = track
                )
                dialog.dismiss()
                binding.playlistTracksRecyclerBS.adapter?.notifyDataSetChanged()
            }.setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun showDeletePlaylistDialog(context: Context) {
        AlertDialog.Builder(context).setTitle(R.string.delete_playlist)
            .setMessage(R.string.delete_playlist_question)
            .setPositiveButton(R.string.yes) { dialog, _ ->
                vm.deletePlaylist(
                    playlist = playlist
                )
                dialog.dismiss()
                findNavController().popBackStack()
            }.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }.show()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.totalDuration.collect { duration ->
                    binding.sumLength.text = duration
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.totalCount.collect { count ->
                    binding.tracksCount.text = count
                }
            }
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                vm.totalCountInt.collect { countInt ->
                    if (countInt == 0) {
                        binding.bottomSheetPlaylistInfo.visibility = View.GONE
                    } else {
                        binding.bottomSheetPlaylistInfo.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showPlaylistDetails(playlist: Playlist) {
        loadImageOrPlaceholder(requireContext(), binding.PlaylistImage)
        binding.PlaylistName.text = playlist.name
        binding.PlaylistDescription.text = playlist.description
        binding.sumLength.text = getTotalDurationString(playlist.tracks)
        binding.tracksCount.text = getTracksCountString(playlist.tracks)
        if (playlist.tracks.size != 0) {
            binding.bottomSheetPlaylistInfo.visibility = View.VISIBLE
            binding.playlistTracksRecyclerBS.layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            binding.playlistTracksRecyclerBS.adapter = BottomSheetAdapterTracks(playlist.tracks,
                object : BottomSheetAdapterTracks.Listener {
                    override fun onClick(track: Track) {
                        if (clickDebounce()) {
                            val bundle = Bundle().apply {
                                putParcelable("track", track)
                            }
                            findNavController().navigate(
                                R.id.trackInfoFragment, bundle
                            )
                        }
                    }

                    override fun onLongClick(track: Track) {
                        showDeleteTrackDialog(requireContext(), track)
                    }
                })
        } else {
            binding.bottomSheetPlaylistInfo.visibility = View.GONE
        }
    }

    private fun loadImageOrPlaceholder(context: Context, imageView: ImageView) {
        Glide.with(context).load(playlist.image?.toUri()).placeholder(R.drawable.placeholder)
            .into(imageView)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            lifecycleScope.launch {
                delay(1000L)
                isClickAllowed = true
            }
        }
        return current
    }

    private fun getTracksCountString(tracks: List<Track>): String {
        val tracksCount = tracks.size

        // Применяем правила склонения для слова "трек"
        val tracksWord = when {
            tracksCount % 10 == 1 && tracksCount % 100 != 11 -> "трек"  // 1 трек
            tracksCount % 10 in 2..4 && (tracksCount % 100 !in 12..14) -> "трека"  // 2-4 трека
            else -> "треков"  // 5 и более треков
        }

        // Форматируем строку для отображения
        return "$tracksCount $tracksWord"
    }

    private fun getTotalDurationString(tracks: List<Track>): String {
        // Суммируем продолжительности всех треков (преобразуем строку в Long)
        val totalDurationMillis = tracks.sumOf { it.trackTimeMillis?.toLongOrNull() ?: 0L }

        val totalMinutes = totalDurationMillis / 60000

        val minutesWord = when {
            (totalMinutes % 10).toInt() == 1 && (totalMinutes % 100).toInt() != 11 -> "минута"  // 1 минута
            totalMinutes % 10 in 2..4 && (totalMinutes % 100 !in 12..14) -> "минуты"  // 2-4 минуты
            else -> "минут"  // 5 и более минут
        }

        // Форматируем строку для отображения
        return "$totalMinutes $minutesWord"
    }

    override fun onResume() {
        super.onResume()
        binding.darkFrame.visibility = View.GONE
    }

}
