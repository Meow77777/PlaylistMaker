package com.practicum.playlistmaker.search.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.player.ui.TrackInfoActivity
import com.practicum.playlistmaker.search.models.Track
import com.practicum.playlistmaker.search.presentation.SearchTracksViewModel
import com.practicum.playlistmaker.search.presentation.state.TracksState


class SearchActivity : AppCompatActivity() {

    companion object {
        private const val EDIT_TEXT = "TEXT"
        private const val EDIT_TEXT_ENTER = ""
        const val HISTORY_KEY = "key_for_history_search"
        const val PREFS_HISTORY = "prefs_history"
    }

    lateinit var editText: EditText

    private val tracks = ArrayList<Track>()

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: SongSearchAdapter
    lateinit var placeholderText: TextView
    lateinit var placeholderImageNoInternet: ImageView
    lateinit var placeholderImageNothingFound: ImageView
    lateinit var placeholderResetButton: Button
    lateinit var recycler: RecyclerView
    lateinit var recyclerSearchHistory: RecyclerView
    lateinit var progressBar: ProgressBar
    lateinit var youSearchedText: TextView
    private lateinit var deleteSearchHistory: Button

    private lateinit var adapterHistory: AdapterHistoryTracks


    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

    private lateinit var viewModel: SearchTracksViewModel
    private lateinit var searchText: String
    private lateinit var tracksListHistory: MutableList<Track>

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poisk)
        recycler = findViewById<RecyclerView>(R.id.listView)
        val buttonSearchBack = findViewById<ImageView>(R.id.settingsBackFromSearch)
        editText = findViewById<EditText>(R.id.editText)
        val buttonSearchDelete = findViewById<ImageView>(R.id.deleteSearchButton)
        placeholderImageNoInternet = findViewById<ImageView>(R.id.noInternetImage)
        placeholderImageNothingFound = findViewById<ImageView>(R.id.nothingFoundImage)
        placeholderText = findViewById<TextView>(R.id.placeholderText)
        placeholderResetButton = findViewById<Button>(R.id.placeholderResetButton)
        youSearchedText = findViewById<TextView>(R.id.youSearched)
        recyclerSearchHistory = findViewById<RecyclerView>(R.id.recyclerSearchHistory)
        deleteSearchHistory = findViewById<Button>(R.id.deleteSearchHistory)
        progressBar = findViewById(R.id.progressBar)

        viewModel = ViewModelProvider(
            this,
            SearchTracksViewModel.getViewModelFactory()
        )[SearchTracksViewModel::class.java]

        placeholderImageNoInternet.isVisible = false
        placeholderImageNothingFound.isVisible = false
        placeholderText.isVisible = false
        placeholderResetButton.isVisible = false
        youSearchedText.isVisible = false
        deleteSearchHistory.isVisible = false
        recyclerSearchHistory.isVisible = false

        sharedPreferences = getSharedPreferences(PREFS_HISTORY, MODE_PRIVATE)
        val sharedPreferences: SharedPreferences = getSharedPreferences(HISTORY_KEY, MODE_PRIVATE)

        tracksListHistory = mutableListOf()

        viewModel.getHistoryTracksLiveData().observe(this) { listOfHistoryTracks ->
            tracksListHistory = listOfHistoryTracks
        }

        //ADAPTER HISTORY
        adapterHistory =
            AdapterHistoryTracks(tracksListHistory, object : SongSearchAdapter.Listener {
                override fun onClick(track: Track) {
                    if (clickDebounce()) {
                        val trackInfoActivityIntent =
                            Intent(this@SearchActivity, TrackInfoActivity::class.java)
                        trackInfoActivityIntent.putExtra("track", track)
                        startActivity(trackInfoActivityIntent)
                    }
                }

            })


        //ADAPTER
        adapter = SongSearchAdapter(tracks, sharedPreferences, object : SongSearchAdapter.Listener {
            override fun onClick(track: Track) {
                if (clickDebounce()) {
                    val trackInfoActivityIntent =
                        Intent(this@SearchActivity, TrackInfoActivity::class.java)
                    trackInfoActivityIntent.putExtra("track", track)
                    viewModel.addToHistory(track = track)
                    startActivity(trackInfoActivityIntent)
                }
            }
        })
        recycler.adapter = adapter



        recyclerSearchHistory.adapter = adapterHistory
        recyclerSearchHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        editText.setOnFocusChangeListener { _, hasFocus ->
            youSearchedText.visibility =
                if (hasFocus && editText.text.isEmpty() && tracksListHistory.isNotEmpty()) View.VISIBLE else View.GONE
            if (hasFocus && editText.text.isEmpty() && tracksListHistory.isNotEmpty()){
                notifyHistoryAdapter()
                recyclerSearchHistory.visibility = View.VISIBLE
            } else{
                View.GONE
            }
            deleteSearchHistory.visibility =
                if (hasFocus && editText.text.isEmpty() && tracksListHistory.isNotEmpty()) View.VISIBLE else View.GONE
        }
           deleteSearchHistory.setOnClickListener {
            tracksListHistory.clear()
            sharedPreferences.edit()
                .putString(HISTORY_KEY, Gson().toJson(tracksListHistory)).apply()
            adapterHistory.notifyDataSetChanged()
            youSearchedText.isVisible = false
            deleteSearchHistory.isVisible = false
            recyclerSearchHistory.isVisible = false
        }

        editText.isFocusable = true

        buttonSearchDelete.isVisible = false

        if (savedInstanceState != null) {
            editText.setText(savedInstanceState.getString(EDIT_TEXT, enterEditText))
        }

        buttonSearchBack.setOnClickListener {
            finish()
        }

        buttonSearchDelete.setOnClickListener {
            editText.text.clear()
            hidePlaceholder()
            tracks.clear()
            notifyHistoryAdapter()
            adapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.searchDebounce(changedText = p0?.toString() ?: "")
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

        viewModel.observeState().observe(this) {
            render(it)
        }

        placeholderResetButton.setOnClickListener {
            viewModel.searchDebounce(changedText = searchText)
            hidePlaceholder()
        }

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
        val tracksListHistory = viewModel.getTracksHistory()
        adapterHistory =
            AdapterHistoryTracks(tracksListHistory, object : SongSearchAdapter.Listener {
                override fun onClick(track: Track) {
                    if (clickDebounce()) {
                        val trackInfoActivityIntent =
                            Intent(this@SearchActivity, TrackInfoActivity::class.java)
                        trackInfoActivityIntent.putExtra("track", track)
                        startActivity(trackInfoActivityIntent)
                    }
                }
            })
        recyclerSearchHistory.adapter = adapterHistory
        adapterHistory.notifyDataSetChanged()
    }

    private fun closeKeyboard(view: View) {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private var enterEditText: String = EDIT_TEXT_ENTER
    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(EDIT_TEXT, enterEditText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        enterEditText = savedInstanceState.getString(EDIT_TEXT, enterEditText)
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
