package com.fdez.projecttfg

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.lifecycle.MutableLiveData
import com.fdez.projecttfg.Api.YelpApi
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

class FavoritesWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return FavoritesWidgetRemoteViewsFactory(applicationContext)
    }
}

class FavoritesWidgetRemoteViewsFactory(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    private val negocioList = mutableListOf<Negocio>()
    private var job: Job? = null

    override fun onCreate() {}

    override fun getLoadingView(): RemoteViews? = null

    override fun getItemId(position: Int): Long = position.toLong()

    override fun onDataSetChanged() {
        job?.cancel() // Cancela la corrutina anterior si existe

        job = CoroutineScope(Dispatchers.IO).launch {
            obtenerListaCompleta()
        }

        // Espera a que la corrutina se complete
        runBlocking {
            job?.join()
        }
    }

    override fun hasStableIds(): Boolean = true

    override fun getViewAt(position: Int): RemoteViews {
        val remoteViews = RemoteViews(context.packageName, android.R.layout.simple_list_item_1)
        remoteViews.setTextViewText(android.R.id.text1, negocioList[position].name)
        return remoteViews
    }

    override fun getCount(): Int = negocioList.size

    override fun getViewTypeCount(): Int = 1

    override fun onDestroy() {
        job?.cancel() // Cancela la corrutina al destruir el widget
    }

    private suspend fun obtenerListaCompleta() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        val exceptionHandler = CoroutineExceptionHandler { _, exception ->
            Log.e("tag", "Excepción en la corutina: $exception")
        }

        if (userId != null) {
            val offersCollection = FirebaseFirestore.getInstance().collection("likes")

            try {
                val documents = offersCollection
                    .whereEqualTo("id_user", userId)
                    .get()
                    .await()

                val newNegocioList = mutableListOf<Negocio>()

                for (document in documents) {
                    val alias = document.getString("alias")
                    if (alias != null) {
                        val negocio = YelpApi().getAlias(alias)
                        newNegocioList.add(negocio)
                    }
                }

                withContext(Dispatchers.Main) {
                    negocioList.clear()
                    negocioList.addAll(newNegocioList)
                    Log.e("tagNegocioList2", negocioList.toString())
                }
            } catch (exception: Exception) {
                Log.e("tag", "Excepción al obtener documentos: $exception")
            }
        }
    }
}
