package com.example.submissionstoryapp.ui.fragment

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.injection.Injection
import com.example.submissionstoryapp.data.local.Result
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.databinding.FragmentMapStoryBinding
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.activity.DetailsStoryActivity
import com.example.submissionstoryapp.ui.view_model.DataStoryViewModelFactory
import com.example.submissionstoryapp.ui.view_model.MapStoryViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.MarkerOptions
import java.io.IOException
import java.util.*


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class MapStoryFragment : Fragment(), OnMapReadyCallback {
    private var mMap: GoogleMap? = null
    private var mapFragment: SupportMapFragment? = null
    private var _binding : FragmentMapStoryBinding? = null
    private val mapStoryBinding get() = _binding!!
    private lateinit var mapStoryViewModel: MapStoryViewModel
    private val boundsBuilder = LatLngBounds.Builder()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapStoryBinding.inflate(inflater, container, false)
        requireActivity().title = resources.getString(R.string.app_name)

        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val injection = Injection.provideRepository(requireActivity())
        mapStoryViewModel = ViewModelProvider(this, DataStoryViewModelFactory(pref, injection))[MapStoryViewModel::class.java]
        getAllStories()

        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        return mapStoryBinding.root
    }

    private fun setMapStyle() {
        try {
            mMap?.setMapStyle(MapStyleOptions.loadRawResourceStyle(requireContext(), R.raw.map_style))
        } catch (exception: Resources.NotFoundException) {
            Log.e("ERR", "Can't find style. Error: ", exception)
        }
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                getMyLocation()
            }else{
                Toast.makeText(requireActivity(), resources.getString(R.string.not_granted_permission), Toast.LENGTH_SHORT).show()
            }
        }
    private fun getMyLocation() {
            if (ContextCompat.checkSelfPermission(
                    requireContext().applicationContext,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                mMap?.isMyLocationEnabled = true
            } else {
                requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
            }
    }

    private fun getAllStories() {
        mapStoryViewModel.getMapStories().observe(viewLifecycleOwner){
        mMap?.clear()
            if(it!=null){
                when(it){
                    is Result.Success -> {
                        mapStoryBinding.pbMap.visibility = View.GONE
                        mapStoryBinding.map.visibility = View.VISIBLE
                        insertLocation(it.data)
                    }
                    is Result.Error -> {
                        mapStoryBinding.pbMap.visibility = View.GONE
                        mapStoryBinding.pbMap.visibility = View.VISIBLE
                        Toast.makeText(
                            context,
                            resources.getString(R.string.error_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    is Result.Loading -> {
                        mapStoryBinding.pbMap.visibility = View.VISIBLE
                        mapStoryBinding.map.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun insertLocation(listStories: List<StoriesEntity>) {
        listStories.forEach {
            if (it.lat != null && it.lon != null) {
                if(it.lat.toInt() in -90 until 90
                    && it.lon.toInt() in -180 until 180) {
                    val storyLocation = LatLng(it.lat, it.lon)
                    val storyAddress = getAddressStory(it.lat, it.lon)
                    val marker = mMap?.addMarker(
                        MarkerOptions().position(storyLocation).title(it.name).snippet(storyAddress)
                    )
                    marker?.tag = it.id
                    boundsBuilder.include(storyLocation)
                }
            }
        }
        mMap?.uiSettings?.isZoomGesturesEnabled = true
        mMap?.uiSettings?.isZoomControlsEnabled = true
        mMap?.uiSettings?.isCompassEnabled = true

        if(listStories[0].lat!=null) {
            val bounds: LatLngBounds = boundsBuilder.build()
            mMap?.animateCamera(
                CameraUpdateFactory.newLatLngBounds(
                    bounds,
                    resources.displayMetrics.widthPixels,
                    resources.displayMetrics.heightPixels,
                    300
                )
            )
        }
    }

    private fun getAddressStory(lat: Double, lon: Double): String? {
        var storyAddress: String? = null
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        try {
            val list = geocoder.getFromLocation(lat, lon, 1)
            if (list != null && list.size != 0) {
                storyAddress = list[0].subAdminArea + ", " + list[0].adminArea
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return storyAddress
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onMapReady(gMap: GoogleMap) {
        mMap = gMap
        getMyLocation()
        setMapStyle()

        var storyId : String
        mMap?.setOnMarkerClickListener { marker ->
            storyId = marker.tag as String
            mMap?.setOnInfoWindowClickListener {
                val intent = Intent(requireActivity(), DetailsStoryActivity::class.java)
                intent.putExtra(DetailsStoryActivity.STORY_ID, storyId)
                startActivity(intent)
            }
            false
        }
    }
}