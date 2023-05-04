package com.fdez.projecttfg.Api

import android.location.Geocoder
import com.fdez.projecttfg.DetailBusiness
import com.fdez.projecttfg.MyApp
import com.fdez.projecttfg.Negocio
import com.google.gson.Gson
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.*

class YelpApi {
    companion object {


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
    suspend fun getAlias(alias: String): Negocio? {
        val response = service.getNegocioDetalle(alias)
        return Negocio( response.image_url, response.is_closed, response.name,
            response.coordinates, response.rating, response.review_count, response.alias)
    }
    suspend fun getBusinessDetails(alias: String): DetailBusiness? {
        val response = service.getNegocioDetalle(alias)
        return DetailBusiness(response.id, response.name, response.rating, response.review_count, response.location, response.phone
            , response.photos, response.coordinates, response.is_closed, response.price, response.url, response.image_url, response.alias)
    }


/*
    // Nuevo método para obtener la longitud y la latitud de un negocio
    suspend fun getBusinessLocation(alias: String): Pair<Double, Double>? {
        val businessDetails = getBusinessDetails(alias)
        val address = businessDetails?.location?.address1 ?: return null
        val geocoder = Geocoder(MyApp.instance, Locale.getDefault())
        try {
            val results = geocoder.getFromLocationName(address, 5)
            if (results != null) {
                if (results.isNotEmpty()) {
                    val latitude = results[0].latitude
                    val longitude = results[0].longitude
                    return Pair(latitude, longitude)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

 */






}