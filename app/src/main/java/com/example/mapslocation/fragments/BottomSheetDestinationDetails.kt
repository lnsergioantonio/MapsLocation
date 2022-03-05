package com.example.mapslocation.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.mapslocation.R
import com.example.mapslocation.databinding.BottomSheetDestinationDetailsBinding
import com.example.mapslocation.utils.ItemPlace
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetDestinationDetails(
    private val itemPlace: ItemPlace
): BottomSheetDialogFragment() {
    private lateinit var binding:BottomSheetDestinationDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = BottomSheetDestinationDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initListeners()
    }

    private fun initListeners() {
        binding.labelLocation.text = "${itemPlace.latitude}, ${itemPlace.longitude}"
        binding.labelDestinationAddress.text = itemPlace.completeAddress
    }
}