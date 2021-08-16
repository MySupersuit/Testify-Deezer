package com.tom.spotifygamev3

import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tom.spotifygamev3.Utils.Utils
import com.tom.spotifygamev3.album_game.game.SpotifyApiStatus
import com.tom.spotifygamev3.models.spotify_models.Images

const val TAG = "BindingAdapter"

@BindingAdapter("imageUrl")
fun bindImage(imgView: ImageView, imgUrl: String?) {
    imgUrl?.let {
        val imgUri = imgUrl.toUri().buildUpon().scheme("https").build()
        Glide.with(imgView.context)
            .load(imgUri)
            .apply(
                RequestOptions()
                    .placeholder(R.drawable.loading_animation)
                    .error(R.drawable.broken_image)
            )
            .into(imgView)
    }
}

@BindingAdapter("showImageLoadingAnim")
fun bindImageLoadingAnim(imgView: ImageView, images: List<Images>) {
    Utils.glideShowImageLoadAnim(images, imgView.context, imgView)

}

@BindingAdapter("spotifyApiStatus")
fun bindStatus(statusImageView: ImageView, status: SpotifyApiStatus) {
    Log.d(TAG, status.name)
    when (status) {
        SpotifyApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        SpotifyApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> Log.e(TAG, "image view elsied")
    }
}

@BindingAdapter("spotifyApiProgress")
fun bindProgress(statusProgress: ConstraintLayout, status: SpotifyApiStatus) {
    Log.d(TAG, status.name)
    when (status) {
        SpotifyApiStatus.LOADING -> {
            statusProgress.visibility = View.VISIBLE
        }
        SpotifyApiStatus.DONE -> {
            statusProgress.visibility = View.GONE
        }
        else -> Log.e(TAG, "progress")
    }
}

@BindingAdapter("showAlbumQuiz")
fun showAlbumQuiz(constraintLayout: ConstraintLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.LOADING -> {
            constraintLayout.visibility = View.GONE
        }
        SpotifyApiStatus.DONE -> {
            constraintLayout.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("showHighLowQuiz")
fun showHighLowQuiz(linearLayout: LinearLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.LOADING -> {
            linearLayout.visibility = View.GONE
        }
        SpotifyApiStatus.DONE -> {
            linearLayout.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("showPlaylistPicker")
fun showPlaylistPicker(constraintLayout: ConstraintLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.LOADING -> {
            constraintLayout.visibility = View.GONE
        }
        SpotifyApiStatus.DONE -> {
            constraintLayout.visibility = View.VISIBLE
        }
    }
}