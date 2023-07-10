package com.example.submissionstoryapp.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.local.Result
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import com.example.submissionstoryapp.preference.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MapStoryViewModel(private val pref: UserPreferences, private val storiesRepository: StoriesRepository) : ViewModel() {

    fun getMapStories(): LiveData<Result<List<StoriesEntity>>> {
        val userData: LoginResult
        runBlocking {
            userData = pref.getUserLogin().first()
        }
        return storiesRepository.getStoriesLocation(userData.token)
    }
}
