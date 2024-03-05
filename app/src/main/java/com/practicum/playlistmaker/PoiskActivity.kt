package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class PoiskActivity : AppCompatActivity() {

    companion object {
        private const val EDIT_TEXT = "TEXT"
        private const val EDIT_TEXT_ENTER = ""
        private const val RESPONSE_SUCCESSFULL: Int = 200
        const val HISTORY_KEY = "key_for_history_search"
        const val PREFS_HISTORY = "prefs_history"
    }

    private val ITUNES_URL = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(ITUNES_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val iTunesService = retrofit.create(TrackApi::class.java)
    private val tracks = ArrayList<Track>()


    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poisk)
        val recycler = findViewById<RecyclerView>(R.id.listView)
        val buttonSearchBack = findViewById<ImageView>(R.id.settingsBackFromSearch)
        val editText = findViewById<EditText>(R.id.editText)
        val buttonSearchDelete = findViewById<ImageView>(R.id.deleteSearchButton)
        val placeholderImageNoInternet = findViewById<ImageView>(R.id.noInternetImage)
        val placeholderImageNothingFound = findViewById<ImageView>(R.id.nothingFoundImage)
        val placeholderText = findViewById<TextView>(R.id.placeholderText)
        val placeholderResetButton = findViewById<Button>(R.id.placeholderResetButton)
        val youSearchedText = findViewById<TextView>(R.id.youSearched)
        val recyclerSearchHistory = findViewById<RecyclerView>(R.id.recyclerSearchHistory)
        val deleteSearchHistory = findViewById<Button>(R.id.deleteSearchHistory)


        placeholderImageNoInternet.isVisible = false
        placeholderImageNothingFound.isVisible = false
        placeholderText.isVisible = false
        placeholderResetButton.isVisible = false
        youSearchedText.isVisible = false
        deleteSearchHistory.isVisible = false
        recyclerSearchHistory.isVisible = false


        val sharedPreferences: SharedPreferences = getSharedPreferences(PREFS_HISTORY, MODE_PRIVATE)
        val searchHistory = SearchHistory(sharedPreferences)
        val adapter = SongSearchAdapter(tracks, sharedPreferences)
        recycler.adapter = adapter
        searchHistory.getTracksList()

        val adapterHistory = AdapterHistoryTracks(searchHistory.historyList)
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



        fun hidePlaceholder() {
            placeholderImageNothingFound.isVisible = false
            placeholderImageNoInternet.isVisible = false
            placeholderText.isVisible = false
            placeholderResetButton.isVisible = false
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
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                if (editText.text.isNotEmpty()) {
                    iTunesService.search(editText.text.toString())
                        .enqueue(object : Callback<TrackResponse> {
                            @SuppressLint("NotifyDataSetChanged")
                            override fun onResponse(
                                call: Call<TrackResponse>,
                                response: Response<TrackResponse>
                            ) {
                                if (response.code() == Companion.RESPONSE_SUCCESSFULL) {
                                    tracks.clear()
                                    hidePlaceholder()
                                    if (response.body()?.results?.isNotEmpty() == true) {
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
                                placeholderText.isVisible = true
                                placeholderImageNoInternet.isVisible = true
                                placeholderImageNothingFound.isVisible = false
                                placeholderResetButton.isVisible = true
                                placeholderText.text = getString(R.string.internetProblem)
                            }
                        })
                }
            }
            false
        }

        placeholderResetButton.setOnClickListener {
            if (editText.text.isNotEmpty()) {
                iTunesService.search(editText.text.toString())
                    .enqueue(object : Callback<TrackResponse> {

                        @SuppressLint("NotifyDataSetChanged")
                        override fun onResponse(
                            call: Call<TrackResponse>,
                            response: Response<TrackResponse>
                        ) {
                            if (response.code() == Companion.RESPONSE_SUCCESSFULL) {
                                tracks.clear()
                                hidePlaceholder()
                                if (response.body()?.results?.isNotEmpty() == true) {
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
                            placeholderText.isVisible = true
                            placeholderImageNoInternet.isVisible = true
                            placeholderImageNothingFound.isVisible = false
                            placeholderResetButton.isVisible = true
                            placeholderText.text = getString(R.string.internetProblem)
                        }
                    })
            }
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
            searchHistory.getTracksList()
            val adapterHistory = AdapterHistoryTracks(searchHistory.historyList)
            recyclerSearchHistory.adapter = adapterHistory
            adapterHistory.notifyDataSetChanged()
            adapter.notifyDataSetChanged()
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.isNullOrEmpty()) {
                    buttonSearchDelete.isVisible = false
                    closeKeyboard(editText)
                } else {
                    buttonSearchDelete.isVisible = true
                    enterEditText = editText.text.toString()
                }

                if (editText.hasFocusable() && (p0?.isEmpty() == true)) {
                    youSearchedText.isVisible = true
                    deleteSearchHistory.isVisible = true
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

}
