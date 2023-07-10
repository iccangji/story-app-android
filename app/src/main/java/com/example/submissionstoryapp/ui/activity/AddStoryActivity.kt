package com.example.submissionstoryapp.ui.activity

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.databinding.ActivityAddStoryBinding
import com.example.submissionstoryapp.media.uriToFile
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.view_model.AddStoryViewModel
import com.example.submissionstoryapp.ui.view_model.UserViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class AddStoryActivity : AppCompatActivity() {
    private lateinit var addStoryBinding: ActivityAddStoryBinding
    private lateinit var addStoryViewModel: AddStoryViewModel
    private var photoFile: File? = null
    private var storyLat: Double? = null
    private var storyLon: Double? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addStoryBinding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(addStoryBinding.root)
        supportActionBar?.title = resources.getString(R.string.add_story)
        getPhotoStory()

        val pref = UserPreferences.getInstance(dataStore)
        addStoryViewModel = ViewModelProvider(this, UserViewModelFactory(pref))[AddStoryViewModel::class.java]

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        addStoryBinding.cbStory.setOnClickListener {
            if(addStoryBinding.cbStory.isChecked){
                getMyLocation()
            }
            else{
                storyLat = null
                storyLon = null
            }
        }

        addStoryBinding.buttonAdd.setOnClickListener {
            addStoryViewModel.uploadStory(photoFile!!, addStoryBinding.edAddDescription.text.toString(), storyLat, storyLon)
            uploadStory()
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    getMyLocation()
                }
                permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    getMyLocation()
                }
                else -> {
                    Toast.makeText(this, resources.getString(R.string.not_granted_permission), Toast.LENGTH_SHORT).show()
                }
            }
        }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private fun getMyLocation() {
        if(checkPermission(android.Manifest.permission.ACCESS_FINE_LOCATION) &&
            checkPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        ){
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    storyLat = location.latitude
                    storyLon = location.longitude
                } else {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.location_not_found),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun uploadStory() {
        addStoryViewModel.isLoading.observe(this){
            addStoryBinding.pbAddStory?.visibility = if(it) View.VISIBLE
            else View.GONE
        }
        addStoryViewModel.successAddStory.observe(this){
            if(it == true){
                val intent = Intent(this@AddStoryActivity, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else if (it==false){
                Toast.makeText(this@AddStoryActivity, resources.getString(R.string.failed_add_story), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getPhotoStory() {
        var photo =  if(Build.VERSION.SDK_INT >= 33) {
            intent.getParcelableExtra("CAMERA_PHOTO", File::class.java)
        }
            else {
            @Suppress("DEPRECATION")
            intent.extras?.get("CAMERA_PHOTO")
        }

        if (photo!=null){
            photoFile = photo as File
            addStoryBinding.ivPreview.setImageBitmap(BitmapFactory.decodeFile(photo.path))
        }
        else{
            photo =  if(Build.VERSION.SDK_INT >= 33) {
                intent.getParcelableExtra("GALLERY_PHOTO", Uri::class.java)
            }
            else {
                @Suppress("DEPRECATION")
                intent.extras?.get("GALLERY_PHOTO")
            }
            photoFile = uriToFile(photo as Uri, this@AddStoryActivity)
            addStoryBinding.ivPreview.setImageURI(photo)
        }
    }
}