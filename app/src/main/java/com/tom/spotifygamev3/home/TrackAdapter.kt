package com.tom.spotifygamev3.home


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tom.spotifygamev3.databinding.GridTrackItemBinding
import com.tom.spotifygamev3.models.Items
import com.tom.spotifygamev3.models.SpotifyPlaylistResponse

class TrackAdapter() : ListAdapter<Items, TrackAdapter.PlaylistViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        return PlaylistViewHolder(GridTrackItemBinding.inflate(
            LayoutInflater.from(parent.context)
        ))
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val track = getItem(position)
        holder.bind(track)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Items>() {
        override fun areItemsTheSame(oldItem: Items, newItem: Items): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Items, newItem: Items): Boolean {
            return oldItem.track.id == newItem.track.id
        }
    }

    class PlaylistViewHolder(private var binding: GridTrackItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Items){
            binding.playlist = item
            binding.executePendingBindings()
//            binding.albumImage
        }

    }


}