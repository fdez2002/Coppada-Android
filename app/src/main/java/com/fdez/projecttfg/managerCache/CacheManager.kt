package com.fdez.projecttfg.managerCache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheManager(private val context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE)

    /**
     * Se utiliza para guardar datos en el almacenamiento compartido (SharedPreferences) utilizando una clave (key) y los datos a guardar.
     */
    fun <T> saveData(key: String, data: T) {
        val json = Gson().toJson(data)
        sharedPreferences.edit {
            putString(key, json)
            apply()
        }
    }

    /**
     * Utiliza para cargar datos del almacenamiento compartido (SharedPreferences)
     * utilizando una clave (key) y devolver los datos deserializados en el tipo especificado.
     */
    inline fun <reified T> loadData(key: String): T? {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)
        } else {
            null
        }
    }

    /**
     * Se utiliza para eliminar todos los datos almacenados
     */
    fun clearCache() {
        sharedPreferences.edit().clear().apply()
    }




}