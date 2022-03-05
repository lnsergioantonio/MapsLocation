package com.example.mapslocation.utils

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import com.google.android.libraries.places.api.net.PlacesClient
import java.util.*

object PlacesAutoCompleteManager {
    fun customAutoComplete(
        queryText: String?,
        latLngBounds: LatLngBounds?,
        placesClient: PlacesClient,
        block:(ArrayList<ItemPlace>)->Unit
    ) {
        val token = AutocompleteSessionToken.newInstance()
//        val lngBounds = LatLngBounds.builder()
//            .include(
//                LatLng(
//                    latLngBounds.center.getLatitude(),
//                    latLngBounds.center.getLongitude()
//                )
//            ).build()
        val request =
            FindAutocompletePredictionsRequest.builder() // Call either setLocationBias() OR setLocationRestriction().
                //.setLocationBias(bounds)
                // .setCountries("MX","CO")
                //.setLocationRestriction(latLngBounds)
                .setCountry("mx")
                //.setOrigin(lngBounds.center)
                .setTypeFilter(TypeFilter.GEOCODE)
                .setSessionToken(token)
                .setQuery(queryText)
                .build()
        placesClient.findAutocompletePredictions(request)
            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
                val places = ArrayList<ItemPlace>()
                for (prediction in response.autocompletePredictions) {
                    val xs = prediction.placeTypes
                    var found = false
                    for (s in xs) {
                        if (!s.name.equals(
                                Place.Type.COUNTRY.toString(),
                                ignoreCase = true
                            ) && !found
                        ) {
                            found = true

                            val place = ItemPlace(
                                prediction.getFullText(null).toString(),
                                prediction.getPrimaryText(null).toString(),
                                prediction.getSecondaryText(null).toString(),
                                prediction.placeId,
                                "${prediction.distanceMeters}"
                            )
                            places.add(place)
                        }
                    }
                }
                block.invoke(places)
            }.addOnFailureListener {
            }
    }

    fun getLatLong(placesClient: PlacesClient, placeId:String, block:(LatLng)->Unit){
        val placeFields = listOf(*Place.Field.values())
        val request = FetchPlaceRequest.builder(placeId, placeFields).build()

        placesClient.fetchPlace(request).addOnSuccessListener { result ->
            result?.place?.latLng?.let {
               block.invoke(it)
            }
        }
    }
}