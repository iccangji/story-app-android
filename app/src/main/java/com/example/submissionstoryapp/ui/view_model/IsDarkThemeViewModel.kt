package com.example.submissionstoryapp.ui.view_model

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.submissionstoryapp.preference.DarkThemePreferences
import kotlinx.coroutines.launch

class IsDarkThemeViewModel(private val pref: DarkThemePreferences): ViewModel() {
    fun getDarkTheme(): LiveData<Boolean>{
        return pref.getThemeSetting().asLiveData()
    }
    fun setDarkTheme(b: Boolean){
        viewModelScope.launch {
            pref.saveThemeSetting(b)
        }
    }
}