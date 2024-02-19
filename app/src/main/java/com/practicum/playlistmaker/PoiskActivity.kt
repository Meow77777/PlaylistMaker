package com.practicum.playlistmaker

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView


class PoiskActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poisk)
        val recycler = findViewById<RecyclerView>(R.id.listView)
        val tracksList = mutableListOf(
            Track(
                "Smells Like Teen Spirit",
                "Nirvana",
                "5:01",
                "https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"
            ),
            Track(
                "Billie Jean",
                "Michael Jackson",
                "4:35",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg",
            ),
            Track(
                "Stayin' Alive",
                "Bee Gees",
                "4:10",
                "https://is4-ssl.mzstatic.com/image/thumb/Music115/v4/1f/80/1f/1f801fc1-8c0f-ea3e-d3e5-387c6619619e/16UMGIM86640.rgb.jpg/100x100bb.jpg",
            ),
            Track(
                "Whole Lotta Love",
                "Led Zeppelin",
                "5:33",
                "https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg",
            ),
            Track(
                "Sweet Child O'Mine",
                "Guns N' Roses",
                "5:03",
                "https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg",
            )
        )
        recycler.adapter = SongSearchAdapter(tracksList)


        val buttonSearchBack = findViewById<ImageView>(R.id.settingsBackFromSearch)
        val editText = findViewById<EditText>(R.id.editText)
        val buttonSearchDelete = findViewById<ImageView>(R.id.deleteSearchButton)

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


    companion object {
        private const val EDIT_TEXT = "TEXT"
        private const val EDIT_TEXT_ENTER = ""
    }

}