package com.fdez.projecttfg.Api

import android.location.Geocoder
import com.fdez.projecttfg.Negocio
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
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
            Negocio(negocio.image_url, negocio.is_closed, negocio.name, negocio.location, negocio.rating, negocio.review_count, negocio.alias)
        }

    }



}