package com.example.submissionstoryapp.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.remote.entity.RegisterResponse
import com.example.submissionstoryapp.data.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _failedRegister = MutableLiveData<Boolean>()
    val failedRegister: LiveData<Boolean> = _failedRegister

    private val _emailTaken = MutableLiveData<Boolean>()
    val emailTaken: LiveData<Boolean> = _emailTaken

    private val _successRegister = MutableLiveData<Boolean>()
    val successRegister: LiveData<Boolean> = _successRegister

    fun setFailedRegister(b: Boolean){
        _failedRegister.value = b
    }
    fun registerAccount(name: String, email: String, password: String) {
        _isLoading.value = true
        val service = ApiConfig.getApiService().register(name, email, password)
        service.enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    _successRegister.value = true
                } else {
                    _failedRegister.value = true
                    _emailTaken.value = response.message() == "Bad Request"
                    Log.e("Register Error", "onFailure: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                _emailTaken.value = false
                _isLoading.value = false
                _failedRegister.value = true
                Log.e("Error", "onFailure: ${t.message}")
            }
        })
    }
}