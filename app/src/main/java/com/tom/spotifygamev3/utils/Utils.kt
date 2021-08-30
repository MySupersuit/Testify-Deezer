package com.tom.spotifygamev3.utils

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.media.MediaPlayer
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.AlbumGameFragmentBinding
import com.tom.spotifygamev3.models.spotify_models.Images
import java.text.Normalizer
import java.util.*

object Utils {
    val TAG = "Utils"

    fun <T> List<T>.safeSubList(fromIndex: Int, toIndex: Int): List<T> =
        this.subList(fromIndex, toIndex.coerceAtMost(this.size))

    fun regexedString(string: String, regex: Regex): String {
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

    // TODO MESSY REDO
    // Gradient drawables
    // https://stackoverflow.com/questions/6115715/how-do-i-programmatically-set-the-background-color-gradient-on-a-custom-title-ba
    // pass in colour to transiation from
    fun glideShowImagePalette(
        images: List<Images>,
        context: Context,
        imageView: ImageView,
        binding: AlbumGameFragmentBinding
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            .error(Glide.with(context).load(imgUri2))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Glide.with(context).load(imgUri2)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d(TAG, "load double failed")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d(TAG, "resource ready1")
                                resource?.let {
                                    val bitmap = it.toBitmap()
                                    val builder = Palette.Builder(bitmap)
                                    val background = listOf(
                                        binding.albumGameCl,
                                        binding.albumButtonCl,
                                        binding.albumScoreCounter,
                                        binding.albumCheckmark,
                                        binding.albumCross
                                    )
                                    val palette = builder.generate() { palette ->
                                        val bground =
                                            binding.albumGameCl.background as ColorDrawable
                                        val fromColor =
                                            bground.color // TODO need to keep track of previous color
                                        val toColor = palette?.dominantSwatch?.rgb
                                        Log.d(TAG, "solid color $fromColor")
                                        val fadeAnimator = ValueAnimator.ofObject(
                                            ArgbEvaluator(),
                                            fromColor,
                                            toColor
                                        )
                                        fadeAnimator.setDuration(250)
                                        fadeAnimator.addUpdateListener { anim ->
                                            binding.albumGameCl.setBackgroundColor(anim.animatedValue as Int)
                                        }
                                        fadeAnimator.start()

                                    }
                                    
                                }
                                return false
                            }
                        })
                        .into(imageView)

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "resource ready2")
                    resource?.let {
                        val bitmap = it.toBitmap()
                        val builder = Palette.Builder(bitmap)
                        val background = listOf(
                            binding.albumGameCl,
                            binding.albumButtonCl,
                            binding.albumScoreCounter,
                            binding.albumCheckmark,
                            binding.albumCross
                        )
                        val palette = builder.generate() { palette ->
                            val fromColor = binding.albumGameCl.solidColor
                            val toColor = palette?.dominantSwatch?.rgb
                            Log.d(TAG, "solid color $fromColor")
                            val fadeAnimator =
                                ValueAnimator.ofObject(ArgbEvaluator(), fromColor
                                    , toColor ?: ContextCompat.getColor(context, R.color.spotify_black))
                            fadeAnimator.setDuration(250)
                            fadeAnimator.addUpdateListener { anim ->
                                background.forEach {
                                    it.setBackgroundColor(anim.animatedValue as Int)
                                }
//                                binding.albumGameCl.setBackgroundColor(anim.animatedValue as Int)
                            }
                            fadeAnimator.start()

                        }
                    }
                    return false
                }

            })
            .apply(
                RequestOptions()
//                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .into(imageView)
    }

    fun glideShowImage(
        images: List<Images>,
        context: Context,
        imageView: ImageView,
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)
//
        Glide.with(context)
            .load(imgUri)
            .error(Glide.with(context).load(imgUri2))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Glide.with(context).load(imgUri2)
                        .listener(object : RequestListener<Drawable> {
                            override fun onLoadFailed(
                                e: GlideException?,
                                model: Any?,
                                target: Target<Drawable>?,
                                isFirstResource: Boolean
                            ): Boolean {
                                Log.d(TAG, "load double failed")
                                return false
                            }

                            override fun onResourceReady(
                                resource: Drawable?,
                                model: Any?,
                                target: Target<Drawable>?,
                                dataSource: DataSource?,
                                isFirstResource: Boolean
                            ): Boolean {
                                return false
                            }
                        })
                        .into(imageView)

                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
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
//            .error(Glide.with(context).load(imgUri2))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Glide.with(context).load(imgUri2)
                        .error(Log.d(TAG, "preload fail twice"))
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .preload()
                    Log.d(TAG, "preload fail once")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }
            })
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .preload()
        Log.d(TAG, "Preloaded")

    }

    fun urlToUri(url: String): Uri {
        return url.toUri().buildUpon().scheme("https").build()
    }

    fun CharSequence.unaccent(): String {
        val temp = Normalizer.normalize(this, Normalizer.Form.NFD)
        return Constants.UNACCENT_REGEX.replace(temp, "")
    }

    fun doAlphaAnimation(imgView: ImageView) {
        val anim = AlphaAnimation(1f, 0f)
        anim.duration = 1500
        anim.fillAfter = true
        imgView.startAnimation(anim)
        imgView.visibility = View.VISIBLE
    }

}