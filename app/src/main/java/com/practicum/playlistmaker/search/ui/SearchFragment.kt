package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.playlistmaker.databinding.FragmentSearchBinding
import com.practicum.playlistmaker.player.ui.TrackInfoActivity
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.presentation.SearchTracksViewModel
import com.practicum.playlistmaker.search.presentation.state.TracksState
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    companion object {
        private const val EDIT_TEXT = "TEXT"
        private const val EDIT_TEXT_ENTER = ""
    }

    lateinit var editText: EditText

    private val tracks = ArrayList<Track>()
    private lateinit var simpleTextWatcher: TextWatcher
    lateinit var adapter: SongSearchAdapter
    lateinit var placeholderText: TextView
    lateinit var placeholderImageNoInternet: ImageView
    lateinit var placeholderImageNothingFound: ImageView
    lateinit var placeholderResetButton: Button
    lateinit var recycler: RecyclerView
    lateinit var recyclerSearchHistory: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var youSearchedText: TextView
    lateinit var buttonSearchDelete: ImageView
    private lateinit var deleteSearchHistory: Button

    private lateinit var adapterHistory: AdapterHistoryTracks


    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    private val vm by viewModel<SearchTracksViewModel>()

    private lateinit var searchText: String
    private lateinit var tracksListHistory: MutableList<Track>

    private lateinit var binding: FragmentSearchBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSearchBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycler = binding.listView
        editText = binding.editText
        buttonSearchDelete = binding.deleteSearchButton
        placeholderImageNoInternet = binding.noInternetImage
        placeholderImageNothingFound = binding.nothingFoundImage
        placeholderText = binding.placeholderText
        placeholderResetButton = binding.placeholderResetButton
        youSearchedText = binding.youSearched
        recyclerSearchHistory = binding.recyclerSearchHistory
        deleteSearchHistory = binding.deleteSearchHistory
        progressBar = binding.progressBar

        placeholderImageNoInternet.isVisible = false
        placeholderImageNothingFound.isVisible = false
        placeholderText.isVisible = false
        placeholderResetButton.isVisible = false
        youSearchedText.isVisible = false
        deleteSearchHistory.isVisible = false
        recyclerSearchHistory.isVisible = false
        recycler.isVisible = false

        tracksListHistory = mutableListOf()

        vm.getHistoryTracksLiveData().observe(viewLifecycleOwner) { listOfHistoryTracks ->
            tracksListHistory = listOfHistoryTracks
        }

        //ADAPTER HISTORY
        adapterHistory =
            AdapterHistoryTracks(tracksListHistory, object : SongSearchAdapter.Listener {
                override fun onClick(track: Track) {
                    if (clickDebounce()) {
                        val trackInfoActivityIntent =
                            Intent(requireContext(), TrackInfoActivity::class.java)
                        trackInfoActivityIntent.putExtra("track", track)
                        startActivity(trackInfoActivityIntent)
                    }
                }

            })


        //ADAPTER
        adapter = SongSearchAdapter(tracks, object : SongSearchAdapter.Listener {
            override fun onClick(track: Track) {
                if (clickDebounce()) {
                    val trackInfoActivityIntent =
                        Intent(requireContext(), TrackInfoActivity::class.java)
                    trackInfoActivityIntent.putExtra("track", track)
                    vm.addToHistory(track = track)
                    startActivity(trackInfoActivityIntent)
                }
            }
        })
        recycler.adapter = adapter

        recyclerSearchHistory.adapter = adapterHistory
        recyclerSearchHistory.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        editText.setOnFocusChangeListener { _, hasFocus ->
            youSearchedText.visibility =
                if (hasFocus && editText.text.isEmpty() && tracksListHistory.isNotEmpty()) View.VISIBLE else View.GONE
            if (hasFocus && editText.text.isEmpty() && tracksListHistory.isNotEmpty()) {
                recyclerSearchHistory.visibility = View.VISIBLE
            } else {
                View.GONE
            }
            deleteSearchHistory.visibility =
                if (hasFocus && editText.text.isEmpty() && tracksListHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }

        //УДАЛЕНИЕ ИСТОРИИ ТРЕКОВ
        deleteSearchHistory.setOnClickListener {
            vm.clearTracksHistory()
            notifyHistoryAdapter()
            youSearchedText.isVisible = false
            deleteSearchHistory.isVisible = false
            recyclerSearchHistory.isVisible = false
        }

        editText.isFocusable = true

        buttonSearchDelete.isVisible = false

        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(EDIT_TEXT, enterEditText))
        }

        //УДАЛЕНИЕ ПОИСКОВОГО ЗАПРОСА
        buttonSearchDelete.setOnClickListener {
            recycler.visibility = View.GONE
            progressBar.visibility = View.GONE
            editText.text.clear()
            hidePlaceholder()
            tracks.clear()
            notifyHistoryAdapter()
            adapter.notifyDataSetChanged()
        }

        simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                vm.searchDebounce(changedText = p0?.toString() ?: "")

                searchText = p0?.toString() ?: ""
                if (p0.isNullOrEmpty()) {
                    buttonSearchDelete.isVisible = false
                    closeKeyboard(editText)
                    recycler.isVisible = false
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    buttonSearchDelete.isVisible = true
                    recycler.isVisible = true
                    enterEditText = editText.text.toString()
                }

                if (editText.hasFocusable() && (p0?.isEmpty() == true) && (tracksListHistory.isNotEmpty())) {
                    youSearchedText.isVisible = true
                    deleteSearchHistory.isVisible = true
                    recycler.isVisible = false
                    placeholderText.visibility = View.GONE
                    placeholderImageNothingFound.visibility = View.GONE
                    placeholderImageNoInternet.visibility = View.GONE
                    placeholderResetButton.visibility = View.GONE
                    notifyHistoryAdapter()
                    recyclerSearchHistory.isVisible = true
                    tracks.clear()
                    adapter.notifyDataSetChanged()
                } else {
                    youSearchedText.isVisible = false
                    deleteSearchHistory.isVisible = false
                    recyclerSearchHistory.isVisible = false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }
        editText.addTextChangedListener(simpleTextWatcher)


        vm.observeState().observe(viewLifecycleOwner) {
            render(it)
        }
        placeholderResetButton.setOnClickListener {
            vm.searchDebounce(changedText = searchText)
            hidePlaceholder()
        }
    }

    override fun onDestroyView() {

        editText.text.clear()
        vm.clearLiveDataTrackState()
        simpleTextWatcher.let { binding.editText.removeTextChangedListener(it) }
        super.onDestroyView()
    }


    private fun render(state: TracksState) {
        when (state) {
            is TracksState.Content -> showContent(state.data)
            is TracksState.Empty -> showEmpty(state.message)
            is TracksState.Error -> showError(state.message)
            is TracksState.Loading -> showLoading()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun showContent(foundTracks: List<Track>) {
        recycler.visibility = View.VISIBLE
        progressBar.visibility = View.GONE

        tracks.clear()
        tracks.addAll(foundTracks)
        adapter.notifyDataSetChanged()


    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showEmpty(emptyMessage: String) {
        recycler.visibility = View.GONE
        placeholderText.visibility = View.VISIBLE
        placeholderImageNothingFound.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
        placeholderImageNoInternet.visibility = View.GONE
        placeholderText.text = emptyMessage
        tracks.clear()
        adapter.notifyDataSetChanged()
    }


    private fun showError(errorMessage: String) {
        recycler.visibility = View.GONE
        placeholderImageNoInternet.visibility = View.VISIBLE
        placeholderText.visibility = View.VISIBLE
        placeholderText.text = errorMessage
        placeholderResetButton.visibility = View.VISIBLE
        progressBar.visibility = View.GONE
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
        placeholderImageNoInternet.isVisible = false
        placeholderImageNothingFound.isVisible = false
        placeholderText.isVisible = false
        placeholderResetButton.isVisible = false
        youSearchedText.isVisible = false
        deleteSearchHistory.isVisible = false
        recyclerSearchHistory.isVisible = false
        recycler.visibility = View.GONE
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyHistoryAdapter() {
        val tracksListHistory = vm.getTracksHistory()
        adapterHistory =
            AdapterHistoryTracks(tracksListHistory, object : SongSearchAdapter.Listener {
                override fun onClick(track: Track) {
                    if (clickDebounce()) {
                        val trackInfoActivityIntent =
                            Intent(requireContext(), TrackInfoActivity::class.java)
                        trackInfoActivityIntent.putExtra("track", track)
                        startActivity(trackInfoActivityIntent)
                    }
                }
            })
        recyclerSearchHistory.adapter = adapterHistory
        adapterHistory.notifyDataSetChanged()
    }

    private fun closeKeyboard(view: View) {
        val inputMethodManager = this.requireActivity()
            .getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private var enterEditText: String = EDIT_TEXT_ENTER
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, enterEditText)
    }

    private fun hidePlaceholder() {
        placeholderImageNothingFound.isVisible = false
        placeholderImageNoInternet.isVisible = false
        placeholderText.isVisible = false
        placeholderResetButton.isVisible = false
    }


    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, 1000L)
        }
        return current
    }

}
