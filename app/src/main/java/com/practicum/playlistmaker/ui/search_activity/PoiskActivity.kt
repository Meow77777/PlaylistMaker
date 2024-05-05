package com.practicum.playlistmaker.ui.search_activity

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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.data.dto.TrackResponse
import com.practicum.playlistmaker.data.network.TrackApi
import com.practicum.playlistmaker.domain.models.Track
import com.practicum.playlistmaker.ui.track_info_activity.TrackInfoActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PoiskActivity : AppCompatActivity(), SongSearchAdapter.Listener {

    companion object {
        private const val EDIT_TEXT = "TEXT"
        private const val EDIT_TEXT_ENTER = ""
        private const val RESPONSE_SUCCESSFULL: Int = 200
        const val HISTORY_KEY = "key_for_history_search"
        const val PREFS_HISTORY = "prefs_history"
    }

    lateinit var editText: EditText
    private val ITUNES_URL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(TrackApi::class.java)
    private val tracks = ArrayList<Track>()

    lateinit var sharedPreferences: SharedPreferences
    lateinit var adapter: SongSearchAdapter
    lateinit var placeholderText: TextView
    lateinit var placeholderImageNoInternet: ImageView
    lateinit var placeholderImageNothingFound: ImageView
    lateinit var placeholderResetButton: Button
    lateinit var recycler: RecyclerView
    lateinit var recyclerSearchHistory: RecyclerView
    lateinit var progressBar: ProgressBar

    private lateinit var searchHistory: SearchHistory
    lateinit var adapterHistory: AdapterHistoryTracks

    private val searchRunnable = Runnable { searchRequest() }
    private val handler = Handler(Looper.getMainLooper())

    private var isClickAllowed = true

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
        val youSearchedText = findViewById<TextView>(R.id.youSearched)
        recyclerSearchHistory = findViewById<RecyclerView>(R.id.recyclerSearchHistory)
        val deleteSearchHistory = findViewById<Button>(R.id.deleteSearchHistory)
        progressBar = findViewById(R.id.progressBar)



        placeholderImageNoInternet.isVisible = false
        placeholderImageNothingFound.isVisible = false
        placeholderText.isVisible = false
        placeholderResetButton.isVisible = false
        youSearchedText.isVisible = false
        deleteSearchHistory.isVisible = false
        recyclerSearchHistory.isVisible = false

        sharedPreferences = getSharedPreferences(PREFS_HISTORY, MODE_PRIVATE)
        adapter = SongSearchAdapter(tracks, sharedPreferences, this)

        searchHistory = SearchHistory(sharedPreferences)

        recycler.adapter = adapter
        searchHistory.getTracksList()

        adapterHistory = AdapterHistoryTracks(searchHistory.historyList, this)
        recyclerSearchHistory.adapter = adapterHistory
        recyclerSearchHistory.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)



        editText.setOnFocusChangeListener { view, hasFocus ->
            youSearchedText.visibility =
                if (hasFocus && editText.text.isEmpty() && searchHistory.historyList.isNotEmpty()) View.VISIBLE else View.GONE
            recyclerSearchHistory.visibility =
                if (hasFocus && editText.text.isEmpty() && searchHistory.historyList.isNotEmpty()) View.VISIBLE else View.GONE
            deleteSearchHistory.visibility =
                if (hasFocus && editText.text.isEmpty() && searchHistory.historyList.isNotEmpty()) View.VISIBLE else View.GONE
        }





        deleteSearchHistory.setOnClickListener {
            searchHistory.historyList.clear()
            sharedPreferences.edit()
                .putString(HISTORY_KEY, Gson().toJson(searchHistory.historyList))
                .apply()
            adapterHistory.notifyDataSetChanged()
            youSearchedText.isVisible = false
            deleteSearchHistory.isVisible = false
            recyclerSearchHistory.isVisible = false
        }



        editText.setOnEditorActionListener { _, actionId, _ ->
            searchRequest()
            false
        }

        placeholderResetButton.setOnClickListener {
            searchRequest()
            hidePlaceholder()
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
            placeholderResetButton.isVisible = false
            tracks.clear()

            notifyHistoryAdapter()

            adapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchDebounce()
                if (p0.isNullOrEmpty()) {
                    buttonSearchDelete.isVisible = false
                    closeKeyboard(editText)
                    recycler.isVisible = false
                } else {
                    buttonSearchDelete.isVisible = true
                    recycler.isVisible = true
                    enterEditText = editText.text.toString()
                }

                if (editText.hasFocusable() && (p0?.isEmpty() == true) && (searchHistory.historyList.isNotEmpty())) {
                    youSearchedText.isVisible = true
                    deleteSearchHistory.isVisible = true
                    recycler.isVisible = false
                    notifyHistoryAdapter()
                    recyclerSearchHistory.isVisible = true
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

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun notifyHistoryAdapter() {
        searchHistory.getTracksList()
        adapterHistory = AdapterHistoryTracks(searchHistory.historyList, this)
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

    private fun searchRequest() {
        if (editText.text.isNotEmpty()) {
            progressBar.visibility = View.VISIBLE
            recycler.visibility = View.GONE
            iTunesService.search(editText.text.toString())
                .enqueue(object : Callback<TrackResponse> {

                    @SuppressLint("NotifyDataSetChanged")
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {

                        if (response.code() == RESPONSE_SUCCESSFULL) {
                            tracks.clear()
                            hidePlaceholder()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                progressBar.visibility = View.GONE
                                recycler.visibility = View.VISIBLE
                                tracks.addAll(response.body()?.results!!)
                                adapter.notifyDataSetChanged()
                            }
                            if (tracks.isEmpty()) {
                                tracks.clear()
                                placeholderText.isVisible = true
                                placeholderImageNothingFound.isVisible = true
                                placeholderImageNoInternet.isVisible = false
                                placeholderText.text = getString(R.string.nothingFound)
                                adapter.notifyDataSetChanged()
                            }
                        } else {
                            placeholderText.isVisible = true
                            placeholderImageNoInternet.isVisible = true
                            placeholderImageNothingFound.isVisible = false
                            placeholderText.text = getString(R.string.internetProblem)
                            placeholderImageNoInternet.setBackgroundResource(R.drawable.no_internet)
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        progressBar.visibility = View.GONE
                        placeholderText.isVisible = true
                        placeholderImageNoInternet.isVisible = true
                        placeholderImageNothingFound.isVisible = false
                        placeholderResetButton.isVisible = true
                        placeholderText.text = getString(R.string.internetProblem)
                    }
                })
        }
    }

    private fun searchDebounce() {
        handler.postDelayed(searchRunnable, 500)
    }

    private fun clickDebounce(): Boolean {
        val current = isClickAllowed
        if (isClickAllowed) {
            isClickAllowed = false
            handler.postDelayed({ isClickAllowed = true }, 1000L)
        }
        return current
    }

    override fun onClick(track: Track) {
        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_HISTORY, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)
        searchHistory.addTrack(track)
        if (clickDebounce()) {
            val trackInfoActivityIntent = Intent(this, TrackInfoActivity::class.java)
            trackInfoActivityIntent.putExtra("track", track)
            startActivity(trackInfoActivityIntent)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(searchRunnable)
    }

}
