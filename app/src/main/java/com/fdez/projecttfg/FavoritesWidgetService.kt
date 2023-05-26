package com.fdez.projecttfg

import android.appwidget.AppWidgetManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.fdez.projecttfg.Api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FavoritesWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FavoritesWidgetRemoteViewsFactory(applicationContext)
    }
}

class FavoritesWidgetRemoteViewsFactory(private val context: Context) : RemoteViewsService.RemoteViewsFactory {
    private val itemList = mutableListOf<String>()

    override fun onCreate() {}

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onDataSetChanged() {
        // Obtén aquí la lista completa de elementos que deseas mostrar en el widget
        itemList.clear()
        itemList.addAll(obtenerListaCompleta())
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
        remoteViews.setTextViewText(android.R.id.text1, itemList[position])
        return remoteViews
    }

    override fun getCount(): Int = itemList.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {}

    private fun obtenerListaCompleta(): List<String> {
        // Aquí debes obtener la lista completa de elementos que deseas mostrar en el widget
        // Puedes reemplazar este código con tu propia lógica para obtener la lista
        return listOf("Elemento 1", "Elemento 2", "Elemento 3", "Elemento 4", "Elemento 5", "Elemento 5", "Elemento 5", "Elemento 5", "Elemento 5", "Elemento 5", "Elemento 5", "Elemento 5", "Elemento 5")
    }
}

