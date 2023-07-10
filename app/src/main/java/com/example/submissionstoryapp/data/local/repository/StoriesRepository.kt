package com.example.submissionstoryapp.data.local.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.paging.*
import com.example.submissionstoryapp.data.local.Result
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.local.room.StoriesDao
import com.example.submissionstoryapp.data.local.room.StoriesDatabase
import com.example.submissionstoryapp.data.paging.StoriesRemoteMediator
import com.example.submissionstoryapp.data.remote.entity.ListStoriesResponse
import com.example.submissionstoryapp.data.remote.retrofit.ApiService
import com.example.submissionstoryapp.data.utils.AppExecutors
import com.example.submissionstoryapp.utils.wrapEspressoIdlingResource
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StoriesRepository private constructor(
    private val apiService: ApiService,
    private val appExecutors: AppExecutors,
    private val storiesDatabase: StoriesDatabase
) {
    private val result = MediatorLiveData<Result<List<StoriesEntity>>>()

    fun getStories(token: String): LiveData<PagingData<StoriesEntity>> {
        wrapEspressoIdlingResource {
            @OptIn(ExperimentalPagingApi::class)
            return Pager(
                config = PagingConfig(
                    pageSize = 5, prefetchDistance = 0, initialLoadSize = 1
                ),
                remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService, token),
                pagingSourceFactory = {
                    storiesDatabase.storiesDao().getPagedStories()
                }
            ).liveData
        }
    }

    fun getStoriesWidget(token: String): List<StoriesEntity> {
        val client = apiService.getListStories("Bearer $token", 1)
        client.enqueue(object : Callback<ListStoriesResponse> {
            override fun onResponse(call: Call<ListStoriesResponse>, response: Response<ListStoriesResponse>) {
                if (response.isSuccessful) {
                    val listStory = response.body()?.listStory
                    val listStoryItem = ArrayList<StoriesEntity>()
                    appExecutors.diskIO.execute {
                        listStory?.forEach {
                            val story = StoriesEntity(
                                it.id,
                                it.photoUrl,
                                it.name,
                                it.lat as Double?,
                                it.lon as Double?
                            )
                            listStoryItem.add(story)
                        }
                        storiesDatabase.storiesDao().deleteAll()
                        storiesDatabase.storiesDao().insertStory(listStoryItem)
                    }
                }
            }

            override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        return storiesDatabase.storiesDao().getStoriesWidget()
    }

    fun deleteStories(){
        appExecutors.diskIO.execute {
            storiesDatabase.storiesDao().deleteAll()
        }
    }

    fun getStoriesLocation(token: String): LiveData<Result<List<StoriesEntity>>> {
        result.value = Result.Loading
        val client = apiService.getListStories("Bearer $token", 1)
        client.enqueue(object : Callback<ListStoriesResponse> {
            override fun onResponse(call: Call<ListStoriesResponse>, response: Response<ListStoriesResponse>) {
                if (response.isSuccessful) {
                    val listStory = response.body()?.listStory
                    val listStoryItem = ArrayList<StoriesEntity>()
                    appExecutors.diskIO.execute {
                        listStory?.forEach {
                            val story = StoriesEntity(
                                it.id,
                                it.photoUrl,
                                it.name,
                                it.lat as Double,
                                it.lon as Double
                            )
                            listStoryItem.add(story)
                        }
                        storiesDatabase.storiesDao().deleteAll()
                        storiesDatabase.storiesDao().insertStory(listStoryItem)
                    }
                }
            }

            override fun onFailure(call: Call<ListStoriesResponse>, t: Throwable) {
                result.value = Result.Error(t.message.toString())
            }
        })
        val localData = storiesDatabase.storiesDao().getStories()
        result.addSource(localData) { newData: List<StoriesEntity> ->
            result.value = Result.Success(newData)
        }
        return result
    }

    companion object {
        @Volatile
        private var instance: StoriesRepository? = null
        fun getInstance(
            apiService: ApiService,
            appExecutors: AppExecutors,
            storiesDatabase: StoriesDatabase
        ): StoriesRepository =
            instance ?: synchronized(this) {
                instance ?: StoriesRepository(apiService, appExecutors, storiesDatabase)
            }.also { instance = it }
    }
}