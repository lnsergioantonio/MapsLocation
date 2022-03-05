package com.example.mapslocation.domain

import com.example.mapslocation.domain.base.State
import com.example.mapslocation.domain.base.UseCase
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow

class FetchDirectionsUseCase(private val repository: DirectionsRepository):
    UseCase<List<LatLng>, FetchDirectionsUseCase.Params>() {

    override fun run(params: Params): Flow<State<List<LatLng>>> {
        return repository.fetchDirections(params.origin,params.destination)
    }

    data class Params(
        val origin:String,
        val destination:String
    )
}
