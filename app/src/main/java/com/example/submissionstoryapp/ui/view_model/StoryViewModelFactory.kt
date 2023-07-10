package com.example.submissionstoryapp.ui.view_model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.preference.UserPreferences

class StoryViewModelFactory(private val pref: UserPreferences, private val storyId: String): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
       if (modelClass.isAssignableFrom(DetailsStoryViewModel::class.java)) {
            return DetailsStoryViewModel(pref, storyId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
}