package com.example.submissionstoryapp.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.submissionstoryapp.data.remote.entity.LoginResponse
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import com.example.submissionstoryapp.data.remote.retrofit.ApiConfig
import com.example.submissionstoryapp.preference.UserPreferences
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginViewModel(private val pref: UserPreferences): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _failedLogin = MutableLiveData<Boolean>()
    val failedLogin: LiveData<Boolean> = _failedLogin

    private val _incorrectLogin = MutableLiveData<Boolean>()
    val incorrectLogin: LiveData<Boolean> = _incorrectLogin

    private val _successLogin = MutableLiveData<Boolean>()
    val successLogin: LiveData<Boolean> = _successLogin

    fun saveUserLogin(loginResult: LoginResult){
        viewModelScope.launch {
            pref.setUserLogin(loginResult)
        }
    }

    init {
        _isLoading.value = false
    }

    fun loginAccount(email: String, password: String) {
        _isLoading.value = true
        val service = ApiConfig.getApiService().login(email, password)
        service.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _successLogin.value = true
                    response.body()?.let { saveUserLogin(it.loginResult) }
                } else {
                    _failedLogin.value = true
                    _incorrectLogin.value = response.message() == "Unauthorized"
                    Log.e("Register Error", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                _isLoading.value = false
                _failedLogin.value = true
                _incorrectLogin.value = false
                Log.e("Error", "onFailure: ${t.message}")
            }
        })
    }
}