package com.example.submissionstoryapp.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.remote.entity.DetailsStoriesResponse
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import com.example.submissionstoryapp.data.remote.entity.Story
import com.example.submissionstoryapp.data.remote.retrofit.ApiConfig
import com.example.submissionstoryapp.preference.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailsStoryViewModel(private val pref: UserPreferences, storyId: String): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _detailsStory = MutableLiveData<Story>()
    val detailsStory: LiveData<Story> = _detailsStory

    private val _failedGetDetails = MutableLiveData<Boolean>()
    val failedGetDetails: LiveData<Boolean> = _failedGetDetails

    init {
        getDetailsStory(storyId)
    }
    private fun getDetailsStory(storyId: String) {
        _isLoading.value = true
        val userData: LoginResult
        runBlocking {
            userData = pref.getUserLogin().first()
        }

        val service = ApiConfig.getApiService().getDetailsStory("bearer " + userData.token, storyId)
        service.enqueue(object : Callback<DetailsStoriesResponse> {
            override fun onResponse(
                call: Call<DetailsStoriesResponse>,
                response: Response<DetailsStoriesResponse>
            ){
                _isLoading.value = false
                if (response.isSuccessful){
                    _detailsStory.value = response.body()?.story
                }
                else{
                    _failedGetDetails.value = true
                }
            }

            override fun onFailure(call: Call<DetailsStoriesResponse>, t: Throwable) {
                _isLoading.value = false
                _failedGetDetails.value = true
                Log.e("Register Error", "onFailure: ${t.message}")
            }
        })
    }
}