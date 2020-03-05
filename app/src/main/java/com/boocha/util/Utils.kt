package com.boocha.util

import android.annotation.SuppressLint
import android.util.Log
import java.lang.NumberFormatException
import java.text.SimpleDateFormat
import java.util.*


@SuppressLint("SimpleDateFormat")
fun getCurrentTime(): String {
    val simpleDateFormat = SimpleDateFormat("dd/M/yyyy hh:mm:ss")
    val currentDateString = simpleDateFormat.format(Date())
    return simpleDateFormat.parse(currentDateString).time.toString()
}

fun dateDifference(date: String): String {
    try {
        val diff = Date(getCurrentTime().toLong()).time - Date(date.toLong()).time

        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24

        return when {
            days > 0 -> "$days days ago"
            hours > 0 -> "$hours hours ago"
            minutes > 0 -> "$minutes minutes ago"
            else -> "$seconds seconds ago"
        }
    }catch (exception: NumberFormatException){
        Log.e("TIME","${exception.message}")
        return ""
    }
}