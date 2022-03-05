package com.example.mapslocation.data.network.enties

import com.google.gson.annotations.SerializedName

data class RoutesResponse(
    @SerializedName("routes") var routes: ArrayList<Routes>?,
    @SerializedName("status") var status: String?
)

data class Routes(
    @SerializedName("overview_polyline") var overviewPolyline: OverviewPolyline?,
)

data class OverviewPolyline(
    @SerializedName("points") var points: String?
)