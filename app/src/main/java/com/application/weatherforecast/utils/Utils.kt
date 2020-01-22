package com.application.weatherforecast.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.application.weatherforecast.R
import java.text.SimpleDateFormat
import java.util.*

val APP_KEY = "c72492406e49c6b2aa990f5afd633791"
val BASE_URL = "https://api.openweathermap.org"
val IMG_URL = "http://openweathermap.org/img/wn/"
val IMG_EXT = "@2x.png"
val METRIC = "metric"

fun convertLongToDate(time: Long): String {
    val calendar = Calendar.getInstance(Locale.US)
    calendar.timeInMillis = (time * 1000)
    val format = SimpleDateFormat("EEEE MMM d, yyyy", Locale.US)
    return format.format(calendar.time)
}

fun convertLongToTime(time: Long): String {
    val calendar = Calendar.getInstance(Locale.US)
    calendar.timeInMillis = (time * 1000)
    val format = SimpleDateFormat("hh a", Locale.US)
    return format.format(calendar.time)
}

fun getProgressDrawable(context: Context): CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 10f
        centerRadius = 50f
        start()
    }
}

fun ImageView.loadImage(iconName: String?, progressDrawable: CircularProgressDrawable) {
    val imageURL = IMG_URL + iconName + IMG_EXT
    val options = RequestOptions()
        .placeholder(progressDrawable)
        .error(R.drawable.ic_cloud_off)
    Glide.with(this.context)
        .setDefaultRequestOptions(options)
        .load(imageURL)
        .into(this)
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}