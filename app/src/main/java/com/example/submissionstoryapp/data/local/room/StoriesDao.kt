package com.example.submissionstoryapp.data.local.room

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.submissionstoryapp.data.local.entity.StoriesEntity

@Dao
interface StoriesDao {
    @Query("SELECT * FROM stories")
    fun getStories(): LiveData<List<StoriesEntity>>

    @Query("SELECT * FROM stories")
    fun getPagedStories(): PagingSource<Int, StoriesEntity>

    @Query("SELECT * FROM stories")
    fun getStoriesWidget(): List<StoriesEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertStory(listStory: List<StoriesEntity>)

    @Query("DELETE FROM stories")
    fun deleteAll()
}