package com.tom.spotifygamev3.Utils

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.models.Images

object Utils {
    val TAG = "Utils"

    fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
        this.subList(fromIndex, toIndex.coerceAtMost(this.size))

    fun regexedString(string: String, regex: Regex): String {
        return regex.replace(string, "")
    }

    fun glideShowImage(images: List<Images>, context: Context, imageView: ImageView) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)
        Log.d(TAG, imgUri.toString())

        Glide.with(context)
            .load(imgUri)
//            .listener(requestListener)
            .error(Glide.with(context).load(imgUri2))
            .apply(
                RequestOptions()
//                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .into(imageView)
    }

    fun glidePreloadImage(images: List<Images>, context: Context) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            .error(Glide.with(context).load(imgUri2))
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .preload()
        Log.d(TAG, "Preloaded")

    }

    fun urlToUri(url: String) : Uri {
        return url.toUri().buildUpon().scheme("https").build()
    }
}