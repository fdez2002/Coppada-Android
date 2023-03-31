package com.fdez.projecttfg

import android.content.ContentValues.TAG
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.fdez.projecttfg.databinding.ActivityMainBinding
import android.os.AsyncTask
import android.util.Log
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.bottomNavigation

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        navView.setupWithNavController(navController)
        YelpSearchTask().execute("pizza", "Linares")
    }

    private inner class YelpSearchTask : AsyncTask<String, Void, String>() {

        override fun doInBackground(vararg params: String): String? {
            val term = params[0]
            val location = params[1]

            return try {
                val api = YelpApi()
                api.search(term, location)
            } catch (e: IOException) {
                Log.e(TAG, "Error al buscar en Yelp", e)
                null
            }
        }


        override fun onPostExecute(result: String?) {
            if (result != null) {
                Log.i(TAG, result)
            }
        }
    }
}