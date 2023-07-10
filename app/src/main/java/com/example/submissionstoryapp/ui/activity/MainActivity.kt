package com.example.submissionstoryapp.ui.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment.Companion.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityMainBinding
import com.example.submissionstoryapp.preference.DarkThemePreferences
import com.example.submissionstoryapp.ui.view_model.DarkThemeViewModelFactory
import com.example.submissionstoryapp.ui.view_model.IsDarkThemeViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView

private val Context.dataStoreDarkTheme: DataStore<Preferences> by preferencesDataStore(name = "dark_theme")
class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var isDarkThemeViewModel: IsDarkThemeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val navView: BottomNavigationView = mainBinding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_list, R.id.navigation_maps,
            ),

        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val prefDarkTheme = DarkThemePreferences.getInstance(dataStoreDarkTheme)
        isDarkThemeViewModel = ViewModelProvider(this, DarkThemeViewModelFactory(prefDarkTheme))[IsDarkThemeViewModel::class.java]

        setupDarkTheme()
        supportActionBar?.elevation = 0f
    }

//    DARK THEME
    private fun setupDarkTheme() {
        isDarkThemeViewModel.getDarkTheme().observe(this){
            if(it) AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.item_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.menu_settings){
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
        return true
    }

    companion object {
        @StringRes
        private val TAB_TITLES = intArrayOf(
            R.string.list_story,
            R.string.maps_story
        )
    }
}