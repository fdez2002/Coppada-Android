package com.fdez.projecttfg.widgetFavoritos

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.bumptech.glide.Glide
import com.fdez.projecttfg.MainActivity
import com.fdez.projecttfg.api.YelpApi
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.R
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
        val remoteViews = RemoteViews(context.packageName, R.layout.widget_item_layout)
        remoteViews.setTextViewText(R.id.widget_item_name, negocioList[position].name)

        // Cargar la imagen utilizando Glide
        val bitmap = getBitmapFromUrl(negocioList[position].image_url)

        // Establecer la imagen en el ImageView utilizando setImageViewBitmap()
        remoteViews.setImageViewBitmap(R.id.widget_item_image, bitmap)

        return remoteViews
    }
    private fun getBitmapFromUrl(imageUrl: String): Bitmap? {
        try {
            // Utilizar Glide para cargar la imagen en un formato compatible
            return Glide.with(context.applicationContext)
                .asBitmap()
                .load(imageUrl)
                .submit()
                .get()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
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
