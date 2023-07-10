package com.example.submissionstoryapp.ui.activity

import android.content.Context
import android.location.Geocoder
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.submissionstoryapp.databinding.ActivityDetailsStoryBinding
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.view_model.DetailsStoryViewModel
import com.example.submissionstoryapp.ui.view_model.StoryViewModelFactory
import java.io.IOException
import java.util.*

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class DetailsStoryActivity : AppCompatActivity() {
    private lateinit var detailsBinding: ActivityDetailsStoryBinding
    private lateinit var detailsViewModel: DetailsStoryViewModel
    companion object{
        const val STORY_ID= "story_id"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        detailsBinding = ActivityDetailsStoryBinding.inflate(layoutInflater)
        setContentView(detailsBinding.root)

        val pref = UserPreferences.getInstance(dataStore)
        val storyId = intent.getStringExtra(STORY_ID).toString()
        detailsViewModel = ViewModelProvider(this, StoryViewModelFactory(pref, storyId))[DetailsStoryViewModel::class.java]

        detailsViewModel.detailsStory.observe(this){story ->
            detailsBinding.apply {
                tvDetailName.text = story.name
                tvDetailDescription.text = story.description
                if(story.lat != null && story.lon!=null){
                    if(story.lat.toInt() in -90 until 90
                        && story.lon.toInt() in -180 until 180) {
                    tvDetailLocation.visibility = View.VISIBLE
                    tvDetailLocation.text = getLocation(story.lat, story.lon)
                    }
                }
            }
            Glide.with(this)
                .load(story.photoUrl)
                .into(detailsBinding.ivDetailPhoto)
        }

        detailsViewModel.isLoading.observe(this){
            if(it) detailsBinding.pbDetails.visibility = View.VISIBLE
            else detailsBinding.pbDetails.visibility = View.GONE
        }
        detailsViewModel.failedGetDetails.observe(this){
            if(it) detailsBinding.tvErrorConnection.visibility = View.VISIBLE
            else detailsBinding.tvErrorConnection.visibility = View.GONE
        }
    }

    private fun getLocation(lat: Double, lon: Double): String?{
        var storyAddress: String? = null
        val geocoder = Geocoder(this, Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                storyAddress = list[0].adminArea
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return storyAddress
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==android.R.id.home){
            finish()
        }
        return true
    }
}