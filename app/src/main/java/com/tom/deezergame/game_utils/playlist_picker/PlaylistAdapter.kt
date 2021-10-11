package com.tom.deezergame.game_utils.playlist_picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tom.deezergame.R
import com.tom.deezergame.databinding.PlaylistItemBinding
import com.tom.deezergame.models.deezer_models.UserPlaylistData
import com.tom.deezergame.models.spotify_models.SimplePlaylist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlaylistAdapter(val clickListener: PlaylistListener) :
    ListAdapter<UserPlaylistData, RecyclerView.ViewHolder>(PlaylistDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val playlistItem = getItem(position) as UserPlaylistData
                holder.bind(playlistItem, clickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    fun submitPlaylist(list: List<UserPlaylistData>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(list.sortedByDescending { it.fans })
            }
        }
    }

    class ViewHolder private constructor(val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: UserPlaylistData, clickListener: PlaylistListener) {
            binding.playlist = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
            if (item.fans == 0) {
                binding.playlistFans.text = this.itemView.context.getString(
                    R.string.nb_tracks, item.nb_tracks
                )
            } else {
                binding.playlistFans.text = this.itemView.context.getString(R.string.fans, String.format("%,d", item.fans))

            }
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = PlaylistItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class PlaylistDiffCallback : DiffUtil.ItemCallback<UserPlaylistData>() {
    override fun areItemsTheSame(oldItem: UserPlaylistData, newItem: UserPlaylistData): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: UserPlaylistData, newItem: UserPlaylistData): Boolean {
        return oldItem == newItem
    }
}

class PlaylistListener(val clickListener: (playlistId: Long) -> Unit) {
    fun onClick(playlist: UserPlaylistData) = clickListener(playlist.id)
}

private val adapterScope = CoroutineScope(Dispatchers.Default)