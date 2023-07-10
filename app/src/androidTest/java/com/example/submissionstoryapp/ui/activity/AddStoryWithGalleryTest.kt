package com.example.submissionstoryapp.ui.activity

import android.app.Activity
import android.app.Instrumentation
import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.utils.EspressoIdlingResource
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


@RunWith(AndroidJUnit4::class)
@LargeTest
class AddStoryWithGalleryTest{
    private val dummyDescription = "semangat yang lagi develop testing"

    @get:Rule
    val activity = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Intents.init()

    }
    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
        Intents.release()
    }

    @Test
    fun addStory_Failed() {
        lateinit var galleryActivityResult : ActivityResult
        lateinit var mainActivity: Activity
        activity.scenario.onActivity {
            mainActivity = it
        }
        galleryActivityResult = createImageActivityResultStub(mainActivity)
        saveImage(mainActivity)

        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(galleryActivityResult)
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withId(R.id.fab_add_story)).perform(click())
        onView(withText("Gallery"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
        intended(hasComponent(AddStoryActivity::class.java.name))
        onView(withId(R.id.iv_preview)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_add_description)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_add_description)).perform(typeText(dummyDescription), closeSoftKeyboard())
        onView(withId(R.id.button_add)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add)).perform(click())
        onView(withText(R.string.failed_add_story)).inRoot(withDecorView(not(mainActivity.window.decorView))).check(matches(isDisplayed()))
    }

    @Test
    fun addStory_Success() {
        lateinit var galleryActivityResult : ActivityResult
        activity.scenario.onActivity {
            galleryActivityResult = createImageActivityResultStub(it)
            saveImage(it)
        }
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(galleryActivityResult)
        onView(withId(R.id.rv_stories)).check(matches(isDisplayed()))
        onView(withId(R.id.fab_add_story)).perform(click())
        onView(withText("Gallery"))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())
        intended(hasComponent(AddStoryActivity::class.java.name))
        onView(withId(R.id.iv_preview)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_add_description)).check(matches(isDisplayed()))
        onView(withId(R.id.ed_add_description)).perform(typeText(dummyDescription), closeSoftKeyboard())
        onView(withId(R.id.button_add)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add)).perform(click())

//        Launch activityScenario again, because the activity using intent with flag new task after add story
        ActivityScenario.launch(MainActivity::class.java)
        onView(withId(R.id.rv_stories)).perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.tv_detail_description)).check(matches(withText(dummyDescription)))
    }

    private fun saveImage(activity: Activity) {
        val bm = BitmapFactory.decodeResource(activity.resources, R.drawable.image_testing)
        val dir = activity.externalCacheDir
        val file = File(dir?.path, "pickImageResult.jpeg")
        val outStream: FileOutputStream?
        try {
            outStream = FileOutputStream(file)
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream)
            with(outStream) {
                flush()
                close()
            }
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
    }

    private fun createImageActivityResultStub(activity: Activity): ActivityResult {
        val resultData = Intent()
        val dir = activity.externalCacheDir
        val file = File(dir?.path, "pickImageResult.jpeg")
        val uri = Uri.fromFile(file)
        resultData.data = uri
        return Instrumentation.ActivityResult(Activity.RESULT_OK, resultData)
    }
}