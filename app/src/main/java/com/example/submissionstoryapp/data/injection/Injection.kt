package com.example.submissionstoryapp.data.injection

import android.content.Context
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.data.local.room.StoriesDatabase
import com.example.submissionstoryapp.data.remote.retrofit.ApiConfig
import com.example.submissionstoryapp.data.utils.AppExecutors

object Injection {
    fun provideRepository(context: Context): StoriesRepository {
        val apiService = ApiConfig.getApiService()
        val database = StoriesDatabase.getInstance(context)
        val appExecutors = AppExecutors()

        return StoriesRepository.getInstance(apiService, appExecutors, database)
    }
}