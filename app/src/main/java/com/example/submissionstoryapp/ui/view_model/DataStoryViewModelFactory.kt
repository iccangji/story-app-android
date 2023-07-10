package com.example.submissionstoryapp.ui.view_model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.data.injection.Injection
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.preference.UserPreferences

class DataStoryViewModelFactory(private val pref: UserPreferences, private val storiesRepository: StoriesRepository): ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListStoryViewModel::class.java)) {
            return ListStoryViewModel(pref, storiesRepository) as T
        }
        else if (modelClass.isAssignableFrom(MapStoryViewModel::class.java)) {
            return MapStoryViewModel(pref, storiesRepository) as T
        }
        else if (modelClass.isAssignableFrom(LogoutViewModel::class.java)) {
            return LogoutViewModel(pref, storiesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
    }
    companion object {
        @Volatile
        private var instance: DataStoryViewModelFactory? = null
        fun getInstance(pref: UserPreferences,
        context: Context): DataStoryViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: DataStoryViewModelFactory(pref, Injection.provideRepository(context))
            }.also { instance = it }
    }
}