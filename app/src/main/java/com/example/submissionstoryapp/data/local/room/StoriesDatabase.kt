package com.example.submissionstoryapp.data.local.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.paging.RemoteKeys
import com.example.submissionstoryapp.data.paging.RemoteKeysDao

@Database(
    entities = [StoriesEntity::class, RemoteKeys::class],
    version = 2,
    exportSchema = true)
abstract class StoriesDatabase : RoomDatabase() {
    abstract fun storiesDao(): StoriesDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var instance: StoriesDatabase? = null
        fun getInstance(context: Context): StoriesDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    StoriesDatabase::class.java, "Stories.db"
                )
                .fallbackToDestructiveMigration()
                .build()
                .also { instance = it }
            }
    }
}