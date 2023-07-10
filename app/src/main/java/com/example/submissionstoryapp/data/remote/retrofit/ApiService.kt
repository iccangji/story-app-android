package com.example.submissionstoryapp.data.remote.retrofit

import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.remote.entity.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @Multipart
    @POST("stories")
    fun addStory(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
    ): Call<AddStoryResponse>

    @Multipart
    @POST("stories")
    fun addStoryWithLocation(
        @Header("Authorization") token: String,
        @Part("description") description: RequestBody,
        @Part file: MultipartBody.Part,
        @Part("lat") latitude: RequestBody,
        @Part("lon") longitude: RequestBody,
    ): Call<AddStoryResponse>

    @GET("stories")
    fun getListStories(
        @Header("Authorization") token : String,
        @Query("location") location: Int,
    ): Call<ListStoriesResponse>

    @GET("stories")
    suspend fun getPagedListStories(
        @Header("Authorization") token : String,
        @Query("location") location: Int,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): ListStoriesResponse

    @GET("stories/{id}")
    fun getDetailsStory(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Call<DetailsStoriesResponse>
}