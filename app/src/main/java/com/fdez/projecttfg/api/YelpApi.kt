package com.fdez.projecttfg.api

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.fdez.projecttfg.DetailBusiness
import com.fdez.projecttfg.Negocio
import com.fdez.projecttfg.Review
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class YelpApi {
    companion object {
        private const val REQUEST_LOCATION_PERMISSIONS = 1001

        private const val BASE_URL = "https://api.yelp.com/v3/"
        private const val API_KEY = "PDxjKdfN4Cz4L84q_caBdnfl5HbJQFuSCCubpJOl45SKNEhkATASxW9hNp32P5FLCKKIAqqphxCHd_wggyA5jYfOcSTLcVlN48ju0moM_riS1W7JIpAbw9JYV5kcZHYx"
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("Authorization", "Bearer $API_KEY")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val service = retrofit.create(YelpService::class.java)

    suspend fun search(term: String, location: String): List<Negocio> {
        val response = service.searchBusinesses(term, location)
        val sortedBusinesses = response.businesses.sortedByDescending { it.rating }
        return sortedBusinesses.map { negocio ->
            Negocio(negocio.image_url, negocio.is_closed, negocio.name, negocio.coordinates, negocio.rating, negocio.review_count, negocio.alias)
        }
    }

    suspend fun searchCiudad(term: String, location: String): List<Negocio> {
        val response = service.searchBusinesses(term, location)
        val sortedBusinesses = response.businesses.sortedByDescending { it.rating }
        return sortedBusinesses.map { negocio ->
            Negocio(
                negocio.image_url,
                negocio.is_closed,
                negocio.name,
                negocio.coordinates,
                negocio.rating,
                negocio.review_count,
                negocio.alias
            )
        }
    }

    suspend fun getAlias(alias: String): Negocio {
        val response = service.getNegocioDetalle(alias)
        return Negocio(
            response.image_url, response.is_closed, response.name,
            response.coordinates, response.rating, response.review_count, response.alias
        )
    }

    suspend fun getBusinessDetails(alias: String): DetailBusiness? {
        val response = service.getNegocioDetalle(alias)
        return DetailBusiness(
            response.id, response.name, response.rating, response.review_count, response.location, response.phone,
            response.photos, response.coordinates, response.is_closed, response.price, response.url, response.image_url, response.alias
        )
    }

    suspend fun getBusinessReviews(alias: String): List<Review> {
        val response = service.getNegocioReviews(alias)
        return response.reviews.map { reviews ->
            Review(reviews.url, reviews.text, reviews.user, reviews.rating)
        }
    }


    private suspend fun getCurrentLocation(context: Context): Location = suspendCoroutine { continuation ->
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Verificar permisos de ubicación
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocationPermission && hasCoarseLocationPermission) {
            // Permiso concedido, obtener la última ubicación conocida
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (lastKnownLocation != null) {
                continuation.resume(lastKnownLocation)
            } else {
                // No se puede obtener la ubicación actual, devolver una ubicación vacía
                continuation.resume(Location(""))
            }
        } else {
            // No se han concedido los permisos, solicitarlos al usuario
            if (context is Activity) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) || ActivityCompat.shouldShowRequestPermissionRationale(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    // Explicar al usuario por qué se necesitan los permisos (opcional)
                }

                ActivityCompat.requestPermissions(
                    context,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    REQUEST_LOCATION_PERMISSIONS
                )
            }
            // Devolver una ubicación vacía mientras se espera la respuesta de los permisos
            continuation.resume(Location(""))
        }
    }
}
