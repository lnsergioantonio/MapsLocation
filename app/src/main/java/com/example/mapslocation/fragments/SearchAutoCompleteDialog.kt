package com.example.mapslocation.fragments

import android.app.Dialog
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.appcompat.widget.ListPopupWindow
import androidx.fragment.app.DialogFragment
import com.example.mapslocation.R
import com.example.mapslocation.databinding.DialogSearchAutocompleteBinding
import com.example.mapslocation.utils.ItemPlace
import com.example.mapslocation.utils.ext.hideSoftKeyboard
import com.example.mapslocation.utils.ext.onTextChanged
import com.example.mapslocation.utils.ext.showSoftKeyboard
import com.example.mapslocation.utils.PlacesAutoCompleteManager
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

class SearchAutoCompleteDialog : DialogFragment() {
    private var itemSelected:(ItemPlace?, Boolean)->Unit = {_, _ ->  }
    private var latLngBounds: LatLngBounds? = null
    private var placesClient: PlacesClient? = null
    private lateinit var  binding : DialogSearchAutocompleteBinding
    private lateinit var adapter:PlaceAdapter

    companion object {
        @JvmStatic
        fun newInstance(bounds: LatLngBounds?): SearchAutoCompleteDialog {
            val fragment = SearchAutoCompleteDialog()
            val args = Bundle()
            //args.putParcelable("bounds", bounds)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.CustomDialogTransparent)
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val lp = WindowManager.LayoutParams()
            lp.copyFrom(getDialog()?.window?.attributes)
            lp.width = ListPopupWindow.MATCH_PARENT
            lp.height = ListPopupWindow.MATCH_PARENT
            getDialog()?.window?.attributes = lp
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogSearchAutocompleteBinding.inflate(inflater, container, false)

        dialog?.window?.setBackgroundDrawable(null)
        if (arguments != null) {
            if (requireArguments().getParcelable<Parcelable?>("bounds") != null) {
                latLngBounds = requireArguments().getParcelable("bounds")
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isCancelable = true
        placesClient = Places.createClient(requireActivity())
        initListeners()
    }

    private fun initListeners() {
        with(binding){
            searchTextInputEditText.requestFocus()
            requireActivity().showSoftKeyboard()

            closeSearchButton.setOnClickListener {
                searchTextInputEditText.setText("")
                searchResultsList.visibility = View.GONE
            }

            backButton.setOnClickListener {
                requireActivity().hideSoftKeyboard()
                dismiss()
            }

            searchTextInputEditText.onTextChanged { searchText ->
                placesClient?.let { client ->
                    PlacesAutoCompleteManager.customAutoComplete(searchText,null, client){
                        adapter.updatePlaces(it)
                        searchResultsList.visibility = View.VISIBLE
                    }
                }
            }

            adapter = PlaceAdapter(arrayListOf()){ destinationItem ->
                placesClient?.let { noNullClient->
                    PlacesAutoCompleteManager.getLatLong(noNullClient,destinationItem.placeId){ destinationLatLng ->
                        destinationItem.setLatLng(destinationLatLng)
                        itemSelected.invoke(destinationItem, false)
                        dismiss()
                    }
                }
            }

            searchResultsList.adapter = adapter
            searchResultsList.visibility = View.GONE
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        return dialog
    }

    fun onSelectedItem(itemSelected:(ItemPlace?,Boolean)->Unit){
        this.itemSelected = itemSelected
    }

}