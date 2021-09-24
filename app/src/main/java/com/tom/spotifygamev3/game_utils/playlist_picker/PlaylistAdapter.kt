package com.tom.spotifygamev3.game_utils.playlist_picker

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tom.spotifygamev3.databinding.PlaylistItemBinding
import com.tom.spotifygamev3.models.spotify_models.Playlist
import com.tom.spotifygamev3.models.spotify_models.SimplePlaylist
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlaylistAdapter(val clickListener: PlaylistListener) :
    ListAdapter<SimplePlaylist, RecyclerView.ViewHolder>(PlaylistDiffCallback()) {

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ViewHolder -> {
                val playlistItem = getItem(position) as SimplePlaylist
                holder.bind(playlistItem, clickListener)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    fun submitPlaylist(list : List<SimplePlaylist>) {
        adapterScope.launch {
            withContext(Dispatchers.Main) {
                submitList(list)
            }
        }
    }


    class ViewHolder private constructor(val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SimplePlaylist, clickListener: PlaylistListener) {
            binding.playlist = item
            binding.executePendingBindings()
            binding.clickListener = clickListener
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

class PlaylistDiffCallback : DiffUtil.ItemCallback<SimplePlaylist>() {
    override fun areItemsTheSame(oldItem: SimplePlaylist, newItem: SimplePlaylist): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: SimplePlaylist, newItem: SimplePlaylist): Boolean {
        return oldItem == newItem
    }
}

class PlaylistListener(val clickListener: (playlistId: String) -> Unit) {
    fun onClick(playlist: SimplePlaylist) = clickListener(playlist.id)
}

private val adapterScope = CoroutineScope(Dispatchers.Default)