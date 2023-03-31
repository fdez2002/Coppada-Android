package com.fdez.projecttfg
data class BusinessSearchResponse(
    val total: Int,
    val businesses: List<Negocio>
)

data class Negocio(
    val name: String,
    /*
    val rating: Double,
    val phone: String,
    val address: String,
    val city: String,
    val state: String,
    val zipCode: String

     */
)
