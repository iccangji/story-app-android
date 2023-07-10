package com.example.submissionstoryapp.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.submissionstoryapp.data.local.Result
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.utils.wrapEspressoIdlingResource
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class ListStoryViewModel(private val pref: UserPreferences, private val storiesRepository: StoriesRepository): ViewModel() {
    val userLogin by lazy {pref.getUserLogin().asLiveData()}

    fun getListStories(token: String): LiveData<PagingData<StoriesEntity>> {
            return storiesRepository.getStories(token).cachedIn(viewModelScope)
    }
}
