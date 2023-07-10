package com.example.submissionstoryapp.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import com.example.submissionstoryapp.preference.UserPreferences
import kotlinx.coroutines.launch

class LogoutViewModel(private val pref: UserPreferences, private val storiesRepository: StoriesRepository): ViewModel() {
    fun logoutUser(){
        viewModelScope.launch {
            pref.setUserLogin(
                LoginResult("","","")
            )
        }
        storiesRepository.deleteStories()
    }
}