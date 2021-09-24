package com.tom.spotifygamev3.utils

import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.blue
import androidx.core.graphics.drawable.DrawableCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.net.toUri
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.card.MaterialCardView
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.AlbumGameFragmentBinding
import com.tom.spotifygamev3.databinding.HighLowGameFragment3Binding
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

    //    fun hlShowImage1(images: List<Images>, context: Context, imgView: ImageView, binding: HighLowGameFragment3Binding) {
    fun hlShowImage1(images: List<Images>, context: Context, binding: HighLowGameFragment3Binding) {
        glideShowImagePaletteHL(
            images,
            context,
            binding.imageAns1,
            binding.clAns1,
            binding.artistAns1,
            binding.songAns1,
            binding.divAns1,
            binding.bground1,
            intArrayOf(-1, ContextCompat.getColor(context, R.color.spotify_black))

        )
    }

    fun hlShowImage2(images: List<Images>, context: Context, binding: HighLowGameFragment3Binding) {
        glideShowImagePaletteHL(
            images,
            context,
            binding.imageAns2,
            binding.clAns2,
            binding.artistAns2,
            binding.songAns2,
            binding.divAns2,
            binding.bground2,
            intArrayOf(ContextCompat.getColor(context, R.color.spotify_black), -1)

        )
    }

    private fun glideShowImagePaletteHL(
        images: List<Images>,
        context: Context,
        imgView: ImageView,
        cl: ConstraintLayout,
        artistTv: TextView,
        songTv: TextView,
        div: View,
        bground: View,
        gdColors: IntArray
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.e(TAG, "single fail HL", e)

                    if (e != null) {
                        for (t in e.rootCauses) {
                            Log.e(TAG, "Caused by", t)
                        }
                    }
                    Log.d(TAG, "posting runnable to main")
                    imgView.post {
                        Runnable {
                            reloadHL(
                                context,
                                imgUri2,
                                imgView,
                                cl,
                                artistTv,
                                songTv,
                                div,
                                bground,
                                gdColors
                            )
                        }
                    }
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        hlAnim(context, resource, cl, artistTv, songTv, div, bground, gdColors)
                    }
                    return false
                }
            })
            .into(imgView)
    }

    private fun hlAnim(
        context: Context,
        resource: Drawable,
        cl: ConstraintLayout,
        artistTv: TextView,
        songTv: TextView,
        div: View,
        bground: View,
        gdColors: IntArray
    ) {
        val bitmap = resource.toBitmap()
        val builder = Palette.Builder(bitmap)
        val black = ContextCompat.getColor(context, R.color.spotify_black)
        val white = ContextCompat.getColor(context, R.color.spotify_white)
        val grey = ContextCompat.getColor(context, R.color.spotify_grey)
        val index = if (gdColors[0] == -1) 0 else 1
        builder.generate { palette ->
            val fromDomColor = (cl.background as ColorDrawable).color
            val fromMutColor = (bground.background as GradientDrawable).colors?.get(index)
            val toDomColor = palette?.dominantSwatch?.rgb ?: black
            val toMutColor = palette?.mutedSwatch?.rgb ?: black
//            val toDarkVibColor = palette?.mute?.rgb ?: black
            val animator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromDomColor,
                toDomColor
            )
            val bgAnimator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromMutColor,
                toMutColor
            )
            animator.duration = 250
            bgAnimator.duration = 250
            animator.addUpdateListener { anim ->
                cl.setBackgroundColor(anim.animatedValue as Int)
            }
            bgAnimator.addUpdateListener { anim ->
                gdColors[index] = anim.animatedValue as Int
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    gdColors
                )
                bground.background = gd
            }
            animator.start()
            bgAnimator.start()
            artistTv.setTextColor(
                palette?.dominantSwatch?.bodyTextColor ?: grey
            )
            songTv.setTextColor(
                palette?.dominantSwatch?.bodyTextColor ?: white
            )
            div.setBackgroundColor(
                palette?.dominantSwatch?.bodyTextColor ?: white
            )
        }
    }

    private fun reloadHL(
        context: Context,
        uri: Uri,
        imgView: ImageView,
        cl: ConstraintLayout,
        artistTv: TextView,
        songTv: TextView,
        div: View,
        bground: View,
        gdColors: IntArray
    ) {
        Log.d(TAG, "reload HL")
        Glide.with(context).load(uri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.music_note_icon)
                    .error(R.drawable.ic_connection_error)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "second fail HL")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        hlAnim(context, resource, cl, artistTv, songTv, div, bground, gdColors)
                    }
                    return false
                }
            })
            .into(imgView)

    }

    fun glideShowImagePaletteBtI(
        images: List<Images>,
        context: Context,
        imgView: ImageView,
        cl: ConstraintLayout,
        songTv: TextView,
        artistTv: TextView,
        titleTv: TextView,
        scoreTv: TextView,
        nextBtn: AppCompatButton
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)

        Glide.with(context).load(imgUri)
            .error(
                reloadBtI(
                    imgUri2,
                    context,
                    imgView,
                    cl,
                    songTv,
                    artistTv,
                    titleTv,
                    scoreTv,
                    nextBtn
                )
            )
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.loading_animation)
            )
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "BtI load fail 1")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        setColorsBtI(
                            resource, context, cl,
                            songTv, artistTv, titleTv, scoreTv, nextBtn
                        )
                    }
                    return false
                }
            })
            .into(imgView)
    }

    private fun setColorsBtI(
        resource: Drawable,
        context: Context,
        cl: ConstraintLayout,
        songTv: TextView,
        artistTv: TextView,
        titleTv: TextView,
        scoreTv: TextView,
        nextBtn: AppCompatButton
    ) {
        val bitmap = resource.toBitmap()
        val builder = Palette.Builder(bitmap)
        val black = ContextCompat.getColor(context, R.color.spotify_black)
        val white = ContextCompat.getColor(context, R.color.spotify_white)
        val green = ContextCompat.getColor(context, R.color.spotify_green)
        builder.generate { palette ->
            val fromDomColor = (cl.background as ColorDrawable).color
            val domColor = palette?.dominantSwatch?.rgb ?: green
//            val lightVibColor = palette?.lightVibrantSwatch?.rgb
            val darkVib = palette?.mutedSwatch?.rgb
            val domTextColor = palette?.dominantSwatch?.bodyTextColor ?: black
            Log.d(TAG, "to dom color ${domColor.red} ${domColor.green} ${domColor.blue}")
            val animator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromDomColor,
                domColor
            )
            val vibAnimator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                black,
                darkVib ?: black
            )
            animator.duration = 600
            vibAnimator.duration = 600
            animator.addUpdateListener { anim ->
                cl.setBackgroundColor(anim.animatedValue as Int)
            }
            vibAnimator.addUpdateListener { anim ->
                nextBtn.backgroundTintList = ColorStateList.valueOf(anim.animatedValue as Int)
            }
            animator.start()
            vibAnimator.start()

            songTv.setTextColor(domTextColor)
            artistTv.setTextColor(domTextColor)
            titleTv.setTextColor(domTextColor)
            scoreTv.setTextColor(domTextColor)
