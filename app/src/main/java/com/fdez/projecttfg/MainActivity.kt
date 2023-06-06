package com.fdez.projecttfg

import android.content.pm.PackageManager
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupWithNavController
import com.fdez.projecttfg.databinding.ActivityMainBinding
import com.fdez.projecttfg.ui.favorite.FavoriteFragment
import com.google.firebase.FirebaseApp
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.Manifest.permission.ACCESS_COARSE_LOCATION


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        FirebaseApp.initializeApp(this)

        val navView: BottomNavigationView = binding.bottomNavigation

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_map, R.id.navigation_favorite, R.id.navigation_account
            )
        )
        navView.setupWithNavController(navController)

        // Verifica si la actividad se inició con la acción personalizada
        if (intent?.action == "com.fdez.projecttfg.ACTION_OPEN_FAVORITES" && intent?.getBooleanExtra("openFavoritesFragment", false) == true) {
            openFavoritesFragment()  // Llama a la función para abrir el fragmento de favoritos
        }
        // Verificar y solicitar permisos de ubicación
        checkLocationPermissions()
    }

    private fun checkLocationPermissions() {
        // Verificar si se han concedido los permisos de ubicación
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocationPermission || !hasCoarseLocationPermission) {
            // Si los permisos no se han concedido, solicitarlos al usuario
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    ACCESS_FINE_LOCATION,
                    ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSIONS
            )
        } else {
            // Si los permisos ya se han concedido, puedes realizar las operaciones que requieren permisos aquí
            // Llamar a la función que necesita permisos
            getCurrentLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_LOCATION_PERMISSIONS -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permiso concedido, realizar las operaciones que requieren permisos aquí
                    // Llamar a la función que necesita permisos
                    getCurrentLocation()
                } else {
                    // Permiso denegado, mostrar un mensaje o tomar alguna acción adicional si es necesario
                }
            }
        }
    }

    private fun getCurrentLocation() {
        // Aquí puedes implementar la lógica para obtener la ubicación actual
        // Puedes llamar a la función de la clase YelpApi o manejarla directamente aquí
    }
    fun openFavoritesFragment() {
        val fragment = FavoriteFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, fragment)
            .commit()
    }

}
