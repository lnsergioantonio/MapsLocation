package com.example.mapslocation.fragments

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.mapslocation.databinding.ItemPlaceBinding
import com.example.mapslocation.utils.ItemPlace

class PlaceAdapter(private var places : ArrayList<ItemPlace>, private val itemSelected:(ItemPlace)->Unit) : RecyclerView.Adapter<PlaceViewHolder>(){
    fun updatePlaces(places : ArrayList<ItemPlace>) {
        this.places = places
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val binding = ItemPlaceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlaceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.bind(places[position], itemSelected)
    }

    override fun getItemCount(): Int = places.size
}

class PlaceViewHolder(private val binding: ItemPlaceBinding):RecyclerView.ViewHolder(binding.root){
    fun bind(itemPlace: ItemPlace, itemSelected: (ItemPlace) -> Unit) {
        with(binding){
            labelPlace.text = itemPlace.street
            labelAddress.text = itemPlace.completeAddress.trim()

            itemView.setOnClickListener {
                itemSelected.invoke(itemPlace)
            }
        }
    }
}