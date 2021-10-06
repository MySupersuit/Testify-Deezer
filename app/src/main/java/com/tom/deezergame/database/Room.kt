package com.tom.deezergame.database

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DzPlaylistDao {
    @Query("select * from dzdatabaseuserplaylist")
    fun getUserPlaylists(): LiveData<List<DzDatabaseUserPlaylist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertUserPlaylists(playlists: List<DzDatabaseUserPlaylist>)

    @Query("DELETE from dzdatabaseuserplaylist")
    fun deleteUsers()
}

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

@Database(
    entities = [DatabaseUserPlaylist::class,
        DatabaseCommonPlaylist::class], version = 5
)
@TypeConverters(DataConverter::class)
abstract class PlaylistDatabase : RoomDatabase() {
    abstract val playlistDao: PlaylistDao
}

@Database(entities = [DzDatabaseUserPlaylist::class], version = 3)
abstract class DzPlaylistDatabase: RoomDatabase() {
    abstract val playlistDao: DzPlaylistDao
}

private lateinit var dzINSTANCE: DzPlaylistDatabase

private lateinit var INSTANCE: PlaylistDatabase

fun getDzDatabase(context: Context): DzPlaylistDatabase {
    synchronized(DzPlaylistDatabase::class.java) {
        if (!::dzINSTANCE.isInitialized) {
            dzINSTANCE = Room.databaseBuilder(
                context.applicationContext,
                DzPlaylistDatabase::class.java,
                "dzPlaylists"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return dzINSTANCE
}

fun getDatabase(context: Context): PlaylistDatabase {
    synchronized(PlaylistDatabase::class.java) {
        if (!::INSTANCE.isInitialized) {
            INSTANCE = Room.databaseBuilder(
                context.applicationContext,
                PlaylistDatabase::class.java,
                "playlists"
            )
                .fallbackToDestructiveMigration()
                .build()
        }
    }
    return INSTANCE
}