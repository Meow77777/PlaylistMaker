package com.practicum.playlistmaker

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.core.view.isVisible
import com.google.android.material.textfield.TextInputLayout

class PoiskActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_poisk)


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
                    closekeyboard(editText)
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

    fun closekeyboard(view: View) {
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
        const val EDIT_TEXT = "TEXT"
        const val EDIT_TEXT_ENTER = ""
    }
}