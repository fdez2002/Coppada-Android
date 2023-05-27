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

        // Define la acción para el PendingIntent que se lanzará al pulsar el widget
        val openFavoritesIntent = Intent(context, MainActivity::class.java).apply {
            action = "com.fdez.projecttfg.ACTION_OPEN_FAVORITES"
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openFavoritesIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE // Agrega la bandera FLAG_IMMUTABLE
        )

        // Asigna el PendingIntent a la vista del widget
        remoteViews.setPendingIntentTemplate(R.id.list, pendingIntent)

        // Notifica al administrador del widget que los datos han cambiado
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.list)

        // Actualiza el widget
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews)
    }
}
