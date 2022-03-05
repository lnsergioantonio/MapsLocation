package com.example.mapslocation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapslocation.domain.FetchDirectionsUseCase
import com.example.mapslocation.domain.base.State
import com.google.android.gms.maps.model.LatLng

class MainViewModel(private val fetchDirectionsUseCase: FetchDirectionsUseCase):ViewModel() {

    private val routesLiveData = MutableLiveData<State<List<LatLng>>>()
    val routesState:LiveData<State<List<LatLng>>> get() = routesLiveData

    fun fetchDirections(origin:String,destination:String) {
        val params = FetchDirectionsUseCase.Params(origin, destination)
        fetchDirectionsUseCase.invoke(viewModelScope,params){
            routesLiveData.value = it
        }
    }
}