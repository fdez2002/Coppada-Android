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

        private const val BASE_URL = "https://api.yelp.com/v3/"//URL base de la api
        //Key de acceso
        private const val API_KEY = "PDxjKdfN4Cz4L84q_caBdnfl5HbJQFuSCCubpJOl45SKNEhkATASxW9hNp32P5FLCKKIAqqphxCHd_wggyA5jYfOcSTLcVlN48ju0moM_riS1W7JIpAbw9JYV5kcZHYx"
    }

    private var location: String = ""

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

    /**
     * Realiza una búsqueda de negocios utilizando un término de búsqueda y una ubicación
     * La palabra "suspend" se refiere a una función o una expresión que puede pausar su ejecución sin bloquear el hilo principal
     */
    suspend fun search(term: String, location: String): List<Negocio> {
        this.location = location
        val response = service.searchBusinesses(term, location)
        val sortedBusinesses = response.businesses.sortedByDescending { it.rating }
        return sortedBusinesses.map { negocio ->
            Negocio(negocio.image_url, negocio.is_closed, negocio.name, negocio.coordinates, negocio.rating, negocio.review_count, negocio.alias)
        }
    }

    /**
     * Obtiene los detalles de un negocio utilizando su alias.
     */
    suspend fun getAlias(alias: String): Negocio {
        val response = service.getNegocioDetalle(alias)
        return Negocio(
            response.image_url, response.is_closed, response.name,
            response.coordinates, response.rating, response.review_count, response.alias
        )
    }

    /**
     * Obtiene los detalles de un negocio utilizando su alias y devuelve un objeto de la clase "DetailBusiness" o
     * null si no se encontraron detalles para el negocio.
     */
    suspend fun getBusinessDetails(alias: String): DetailBusiness? {
        val response = service.getNegocioDetalle(alias)
        return DetailBusiness(
            response.id, response.name, response.rating, response.review_count, response.location, response.phone,
            response.photos, response.coordinates, response.is_closed, response.price, response.url, response.image_url, response.alias
        )
    }

    /**
     * Obtiene las reseñas de un negocio utilizando su alias y devuelve una lista de objetos de la clase "Review"
     */
    suspend fun getBusinessReviews(alias: String): List<Review> {
        val response = service.getNegocioReviews(alias)
        return response.reviews.map { reviews ->
            Review(reviews.url, reviews.text, reviews.user, reviews.rating)
        }
    }

}
