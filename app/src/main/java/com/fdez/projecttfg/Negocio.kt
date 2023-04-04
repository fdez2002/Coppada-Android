package com.fdez.projecttfg
data class BusinessSearchResponse(
    val total: Int,
    val businesses: List<Negocio>
)

data class Negocio(
    val image_url: String,
    val is_closed: Boolean,
    val name: String,
    val location: Location?,
    val rating: Double,
    val review_count: Int,
    val alias: String



    /*
    val rating: Double,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String

     */
)
data class Location(
    val address1: String,
    val address2: String,
    val address3: String,
    val city: String,
    val zip_code: String,
    val country: String,
    val state: String,
    val display_address: List<String>
)