//            nextBtn.setTextColor(palette?.darkVibrantSwatch?.bodyTextColor ?: white)
//            nextBtn.setTextColor(palette?.lightVibrantSwatch?.bodyTextColor ?: white)
//            nextBtn.backgroundTintList = ColorStateList.valueOf(palette?.vibrantSwatch?.rgb ?: black)
        }
    }

    private fun reloadBtI(
        uri: Uri,
        context: Context,
        imgView: ImageView,
        cl: ConstraintLayout,
        songTv: TextView,
        artistTv: TextView,
        titleTv: TextView,
        scoreTv: TextView,
        nextBtn: AppCompatButton
    ) {
        Log.d(TAG, "reloading bti")
        Glide.with(context).load(uri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.music_note_icon)
                    .error(R.drawable.ic_connection_error)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "BtI load fail 2")
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        setColorsBtI(
                            resource, context, cl,
                            songTv, artistTv, titleTv, scoreTv, nextBtn
                        )
                    }
                    return false
                }
            })
            .into(imgView)
    }

    fun glideShowImagePaletteV2(
        images: List<Images>,
        context: Context,
        imgView: ImageView,
        binding: AlbumGameFragmentBinding
    ) {
        val imgUri = urlToUri(images[0].url)
        val imgUri2 = urlToUri(images[1].url)
        for (i in images) {
            Log.d(TAG, "image height: ${i.height} width: ${i.width}")
        }

        Glide.with(context)
            .load(imgUri)
            // on error try reload with 2nd uri
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    imgView.post {
                        reloadAC(
                            context,
                            imgUri2,
                            imgView,
                            binding
                        )
                    }
                    Log.d(TAG, "single fail AC")
                    return false
                }

                // on ready change background colour
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    // On image load - animates background change colour
                    if (resource != null) {
                        bgColorAnimation(context, resource, binding)
                    }
                    return false
                }
            })
            .into(imgView)
    }

    // factor out binding potentially
    private fun bgColorAnimation(
        context: Context, resource: Drawable, binding: AlbumGameFragmentBinding
    ) {
        val bitmap = resource.toBitmap()
        val builder = Palette.Builder(bitmap)
        val black = ContextCompat.getColor(context, R.color.spotify_black)
        val white = ContextCompat.getColor(context, R.color.spotify_white)
        var toColor: Int
        builder.generate { palette ->
            // .colors ups the api level to 24 from 21 - some way to use customGradDrawable to get color[0]
            val fromColor = (binding.mainBackground.background as GradientDrawable).colors?.get(0)
            toColor = palette?.dominantSwatch?.rgb ?: black
            val animator = ValueAnimator.ofObject(
                ArgbEvaluator(),
                fromColor,
                toColor
            )
            animator.duration = 250
            animator.addUpdateListener { anim ->
                // make new gradient drawable
                val gd = GradientDrawable(
                    GradientDrawable.Orientation.TOP_BOTTOM,
                    intArrayOf(anim.animatedValue as Int, black)
                )
                binding.mainBackground.background = gd
            }
            animator.start()
            binding.albumScoreCounter.setTextColor(
                palette?.dominantSwatch?.bodyTextColor ?: white
            )
        }
    }

    private fun reloadAC(
        context: Context,
        uri: Uri,
        imageView: ImageView,
        binding: AlbumGameFragmentBinding
    ) {
        Log.d(TAG, "reload")
        Glide.with(context).load(uri)
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.music_note_icon)  // on reload show loading animation
                    .error(R.drawable.ic_connection_error)
            )
            .listener(object : RequestListener<Drawable> {

                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "second fail")
                    return false
                }

                // on ready change background colour
                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    if (resource != null) {
                        bgColorAnimation(context, resource, binding)
                    }
                    return false
                }
            })
            .into(imageView)
    }

    fun glideShowImage(
        images: List<Images>,
        context: Context,
        imageView: ImageView,
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
                    .placeholder(R.drawable.music_note_icon)
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
            .apply(
                RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    Log.d(TAG, "preload 1st success of $imgUri")
                    return false
                }
            })
//            .error(
//                Glide.with(context)
//                    .load(imgUri2)
//                    .apply(
//                        RequestOptions()
//                            .diskCacheStrategy(DiskCacheStrategy.ALL)
//                    )
//                    .listener(object: RequestListener<Drawable> {
//                        override fun onLoadFailed(
//                            e: GlideException?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            return false
//                        }
//
//                        override fun onResourceReady(
//                            resource: Drawable?,
//                            model: Any?,
//                            target: Target<Drawable>?,
//                            dataSource: DataSource?,
//                            isFirstResource: Boolean
//                        ): Boolean {
//                            Log.d(TAG, "Preload 2nd success of $imgUri2")
//                            return false
//                        }
//                    })
//                    .preload()
//            )
//            .listener(preloadListener(context, imgUri2))
            .preload()


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