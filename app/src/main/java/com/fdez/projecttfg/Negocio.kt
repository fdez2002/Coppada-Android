package com.fdez.projecttfg

import com.denzcoskun.imageslider.models.SlideModel

data class BusinessSearchResponse(
    val total: Int,
    val businesses: List<Negocio>
)

data class Negocio(
    val image_url: String,
    val is_closed: Boolean,
    val name: String,
    val coordinates: Location?,
    val rating: Double,
    val review_count: Int,
    val alias: String

)
data class Location(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val zip_code: String,
    val country: String,
    val state: String,
    val latitude: Float,
    val longitude: Float,
    val display_address: List<String>
)
data class DetailBusiness(
    val id: String,
    val name: String,
    val rating: Double,
    val review_count: Int,
    val location: Location?,
    val phone: String?,
    val photos: List<String>,
    //val hours: List<Hour>?,
    //val categories: List<Category>,
    val price: String?,
    val url: String,
    val alias: String
)

