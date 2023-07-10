package com.example.submissionstoryapp.ui.fragment

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.injection.Injection
import com.example.submissionstoryapp.databinding.FragmentListStoryBinding
import com.example.submissionstoryapp.media.createCustomTempFile
import com.example.submissionstoryapp.preference.UserPreferences
import com.example.submissionstoryapp.ui.activity.AddStoryActivity
import com.example.submissionstoryapp.ui.activity.OnboardPageActivity
import com.example.submissionstoryapp.ui.adapter.ListStoriesAdapter
import com.example.submissionstoryapp.ui.adapter.LoadingStateAdapter
import com.example.submissionstoryapp.ui.view_model.DataStoryViewModelFactory
import com.example.submissionstoryapp.ui.view_model.ListStoryViewModel
import java.io.File

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")
class ListStoryFragment : Fragment() {
    private var _binding : FragmentListStoryBinding? = null
    private val listStoryBinding get() = _binding!!
    private lateinit var listStoryViewModel: ListStoryViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentListStoryBinding.inflate(inflater, container, false)
        requireActivity().title = resources.getString(R.string.app_name)

        val pref = UserPreferences.getInstance(requireActivity().dataStore)
        val injection = Injection.provideRepository(requireActivity())
        listStoryViewModel = ViewModelProvider(this, DataStoryViewModelFactory(pref, injection))[ListStoryViewModel::class.java]

        setupLogin()
        return listStoryBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val layoutManager = LinearLayoutManager(context)
        listStoryBinding.rvStories.layoutManager = layoutManager
        val itemDecoration = DividerItemDecoration(context, 0)
        listStoryBinding.rvStories.addItemDecoration(itemDecoration)

        listStoryBinding.fabAddStory.setOnClickListener {
            dialogAddStory()
        }
    }

    //    GET USER
    private fun setupLogin() {
        listStoryViewModel.userLogin.observe(viewLifecycleOwner){
            if(it.name == ""){
                val intent = Intent(requireActivity(), OnboardPageActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
            }
            else{
                val token = it.token
                getAllStories(token)
            }
        }
    }

    //    GET LIST STORIES
    private var loadStateErrorIsNotLaunched = true
    private fun getAllStories(token: String) {
        val adapter = ListStoriesAdapter()

        adapter.addLoadStateListener {
            when(it.refresh){
                is LoadState.Loading -> {
                    if(loadStateErrorIsNotLaunched) {
                        listStoryBinding.pbList.visibility = View.VISIBLE
                    }
                }
                is LoadState.NotLoading -> {
                    listStoryBinding.pbList.visibility = View.GONE
                }
                is LoadState.Error -> {
                    if(loadStateErrorIsNotLaunched) {
                        listStoryBinding.pbList.visibility = View.GONE
                        Toast.makeText(
                            context,
                            resources.getString(R.string.error_connection),
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d("loadState", "error")
                        loadStateErrorIsNotLaunched = !loadStateErrorIsNotLaunched
                    }
                }
            }
        }

        listStoryBinding.rvStories.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        listStoryViewModel.getListStories(token).observe(viewLifecycleOwner) {
            adapter.submitData(lifecycle, it)
        }
    }

    //    PHOTO PICKER
    private fun dialogAddStory() {
        val alertDialogBuilder = AlertDialog.Builder(context)
        val photoPicker = arrayOf(
            resources.getString(R.string.camera),
            resources.getString(R.string.gallery)
        )
        alertDialogBuilder.setTitle(R.string.select_photo_dialog)
        alertDialogBuilder.setItems(photoPicker
        ) { _ , which ->
            when(which){
                0 -> startCamera()
                1 -> startGallery()
            }
        }
        alertDialogBuilder.create()
        alertDialogBuilder.show()
    }

    //    GALLERY PHOTO PICKER
    private val galleryLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val selectedPhoto = it.data?.data as Uri
            selectedPhoto.let { uri ->
                val intent = Intent(requireActivity(), AddStoryActivity::class.java)
                intent.putExtra("GALLERY_PHOTO",uri)
                startActivity(intent)
            }
        }
    }
    private fun startGallery() {
        val intent = Intent()
        intent.action = Intent.ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, resources.getString(R.string.gallery_dialog))
        galleryLauncher.launch(chooser)
    }

    //    CAMERA PHOTO PICKER
    private lateinit var currentPhotoPath: String
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            val myFile = File(currentPhotoPath)

            myFile.let { file ->
                val intent = Intent(requireActivity(), AddStoryActivity::class.java)
                intent.putExtra("CAMERA_PHOTO",file)
                startActivity(intent)
            }
        }
    }
    private fun startCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(requireActivity().packageManager)

        createCustomTempFile(requireActivity().application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                requireActivity(),
                "com.example.submissionstoryapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            cameraLauncher.launch(intent)
        }
    }

}