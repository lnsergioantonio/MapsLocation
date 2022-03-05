package com.example.mapslocation.utils

import com.google.android.gms.maps.model.LatLng

data class ItemPlace(
    val completeAddress: String,
    val street: String,
    val streetNeighborhood: String,
    val placeId: String,
    val distanceFromSearch: String,
    var longitude: Double? = null,
    var latitude: Double? = null
) {

    fun  hasLatLng(): Boolean {
        return longitude!=null && latitude!=null
    }

    fun getLatLng(): LatLng? =
        if (latitude != null && longitude != null)
            LatLng(latitude!!, longitude!!)
        else
            null

    fun setLatLng(latLng: LatLng) {
        latitude = latLng.latitude
        longitude = latLng.longitude
    }
}
