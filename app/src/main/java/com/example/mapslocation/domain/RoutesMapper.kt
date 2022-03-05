package com.example.mapslocation.domain

import com.example.mapslocation.data.network.enties.RoutesResponse
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil

fun RoutesResponse.toDomain():MutableList<LatLng>{
    val polylineList : MutableList<LatLng> = arrayListOf()
    routes?.forEach { route ->
        route.overviewPolyline?.points.let { pointString ->
            polylineList.addAll(PolyUtil.decode(pointString))
        }
    }
    return polylineList
}
