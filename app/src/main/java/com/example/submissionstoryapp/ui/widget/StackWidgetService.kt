package com.example.submissionstoryapp.ui.widget

import android.content.Context
import android.content.Intent
import android.widget.RemoteViewsService
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.submissionstoryapp.data.injection.Injection
import com.example.submissionstoryapp.preference.UserPreferences
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class StackWidgetService: RemoteViewsService() {
    override fun onGetViewFactory(p0: Intent?): RemoteViewsFactory {
        val pref = UserPreferences.getInstance(dataStore)
        return StackRemoteViewsFactory(this.applicationContext, Injection.provideRepository(this.applicationContext), pref)
    }
}