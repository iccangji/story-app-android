package com.example.submissionstoryapp.ui.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import com.bumptech.glide.Glide
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.data.local.entity.StoriesEntity
import com.example.submissionstoryapp.data.local.repository.StoriesRepository
import com.example.submissionstoryapp.preference.UserPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

internal class StackRemoteViewsFactory(
    private val mContext: Context,
    private val storyData: StoriesRepository,
    private val pref: UserPreferences
    ): RemoteViewsService.RemoteViewsFactory {

    private var mWidgetItems = ArrayList<StoriesEntity>()
    private lateinit var mStoryItems: List<StoriesEntity>
    private val userData = runBlocking {pref.getUserLogin().first()}
    override fun onCreate() {

    }

    override fun onDataSetChanged() {
        mStoryItems = storyData.getStoriesWidget(userData.token)
        mWidgetItems.clear()
        mWidgetItems.addAll(mStoryItems)

        Log.d("WIDGET",mWidgetItems[0].name)
    }

    override fun onDestroy() {

    }

    override fun getCount(): Int = mWidgetItems.size

    override fun getViewAt(p0: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        val rvImage = Glide.with(mContext)
                .asBitmap()
                .load(mWidgetItems[p0].photoUrl)
                .submit(512,512)
                .get()
        rv.setImageViewBitmap(R.id.imageView, rvImage)
        rv.setTextViewText(R.id.textView, mWidgetItems[p0].name)

        val extras = bundleOf(
            ListStoriesWidget.EXTRA_ITEM to mWidgetItems[p0].id
        )
        val fillInIntent = Intent()
        fillInIntent.putExtras(extras)

        rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        return rv
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(p0: Int): Long = 0

    override fun hasStableIds(): Boolean = false
}