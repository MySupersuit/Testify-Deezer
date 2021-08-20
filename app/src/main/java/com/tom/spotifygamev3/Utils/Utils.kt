package com.tom.spotifygamev3.Utils

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.ImageView
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.models.spotify_models.Images
import java.text.Normalizer
import java.text.NumberFormat
import java.util.*

object Utils {
    val TAG = "Utils"

    fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
        this.subList(fromIndex, toIndex.coerceAtMost(this.size))

    fun regexedString(string: String, regex: Regex) : String {
        return regex.replace(string, "")
    }

    fun regexedString(string: String, regexes: List<Regex>): String {
        var ret = string
        for (reg in regexes) {
            ret = reg.replace(ret, "")
        }
        return ret
    }

    fun cleanedString(string: String): String {
        val original_tokens = string.split(" ").toMutableList()
        val regexed = regexedString(string, Constants.ALPHANUM_REGEX)
        val toRemove = mutableListOf<Int>()
        val splits = regexed.toLowerCase(Locale.ROOT).split(" ")
        val index = splits.indexOf("edition")
        if (index > 0) {
            toRemove.add(index)
            toRemove.add(index - 1)
        }
        toRemove.forEach { original_tokens.removeAt(it) }
        return original_tokens.joinToString(" ")
    }

    fun glideShowImage(images: List<Images>, context: Context, imageView: ImageView) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

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

    fun glideShowImageLoadAnim(images: List<Images>, context: Context, imageView: ImageView) {
        val imgUri = urlToUri(images[0].url)

        val glide = Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )

        if (images.size > 1) {
            val imgUri2 = urlToUri(images[1].url)
            glide.error(Glide.with(context).load(imgUri2))
        }
        glide.into(imageView)
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

    private fun urlToUri(url: String): Uri {
        return url.toUri().buildUpon().scheme("https").build()
    }

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Constants.UNACCENT_REGEX.replace(temp, "")
    }
}