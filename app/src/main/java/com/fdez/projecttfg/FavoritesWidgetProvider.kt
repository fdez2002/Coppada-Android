package com.fdez.projecttfg

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.ArrayAdapter
import android.widget.RemoteViews

class FavoritesWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        val remoteViews = RemoteViews(context.packageName, R.layout.favorites_widget)

        // Configura el adaptador del ListView
        val intent = Intent(context, FavoritesWidgetService::class.java)
        remoteViews.setRemoteAdapter(R.id.list, intent)

        // Notifica al administrador del widget que los datos han cambiado
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)

        // Actualiza el widget
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }
}
