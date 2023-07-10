package com.example.submissionstoryapp.ui.widget

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.example.submissionstoryapp.R
import com.example.submissionstoryapp.ui.activity.DetailsStoryActivity

/**
 * Implementation of App Widget functionality.
 */
class ListStoriesWidget: AppWidgetProvider() {

    companion object {
        private const val TOAST_ACTION = "com.example.submissionstoryapp.TOAST_ACTION"
        const val EXTRA_ITEM = "com.example.submissionstoryapp.EXTRA_ITEM"
        private fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val intent = Intent(context, StackWidgetService::class.java)
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            intent.data = intent.toUri(Intent.URI_INTENT_SCHEME).toUri()
            val views = RemoteViews(context.packageName, R.layout.list_stories_widget)

            views.setRemoteAdapter(R.id.stack_view, intent)
            views.setEmptyView(R.id.stack_view, R.id.empty_view)

            val toastIntent = Intent(context, ListStoriesWidget::class.java)
            toastIntent.action = TOAST_ACTION
            toastIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            val toastPendingIntent = PendingIntent.getBroadcast(
                context, 0, toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            )
            views.setPendingIntentTemplate(R.id.stack_view, toastPendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.layout.list_stories_widget)
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            if (intent.action == TOAST_ACTION) {
                val storyId = intent.getStringExtra(EXTRA_ITEM)
//                Toast.makeText(context, "Touched view $viewIndex", Toast.LENGTH_SHORT).show()
                val mIntent = Intent(context, DetailsStoryActivity::class.java).apply {
                    putExtra(DetailsStoryActivity.STORY_ID, storyId)
                }
                val pendingIntent = PendingIntent.getActivity(
                    context, 0, mIntent, PendingIntent.FLAG_UPDATE_CURRENT
                )
                pendingIntent.send()
            }
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }
}