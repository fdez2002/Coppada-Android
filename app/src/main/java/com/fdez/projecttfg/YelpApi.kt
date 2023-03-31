package com.fdez.projecttfg

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class YelpApi {
    companion object {
        private const val BASE_URL = "https://api.yelp.com/v3"
        private const val API_KEY = "PDxjKdfN4Cz4L84q_caBdnfl5HbJQFuSCCubpJOl45SKNEhkATASxW9hNp32P5FLCKKIAqqphxCHd_wggyA5jYfOcSTLcVlN48ju0moM_riS1W7JIpAbw9JYV5kcZHYx"
    }

    private val client = OkHttpClient()

    @Throws(IOException::class)
    fun search(term: String, location: String): String? {
        val request: Request = Request.Builder()
            .url("$BASE_URL/businesses/search?term=$term&location=$location")
            .header("Authorization", "Bearer $API_KEY")
            .build()
        val response: Response = client.newCall(request).execute()
        return response.body?.string()
    }
}