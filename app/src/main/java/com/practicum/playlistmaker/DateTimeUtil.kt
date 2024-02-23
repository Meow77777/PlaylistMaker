package com.practicum.playlistmaker

import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {
    fun timeConvert(time: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

}