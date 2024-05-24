package com.example.diplomaapplication.ui.map.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.diplomaapplication.R
import com.example.diplomaapplication.ui.map.model.NearbyPlaces
import com.example.diplomaapplication.ui.map.util.NetworkConstants
import com.example.diplomaapplication.ui.map.util.WorshipType
import com.example.diplomaapplication.ui.map.viewmodel.MapViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import java.util.Locale

class MapFragment : Fragment(), LocationListener, OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveListener {

    private val mapViewModel: MapViewModel by activityViewModels()
    private lateinit var locationManager: LocationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var gmap: GoogleMap
    private val markers: MutableList<Marker> = mutableListOf()
    private lateinit var selectedMarker: Marker
    private var curZoom: Float = 13f

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.map_fragment_layout, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationManager = view.context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        mapViewModel.nearbyPlaceResults.observe(viewLifecycleOwner) {
            if (this::gmap.isInitialized)
                it.searchResults?.let { results ->
                    Log.d("TAG_X", "Result received")
                    placeNearbyMarkers(results)
                }
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationPolling(enabled: Boolean) {
        if (enabled)
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000L, 100f, this)
        else
            locationManager.removeUpdates(this)
    }

    override fun onLocationChanged(location: Location) {
        Log.d("TAG_X", "current location -> LAT: ${location.latitude}, LONG: ${location.longitude}")
        val newLocation = LatLng(location.latitude, location.longitude)

        mapViewModel.updateLocation(newLocation)
        if (this::gmap.isInitialized) {
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, curZoom))
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        googleMap.let {
            gmap = it
            customizeMap()
            enableLocationPolling(true)
        }
    }

    companion object {
        private const val DEFAULT_ZOOM = 15
    }

    override fun onStop() {
        super.onStop()
        enableLocationPolling(false)
    }

    @SuppressLint("MissingPermission")
    private fun customizeMap() {
        gmap.setMaxZoomPreference(17f)
        gmap.setMinZoomPreference(7f)
        gmap.uiSettings.isCompassEnabled = true
        gmap.isMyLocationEnabled = true
        gmap.uiSettings.isMyLocationButtonEnabled = true
        gmap.setOnMarkerClickListener(this)
        gmap.setOnCameraMoveListener(this)
    }

    private fun placeNearbyMarkers(placesList: List<NearbyPlaces.SearchResult>) {
        removeAllMarkers()

        if (placesList.isEmpty()) {
            Log.d("TAG_X", "Список близлежащих мест пуст.")
            return
        }

        for (place in placesList) {
            val lat = place.geometry?.location?.lat ?: 0.0
            val lng = place.geometry?.location?.lng ?: 0.0

            val mark = gmap.addMarker(
                MarkerOptions()
                    .position(LatLng(lat, lng))
                    .title(place.name)
            )

            var iconId: Int = R.drawable.worship_general_71

            place.types?.let { type ->
                iconId = when {
                    type.contains(WorshipType.MOSQUE.toString().toLowerCase(Locale.ROOT)) -> R.drawable.islam
                    type.contains(WorshipType.PHARMACY.toString().toLowerCase(Locale.ROOT)) -> R.drawable.icon_hospital
                    type.contains(WorshipType.HOSPITAL.toString().toLowerCase(Locale.ROOT)) -> R.drawable.icon_pharmacy
                    else -> R.drawable.worship_general_71
                }
            }

            mark?.setIcon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(iconId, 100, 100)))
            mark?.tag = place.place_id + ":" + iconId
            markers.add(mark!!)
        }
    }

    fun resizeBitmap(iconId: Int, width: Int, height: Int): Bitmap {
        val imageBitmap = BitmapFactory.decodeResource(resources, iconId)
        return Bitmap.createScaledBitmap(imageBitmap, width, height, false)
    }

    private fun removeAllMarkers() {
        for (m in markers)
            m.remove()
        markers.clear()
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        if (this::selectedMarker.isInitialized)
            selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                resizeBitmap(mapViewModel.selectedPlaceIconId, 100, 100)))

        selectedMarker = p0

        val tagData = p0.tag.toString().split(":")

        val queryMap = mapOf(
            NetworkConstants.KEY_KEY to NetworkConstants.KEY_VALUE,
            NetworkConstants.PLACE_ID_KEY to tagData[0]
        )

        p0.title?.let { mapViewModel.selectedPlaceName = it }
        mapViewModel.selectedPlaceId = tagData[0]
        mapViewModel.selectedPlaceIconId = tagData[1].toInt()
        mapViewModel.requestGeocodeData(queryMap)

        selectedMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
            resizeBitmap(mapViewModel.selectedPlaceIconId, 175, 175)))

        return false
    }

    override fun onCameraMove() {
        curZoom = gmap.cameraPosition.zoom
    }
}
