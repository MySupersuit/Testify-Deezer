package com.tom.deezergame

import android.view.View
import android.widget.*
import androidx.appcompat.widget.AppCompatButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.tom.deezergame.album_game.DeezerApiStatus
import com.tom.deezergame.utils.Utils
import com.tom.deezergame.album_game.SpotifyApiStatus
import com.tom.deezergame.models.spotify_models.Images
import timber.log.Timber

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
fun bindImageLoadingAnim(imgView: ImageView, images: List<String>) {
    Utils.glideShowImageLoadAnim(images, imgView.context, imgView)

}

@BindingAdapter("spotifyApiStatus")
fun bindStatus(statusImageView: ImageView, status: SpotifyApiStatus) {
    Timber.d(status.name)
    when (status) {
        SpotifyApiStatus.LOADING -> {
            statusImageView.visibility = View.VISIBLE
            statusImageView.setImageResource(R.drawable.ic_connection_error)
        }
        SpotifyApiStatus.DONE -> {
            statusImageView.visibility = View.GONE
        }
        else -> Timber.e( "image view elsied")
    }
}

@BindingAdapter("BtIApiStatus")
fun bindBtIStatus(statusProgress: ConstraintLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.LOADING -> {
            statusProgress.visibility = View.VISIBLE
        }
        SpotifyApiStatus.DONE -> statusProgress.visibility = View.GONE
        SpotifyApiStatus.ERROR -> {
            statusProgress.visibility = View.VISIBLE
            statusProgress.findViewById<ProgressBar>(R.id.loading_progress_bar).visibility = View.INVISIBLE
            statusProgress.findViewById<TextView>(R.id.beat_intro_loading_message).text = "Error Loading"
        }
    }
}

@BindingAdapter("spotifyApiProgress")
fun bindProgress(statusProgress: ConstraintLayout, status: SpotifyApiStatus) {
    Timber.d(status.name)
    when (status) {
        SpotifyApiStatus.LOADING -> {
            statusProgress.visibility = View.VISIBLE
        }
        SpotifyApiStatus.DONE -> {
            statusProgress.visibility = View.GONE
        }
        SpotifyApiStatus.ERROR -> {
            statusProgress.visibility = View.VISIBLE
            statusProgress.findViewById<ProgressBar>(R.id.progress_bar).visibility = View.INVISIBLE
            statusProgress.findViewById<TextView>(R.id.loading_message).text = "Error Loading"
            statusProgress.findViewById<AppCompatButton>(R.id.login_button).visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("showAlbumQuiz")
fun showAlbumQuiz(constraintLayout: ConstraintLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.DONE -> {
            constraintLayout.visibility = View.VISIBLE
        }
        else -> {
            constraintLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("showHighLowQuiz")
fun showHighLowQuiz(frameLayout: FrameLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.DONE -> {
            frameLayout.visibility = View.VISIBLE
        }
        else -> {
            frameLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("showFrameLayout")
fun showFrameLayout(frameLayout: FrameLayout, status: SpotifyApiStatus) {
    when (status) {
        SpotifyApiStatus.DONE -> {
            frameLayout.visibility = View.VISIBLE
        }
        else -> {
            frameLayout.visibility = View.GONE
        }
    }
}

@BindingAdapter("showPlaylistPicker")
fun showPlaylistPicker(constraintLayout: ConstraintLayout, status: SpotifyApiStatus) {
    Timber.d("showPlaylistPicker pls $status")
    when (status) {
        SpotifyApiStatus.LOADING -> {
            constraintLayout.visibility = View.GONE
        }
        SpotifyApiStatus.DONE -> {
            constraintLayout.visibility = View.VISIBLE
        }
    }
}

@BindingAdapter("deezerSearchProgress")
fun bindProgress(cl :ConstraintLayout, status: DeezerApiStatus) {
    when (status) {
        DeezerApiStatus.LOADING -> {
            cl.visibility = View.VISIBLE
        }
        DeezerApiStatus.DONE -> {
            cl.visibility = View.GONE
        }
        DeezerApiStatus.ERROR -> {
            cl.visibility = View.VISIBLE
            cl.findViewById<ProgressBar>(R.id.search_progress_bar).visibility = View.INVISIBLE
            cl.findViewById<TextView>(R.id.search_loading_message).text = "Error Loading"
        }
    }
}

