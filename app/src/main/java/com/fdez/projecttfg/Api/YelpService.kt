package com.fdez.projecttfg.Api

import com.fdez.projecttfg.BusinessSearchResponse
import com.fdez.projecttfg.DetailBusiness
import com.fdez.projecttfg.Reviews
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface YelpService {
    @GET("businesses/search")
    suspend fun searchBusinesses(
        @Query("term") term: String,
        @Query("location") location: String
    ): BusinessSearchResponse

    @GET("businesses/{alias}")
    suspend fun getNegocioDetalle(@Path("alias") alias: String): DetailBusiness

    @GET("businesses/{alias}/reviews")
    suspend fun getNegocioReviews(@Path("alias") alias: String): Reviews


}