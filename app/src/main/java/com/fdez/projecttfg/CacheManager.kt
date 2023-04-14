package com.fdez.projecttfg

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CacheManager(private val context: Context) {

    val sharedPreferences: SharedPreferences = context.getSharedPreferences("cache", Context.MODE_PRIVATE)

    fun <T> saveData(key: String, data: T) {
        val json = Gson().toJson(data)
        sharedPreferences.edit {
            putString(key, json)
            apply()
        }
    }

    inline fun <reified T> loadData(key: String): T? {
        val json = sharedPreferences.getString(key, null)
        return if (json != null) {
            Gson().fromJson<T>(json, object : TypeToken<T>() {}.type)
        } else {
            null
        }
    }
}