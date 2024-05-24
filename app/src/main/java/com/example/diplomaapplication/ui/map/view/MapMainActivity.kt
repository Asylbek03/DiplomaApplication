package com.example.diplomaapplication.ui.map.view

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.SeekBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.example.diplomaapplication.R
import com.example.diplomaapplication.ui.map.util.WorshipType
import com.example.diplomaapplication.ui.map.view.custom.PermissionDeniedView
import com.example.diplomaapplication.ui.map.viewmodel.MapViewModel

import com.google.android.material.slider.Slider
import java.util.*

class MapMainActivity : Fragment(), PopupMenu.OnMenuItemClickListener {

    private val mapViewModel: MapViewModel by viewModels()

    private val PERMISSION_REQUEST_CODE: Int = 555

    private var worshipType: WorshipType = WorshipType.ALL

    private val mapFragment = MapFragment()

    private lateinit var permissionDeniedOverlay: PermissionDeniedView
    private lateinit var detailFragmentView: View
    private lateinit var radiusSeekBar: SeekBar
    private lateinit var worshipTypeButton: ImageButton
    private lateinit var searchButton: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.activity_map_main, container, false)

        permissionDeniedOverlay = view.findViewById(R.id.permission_denied_view)
        detailFragmentView = view.findViewById(R.id.detail_fragment_container)
        radiusSeekBar = view.findViewById(R.id.search_radius_seekbar)
        worshipTypeButton = view.findViewById(R.id.place_type_menu_button)
        searchButton = view.findViewById(R.id.search_button)

        radiusSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val radiusInMeters = progress * 100

                val labelText = "${radiusInMeters / 1000f} km"
                seekBar?.contentDescription = labelText
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        worshipTypeButton.setImageResource(R.drawable.ic_add)

        worshipTypeButton.setOnClickListener {
            val popupMenu = PopupMenu(requireContext(), worshipTypeButton)
            popupMenu.menuInflater.inflate(R.menu.worship_selector_menu, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener(this)
            popupMenu.show()
        }
        mapViewModel.curLocation.observe(viewLifecycleOwner) { location ->
            if (location != null) {
                Log.d("TAG_X", "Current location observed -> LAT: ${location.latitude}, LONG: ${location.longitude}")
                searchButton.isEnabled = true
            }
        }

        searchButton.setOnClickListener {
            mapViewModel.curLocation.value?.let {
                Log.d("TAG_X", "Search button clicked with location: ${it.latitude}, ${it.longitude}")
                mapViewModel.updateDetailsEnabled(false)
                mapViewModel.getNearbyPlaces(
                    radiusSeekBar.progress.toString().trim(),
                    worshipType.toString().toLowerCase(Locale.ROOT)
                )
            } ?: Log.e("TAG_X", "Current location is not initialized on search button click")
        }

        mapViewModel.shouldDisplayDetails.observe(viewLifecycleOwner) { enabled ->
            if (enabled)
                detailFragmentView.visibility = View.VISIBLE
            else
                detailFragmentView.visibility = View.GONE
        }

        return view
    }

    override fun onStart() {
        super.onStart()

        if (hasLocationPermission())
            onLocationPermissionsGranted()
        else
            requestPermissionLauncher.launch(android.Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private fun loadMapFragment() {
        childFragmentManager.beginTransaction()
            .add(R.id.main_fragment_containerView, mapFragment)
            .commit()
    }

    private fun hasLocationPermission(): Boolean =
        ActivityCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermissions() =
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_REQUEST_CODE
        )

    private fun onLocationPermissionsGranted() {
        permissionDeniedOverlay.visibility = View.GONE
        loadMapFragment()
    }

    private fun onLocationPermissionsDenied() {
        permissionDeniedOverlay.message =
            resources.getString(R.string.permission_denied_text, resources.getString(R.string.location))
        permissionDeniedOverlay.visibility = View.VISIBLE
    }
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                onLocationPermissionsGranted()
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        requireActivity(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                    )
                )
                    requestLocationPermissions()
                else
                    onLocationPermissionsDenied()
            }
        }


    override fun onMenuItemClick(item: MenuItem): Boolean {
        worshipType = when (item.title.toString().toUpperCase(Locale.ROOT)) {
            WorshipType.MOSQUE.toString() -> WorshipType.MOSQUE
            WorshipType.OTHERS.toString() -> WorshipType.OTHERS
            WorshipType.PHARMACY.toString() -> WorshipType.PHARMACY
            WorshipType.HOSPITAL.toString() -> WorshipType.HOSPITAL

            else -> WorshipType.ALL
        }

        worshipTypeButton.setImageResource(
            when (worshipType) {
                WorshipType.MOSQUE -> R.drawable.ic_islam
                WorshipType.PHARMACY -> R.drawable.icon_pharmacy
                WorshipType.HOSPITAL -> R.drawable.icon_hospital

                WorshipType.OTHERS -> R.drawable.worship_general_71

                else -> R.drawable.ic_add
            }
        )

        return true
    }
}