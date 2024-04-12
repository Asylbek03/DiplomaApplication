package com.example.diplomaapplication.ui.map.util

class NetworkConstants {
    companion object {
        const val BASE_URL = "https://maps.googleapis.com/"

        const val KEY_KEY = "key"
        const val KEY_VALUE = "AIzaSyAEkbsprYryRT_Kpb3m0cdGada5kmR2m8o"

        const val LOCATION_KEY = "location"
        const val TYPE_KEY = "type"
        const val KEYWORD_KEY = "keyword"
        const val RADIUS_KEY = "radius"
        const val PLACE_ID_KEY = "place_id"
        const val PHOTO_REFERENCE_KEY = "photoreference"
        const val MAX_WIDTH_KEY = "maxwidth"
        const val MAX_HEIGHT_KEY = "maxheight"

        const val NEARBY_PLACES_REQUEST_PATH = "maps/api/place/nearbysearch/json"
        const val GEOCODE_REQUEST_PATH = "maps/api/geocode/json"
        const val PLACE_PHOTO_REQUEST_PATH = "maps/api/place/photo"
        const val PLACE_DETAILS_REQUEST_PATH = "maps/api/place/details/json"
    }
}