package com.practicum.playlistmaker.utils

import android.content.Context
import android.util.TypedValue
import java.text.SimpleDateFormat
import java.util.Locale

object DateTimeUtil {
    fun timeConvert(time: Long): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(time)
    }

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

}