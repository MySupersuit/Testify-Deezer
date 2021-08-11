package com.tom.spotifygamev3.album_game.playlist_picker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.tom.spotifygamev3.R
import com.tom.spotifygamev3.databinding.PlaylistItemBinding
import com.tom.spotifygamev3.models.spotify_models.Playlist
import com.tom.spotifygamev3.Utils.Utils.glideShowImageLoadAnim

class PlaylistRecyclerAdapter() :
    RecyclerView.Adapter<PlaylistRecyclerAdapter.PlaylistViewHolder>() {

    var playlists = mutableListOf<Playlist>()

    fun setListPlaylists(playlists: List<Playlist>) {
        this.playlists = playlists.toMutableList()
        notifyDataSetChanged()
    }

    class PlaylistViewHolder(val binding: PlaylistItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        val binding = PlaylistItemBinding.inflate(inflater, parent, false)

        return PlaylistViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        val playlist = playlists[position]
        holder.binding.playlistName.text = playlist.name
        holder.binding.playlistOwner.text = playlist.owner.display_name
        glideShowImageLoadAnim(playlist.images, holder.itemView.context, holder.binding.playlistImage)
    }

    override fun getItemCount() = playlists.size
}

//class PlaylistListener(val clickListener : (playlistId: String) -> Unit) {
//    fun onClick(playlist: Playlist) = clickListener(playlist.id)
//}