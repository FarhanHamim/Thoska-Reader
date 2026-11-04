package de.thoska.pulse.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import de.thoska.pulse.MainActivity
import de.thoska.pulse.R
import de.thoska.pulse.data.ScanHistoryRepository

/**
 * 3x1 Retro Olive-Green Dot-Matrix LCD Home Screen Mini-Widget.
 * Shows the last scanned card balance, meal transaction delta, and scan timestamp.
 */
class ThoskaAppWidget : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val historyRepo = ScanHistoryRepository(context)
            val latest = historyRepo.historyFlow.value.firstOrNull()

            val views = RemoteViews(context.packageName, R.layout.thoska_widget_layout)

            if (latest != null) {
                views.setTextViewText(R.id.widget_balance, "LAST BAL: ${latest.formattedBalance}")
                views.setTextViewText(R.id.widget_tx, "MEAL: -${latest.formattedLastTransaction}")
                views.setTextViewText(R.id.widget_timestamp, latest.formattedDate.take(16))
            } else {
                views.setTextViewText(R.id.widget_balance, "LAST BAL: --.-- €")
                views.setTextViewText(R.id.widget_tx, "MEAL: --.-- €")
                views.setTextViewText(R.id.widget_timestamp, "--.-- €")
            }

            // PendingIntent to launch MainActivity on click
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                } else {
                    PendingIntent.FLAG_UPDATE_CURRENT
                }
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }

        fun updateAllWidgets(context: Context) {
            try {
                val appWidgetManager = AppWidgetManager.getInstance(context)
                val componentName = ComponentName(context, ThoskaAppWidget::class.java)
                val ids = appWidgetManager.getAppWidgetIds(componentName)
                if (ids != null && ids.isNotEmpty()) {
                    val intent = Intent(context, ThoskaAppWidget::class.java).apply {
                        action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
                        putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
                    }
                    context.sendBroadcast(intent)
                }
            } catch (_: Exception) {
                // Ignore errors if widget framework not active
            }
        }
    }
}

