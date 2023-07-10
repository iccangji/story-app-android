package com.example.submissionstoryapp.ui.view_model

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.submissionstoryapp.data.remote.entity.AddStoryResponse
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import com.example.submissionstoryapp.data.remote.retrofit.ApiConfig
import com.example.submissionstoryapp.media.reduceFileImage
import com.example.submissionstoryapp.preference.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel(private val pref: UserPreferences): ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _successAddStory = MutableLiveData<Boolean?>()
    val successAddStory: MutableLiveData<Boolean?> = _successAddStory

    private var latitude: RequestBody? = null
    private var longitude: RequestBody? = null

    fun uploadStory(img: File, desc: String, lat: Double?, lon: Double?){
        _isLoading.value = true
        _successAddStory.value = null
        val userData: LoginResult
        runBlocking {
            userData = pref.getUserLogin().first()
        }

        val file = reduceFileImage(img)
        val description = desc.toRequestBody("text/plain".toMediaType())

        if(lat!=null&&lon!=null) {
             latitude = lat.toString().toRequestBody("text/plain".toMediaType())
             longitude = lon.toString().toRequestBody("text/plain".toMediaType())
         }


        val requestImageFile = file.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            file.name,
            requestImageFile
        )
        val service = ApiConfig.getApiService()

        val storyRequest= if(latitude!=null&&longitude!=null)
            service.addStoryWithLocation("Bearer ${userData.token}", description, imageMultipart, latitude!!, longitude!!)
            else service.addStory("Bearer ${userData.token}", description, imageMultipart)
        storyRequest.enqueue(object : Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ){
                _isLoading.value = false
                _successAddStory.value = response.isSuccessful
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _successAddStory.value = false
                Log.e("Register Error", "onFailure: ${t.message}")
            }
        })
    }
}