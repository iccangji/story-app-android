package com.example.submissionstoryapp.preference

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.submissionstoryapp.data.remote.entity.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val USER_NAME = stringPreferencesKey("user_name")
    private val USER_ID = stringPreferencesKey("user_id")
    private val USER_TOKEN = stringPreferencesKey("user_token")


    fun getUserLogin(): Flow<LoginResult>{
        return dataStore.data.map {
            LoginResult(
                it[USER_NAME]?:"",
                it[USER_ID]?:"",
                it[USER_TOKEN]?:""
            )
        }
    }

    suspend fun setUserLogin(loginResult: LoginResult){
        dataStore.edit {
            it[USER_NAME] = loginResult.name
            it[USER_ID] = loginResult.userId
            it[USER_TOKEN] = loginResult.token
        }
    }

    companion object{
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences{
            return INSTANCE ?: synchronized(this){
                val instance = UserPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}