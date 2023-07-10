package com.example.submissionstoryapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.injection.Injection
import com.example.submissionstoryapp.databinding.ActivitySettingsBinding
import com.example.submissionstoryapp.preference.DarkThemePreferences
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.view_model.DarkThemeViewModelFactory
import com.example.submissionstoryapp.ui.view_model.DataStoryViewModelFactory
import com.example.submissionstoryapp.ui.view_model.IsDarkThemeViewModel
import com.example.submissionstoryapp.ui.view_model.LogoutViewModel

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
private val Context.dataStoreDarkTheme: DataStore<Preferences> by preferencesDataStore(name = "dark_theme")
class SettingsActivity : AppCompatActivity() {
    private lateinit var settingsBinding: ActivitySettingsBinding
    private lateinit var logoutViewModel: LogoutViewModel
    private lateinit var isDarkThemeViewModel: IsDarkThemeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        settingsBinding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(settingsBinding.root)
        val pref = UserPreferences.getInstance(dataStore)
        val injection = Injection.provideRepository(this)
        val prefDarkTheme = DarkThemePreferences.getInstance(dataStoreDarkTheme)
        logoutViewModel = ViewModelProvider(this, DataStoryViewModelFactory(pref, injection))[LogoutViewModel::class.java]
        isDarkThemeViewModel = ViewModelProvider(this, DarkThemeViewModelFactory(prefDarkTheme))[IsDarkThemeViewModel::class.java]

        setupDarkTheme()
        settingsBinding.itemSettingsDarkMode.setOnClickListener{setupSwitchTheme()}
        settingsBinding.switchTheme.setOnClickListener{
            isDarkThemeViewModel.setDarkTheme(settingsBinding.switchTheme.isChecked)
        }

        supportActionBar?.title = resources.getString(R.string.settings)
        settingsBinding.actionLogout.setOnClickListener { logoutAccount() }
    }

    private fun setupDarkTheme() {
        isDarkThemeViewModel.getDarkTheme().observe(this){
            if(it) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            settingsBinding.switchTheme.isChecked = it
        }
    }

    private fun setupSwitchTheme() {
        settingsBinding.switchTheme.isChecked = !settingsBinding.switchTheme.isChecked
        isDarkThemeViewModel.setDarkTheme(settingsBinding.switchTheme.isChecked)
    }

    private fun logoutAccount(){
        isDarkThemeViewModel.setDarkTheme(false)
        logoutViewModel.logoutUser()
        val intent = Intent(this@SettingsActivity, OnboardPageActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == android.R.id.home) finish()
        return true
    }

}