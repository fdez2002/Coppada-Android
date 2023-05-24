package com.fdez.projecttfg

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Implementation of App Widget functionality.
 */
class FavoritesWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.favorites_widget)

    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val firestore = FirebaseFirestore.getInstance()
    val collection = firestore.collection("likes")

    val favoriteRestaurants = mutableListOf<String>()

    if (userId != null) {
        collection
            .whereEqualTo("id_user", userId)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val alias = document.getString("alias")
                    if (alias != null) {
                        favoriteRestaurants.add(alias)
                    }
                }
                if (favoriteRestaurants.isNotEmpty()) {
                    val adapterIntent = Intent(context, FavoritesWidgetService::class.java)
                    adapterIntent.putExtra("favoriteRestaurants", ArrayList(favoriteRestaurants))
                    views.setRemoteAdapter(R.id.favorite_list_item, adapterIntent)
                } else {
                    views.setTextViewText(
                        R.id.favorite_list_item,
                        context.getString(R.string.widget_no_favorites_message)
                    )
                }
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
            .addOnFailureListener { exception ->
                Log.e("FavoritesWidget", "Error al obtener los restaurantes favoritos: $exception")
                views.setTextViewText(
                    R.id.favorite_list_item,
                    context.getString(R.string.widget_no_favorites_message)
                )
                appWidgetManager.updateAppWidget(appWidgetId, views)
            }
    } else {
        views.setTextViewText(
            R.id.favorite_list_item,
            context.getString(R.string.widget_no_favorites_message)
        )
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

class FavoritesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FavoritesRemoteViewsFactory(applicationContext, intent)
    }
}

class FavoritesRemoteViewsFactory(
    private val context: Context,
    private val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private val favoriteRestaurants = mutableListOf<String>()

    override fun onCreate() {
        // Obtener los datos del intent
        val extras = intent.extras
        if (extras != null) {
            val restaurants = extras.getStringArrayList("favoriteRestaurants")
            if (restaurants != null) {
                favoriteRestaurants.addAll(restaurants)
            }
        }
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onDataSetChanged() {
        // No es necesario implementar esta función para la funcionalidad básica
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getViewAt(position: Int): RemoteViews? {
        if (position < 0 || position >= favoriteRestaurants.size) {
            return null
        }

        val restaurant = favoriteRestaurants[position]

        // Configurar la vista del elemento de la lista
        val views = RemoteViews(context.packageName, R.layout.item_card_locales_widget)
        views.setTextViewText(R.id.textView, restaurant)

        return views
    }

    override fun getCount(): Int {
        return favoriteRestaurants.size
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun onDestroy() {
        // No es necesario implementar esta función para la funcionalidad básica
    }
}
