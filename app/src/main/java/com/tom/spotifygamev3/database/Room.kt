package com.tom.spotifygamev3.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface PlaylistDao {
    @Query("select * from databaseuserplaylist")
    fun getUserPlaylists(): LiveData<List<DatabaseUserPlaylist>>

    @Query("select * from databasecommonplaylist")
    fun getCommonPlaylists(): LiveData<List<DatabaseCommonPlaylist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllUserPlaylists(playlists: List<DatabaseUserPlaylist>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCommonPlaylists(playlist: DatabaseCommonPlaylist)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAllCommonPlaylists(playlists: List<DatabaseCommonPlaylist>)

    @Query("DELETE from databasecommonplaylist")
    fun deleteCommonTable()

    @Query("DELETE from databaseuserplaylist")
    fun deleteUserTable()
}

@Database(entities = [DatabaseUserPlaylist::class,
                     DatabaseCommonPlaylist::class], version = 5)
@TypeConverters(DataConverter::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract val playlistDao: PlaylistDao
}

private lateinit var INSTANCE: PlaylistDatabase

fun getDatabase(context: Context) : PlaylistDatabase {
    synchronized(PlaylistDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(context.applicationContext,
            PlaylistDatabase::class.java,
            "playlists")
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}