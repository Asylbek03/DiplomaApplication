package com.example.diplomaapplication.ui.map.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.diplomaapplication.R
import com.example.diplomaapplication.ui.map.util.NetworkConstants

import com.example.diplomaapplication.ui.map.view.adapter.PhotoRecyclerAdapter
import com.example.diplomaapplication.ui.map.viewmodel.MapViewModel
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class PlaceDetailFragment : Fragment() {

    // View Model
    private val mapViewModel: MapViewModel by activityViewModels()

    private lateinit var nameTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var worshipIconImageView: ImageView
    private lateinit var mapRouteImageView: ImageView

    private lateinit var photoRecyclerView: RecyclerView
    private val photoRecyclerAdapter = PhotoRecyclerAdapter(listOf())

    private lateinit var geoLocation: LatLng

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.place_detail_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        nameTextView = view.findViewById(R.id.place_name)
        addressTextView = view.findViewById(R.id.place_address)
        worshipIconImageView = view.findViewById(R.id.place_worship_icon)
        mapRouteImageView = view.findViewById(R.id.place_route_button)

        photoRecyclerView = view.findViewById(R.id.photo_recyclerView)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(photoRecyclerView)
        photoRecyclerView.adapter = photoRecyclerAdapter

        mapViewModel.geocodeResults.observe(viewLifecycleOwner) {
            it.searchResults?.get(0)?.let { result ->
                // set address in view
                result.formatted_address?.let { address ->
                    addressTextView.text = address
                }

                // set directions uri string
                result.geometry?.location?.let { loc ->
                    var lat = 0.0
                    var lng = 0.0
                    loc.lat?.let { lat = it }
                    loc.lng?.let { lng = it }
                    geoLocation = LatLng(lat, lng)
                }
            }

            // set title in view
            nameTextView.text = mapViewModel.selectedPlaceName

            // set icon in view
            worshipIconImageView.setImageResource(mapViewModel.selectedPlaceIconId)

            // enable detail view
            mapViewModel.updateDetailsEnabled(true)

            // get photos after momentary pause
            runBlocking {
                repeat(1) {
                    launch {
                        delay(500L)
                        getPlaceDetails(mapViewModel.selectedPlaceId)
                    }
                }
            }
        }

        mapViewModel.placeDetailsResults.observe(viewLifecycleOwner) {
            it.searchResult?.photos?.let { photos ->
                val idList: MutableList<String> = mutableListOf()
                for (p in photos)
                    p.photo_reference?.let { ref -> idList.add(ref) }

                Log.d("TAG_X", idList[0])
                photoRecyclerAdapter.updatePhotoList(idList)
            } ?: photoRecyclerAdapter.updatePhotoList(listOf("empty"))
        }

        mapRouteImageView.setOnClickListener{
            if (this::geoLocation.isInitialized) {
                val intentUri = Uri.parse("google.navigation:q=" +
                        geoLocation.latitude.toString() + "," + geoLocation.longitude.toString())

                val mapIntent = Intent(Intent.ACTION_VIEW, intentUri).also {
                    it.setPackage("com.google.android.apps.maps")
                }

                context?.packageManager?.let {
                    mapIntent.resolveActivity(it).also {
                        startActivity(mapIntent)
                    }
                }
            }
        }
    }

    private fun getPlaceDetails(placeID: String) {
        val queryMap = mapOf(
            NetworkConstants.KEY_KEY to NetworkConstants.KEY_VALUE,
            NetworkConstants.PLACE_ID_KEY to placeID
        )

        mapViewModel.requestPlaceDetails(queryMap)
    }
}