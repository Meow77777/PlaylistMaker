package com.practicum.playlistmaker.sharing.model

import android.net.Uri

data class DataEmail(
    val email : String,
    val message : String,
    val subject : String,
    val adress : Uri
)
