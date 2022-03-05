package com.example.mapslocation.domain

import com.example.mapslocation.BuildConfig
import com.example.mapslocation.data.network.enties.RoutesResponse
import com.example.mapslocation.data.network.ApiInterface
import com.example.mapslocation.data.network.NetworkHandler
import com.example.mapslocation.domain.base.NetworkConnectionException
import com.example.mapslocation.domain.base.NoDataException
import com.example.mapslocation.domain.base.State
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onStart

interface DirectionsRepository {
    fun fetchDirections(origin: String, destination: String): Flow<State<List<LatLng>>>
}

class DirectionsRepositoryImpl(
    private val api: ApiInterface,
    private val networkHandler: NetworkHandler
) : DirectionsRepository {
    override fun fetchDirections(origin: String, destination: String): Flow<State<List<LatLng>>> {
        return flow {
            val result = when (networkHandler.isConnected) {
                true -> {
                    api.fetchDirection("driving", "less_driving", origin, destination, BuildConfig.MAPS_API_KEY).run {
                        if (isSuccessful && body() != null) {
                            State.Success(body()!!.toDomain())
                        } else {
                            State.Failure(NoDataException())
                        }
                    }
                }
                false -> {
                    State.Failure(NetworkConnectionException())
                }
            }
            emit(result)
        }.onStart {
            State.Progress(true)
        }.catch {
            State.Failure(it)
        }
    }
}