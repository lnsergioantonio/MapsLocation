package com.example.mapslocation.data.network

import com.example.mapslocation.data.network.enties.RoutesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("maps/api/directions/json")
    suspend fun fetchDirection(
        @Query("mode") mode:String,
        @Query("transit_routung_preference") preference:String,
        @Query("origin") origin:String,
        @Query("destination") destination:String,
        @Query("key") key:String
    ):Response<RoutesResponse>
}