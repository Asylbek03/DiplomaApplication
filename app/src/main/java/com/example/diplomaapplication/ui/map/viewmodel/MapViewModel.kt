package com.example.diplomaapplication.ui.map.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

import com.example.diplomaapplication.R
import com.example.diplomaapplication.ui.map.model.Geocode
import com.example.diplomaapplication.ui.map.model.NearbyPlaces
import com.example.diplomaapplication.ui.map.model.PlaceDetails
import com.example.diplomaapplication.ui.map.network.GooglePlacesRetrofit
import com.example.diplomaapplication.ui.map.util.NetworkConstants
import com.example.diplomaapplication.ui.map.util.SearchConstants
import com.example.diplomaapplication.ui.map.util.WorshipType
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.Locale

class MapViewModel : ViewModel() {
    private val compositeDisposable = CompositeDisposable()

    val nearbyPlaceResults: MutableLiveData<NearbyPlaces> = MutableLiveData()
    val geocodeResults: MutableLiveData<Geocode> = MutableLiveData()
    val placeDetailsResults: MutableLiveData<PlaceDetails> = MutableLiveData()

    private val _curLocation: MutableLiveData<LatLng> = MutableLiveData()
    val curLocation: LiveData<LatLng>
        get() = _curLocation

    val shouldDisplayDetails: MutableLiveData<Boolean> = MutableLiveData(false)
    var selectedPlaceId: String = ""
    var selectedPlaceName: String = ""
    var selectedPlaceIconId: Int = R.drawable.worship_general_71

    fun requestNearbyPlaces(queryMap: Map<String, String>) {
        compositeDisposable.add(
            GooglePlacesRetrofit.searchNearbyPlaces(queryMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    if (response.status == "OK") {
                        nearbyPlaceResults.postValue(response)
                    } else {
                        Log.e("TAG_X", "Ошибка запроса близлежащих мест. Статус: ${response.status}")
                    }
                }, { throwable ->
                    Log.e("TAG_X", "Ошибка запроса близлежащих мест: ${throwable.localizedMessage}")
                })
        )
    }

    fun requestGeocodeData(queryMap: Map<String, String>) {
        compositeDisposable.add(
            GooglePlacesRetrofit.getGeocodeData(queryMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.status == "OK") {
                        geocodeResults.postValue(it)
                    } else {
                        Log.e("TAG_X", "geocode request status -> ${it.status}")
                        Log.e("TAG_X", "geocode request error -> ${it.error}")
                    }
                    compositeDisposable.clear()
                }, {
                    Log.e("TAG_X", "geocode exception -> ${it.localizedMessage}")
                })
        )
    }

    fun requestPlaceDetails(queryMap: Map<String, String>) {
        compositeDisposable.add(
            GooglePlacesRetrofit.getPlaceDetails(queryMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    if (it.status == "OK") {
                        placeDetailsResults.postValue(it)
                    } else {
                        Log.e("TAG_X", "place details request status -> ${it.status}")
                        Log.e("TAG_X", "place details request error -> ${it.error}")
                    }
                    compositeDisposable.clear()
                }, {
                    Log.e("TAG_X", "place details exception -> ${it.localizedMessage}")
                })
        )
    }

    fun updateLocation(location: LatLng) {
        _curLocation.postValue(location)
        Log.d("TAG_X", "Updated location -> LAT: ${location.latitude}, LONG: ${location.longitude}")
    }

    fun getNearbyPlaces(radius: String, placeType: String = "") {
        val currentLocation = _curLocation.value

        if (currentLocation == null) {
            Log.e("TAG_X", "Current location is not initialized in getNearbyPlaces")
            return
        }

        var type = placeType
        var keywords = ""

        val nearbyPlacesQueryMap: MutableMap<String, String> = mutableMapOf(
            "location" to "${currentLocation.latitude},${currentLocation.longitude}",
            "radius" to radius,
            "key" to NetworkConstants.KEY_VALUE
        )
        if (placeType.isNotEmpty())
            nearbyPlacesQueryMap["type"] = placeType

        requestNearbyPlaces(nearbyPlacesQueryMap)

        when (placeType.toLowerCase(Locale.ROOT)) {
            WorshipType.OTHERS.toString().toLowerCase(Locale.ROOT) -> {
                type = ""
                keywords = SearchConstants.OTHER_SEARCH_KEYWORDS
            }
            WorshipType.ALL.toString().toLowerCase(Locale.ROOT) -> {
                type = ""
                keywords = SearchConstants.ALL_SEARCH_PREFIX + "|" + SearchConstants.OTHER_SEARCH_KEYWORDS
            }
        }

        val queryMap = mapOf(
            NetworkConstants.KEY_KEY to NetworkConstants.KEY_VALUE,
            NetworkConstants.LOCATION_KEY to "${currentLocation.latitude},${currentLocation.longitude}",
            NetworkConstants.RADIUS_KEY to radius,
            NetworkConstants.TYPE_KEY to type,
            NetworkConstants.KEYWORD_KEY to keywords
        )

        requestNearbyPlaces(queryMap)
        Log.d("TAG_X", "Query map: $queryMap")
    }

    fun updateDetailsEnabled(enabled: Boolean) {
        shouldDisplayDetails.postValue(enabled)
    }
}
