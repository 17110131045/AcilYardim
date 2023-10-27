package com.nexis.acilyardim.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar

fun String.show(v: View, message: String){
    Snackbar.make(v, message, Snackbar.LENGTH_LONG).show()
}

fun ImageView.downloadImageUrl(imageUrl: String?){
    val options = RequestOptions()
        .placeholder(placeHolderProgress(context))
    //.error(R.mipmap.ic_launcher_round)

    Glide.with(context)
        .setDefaultRequestOptions(options)
        .load(imageUrl)
        .into(this)
}

fun placeHolderProgress(context: Context) : CircularProgressDrawable {
    return CircularProgressDrawable(context).apply {
        strokeWidth = 8f
        centerRadius = 40f
        backgroundColor = Color.parseColor("#DFDFDF")
        start()
    }
}

@BindingAdapter("android:downloadImage")
fun downloadImage(view: ImageView, url: String?){
    view.downloadImageUrl(url)
}

@BindingAdapter("android:setOnlineStatus")
fun setOnlineStatus(view: TextView, isOnline: Boolean){
    if (isOnline){
        view.text = "Çevrimiçi"
        view.setTextColor(Color.parseColor("#6DCF91"))
    } else {
        view.text = "Çevrimdışı"
        view.setTextColor(Color.parseColor("#CF6D6D"))
    }
}