package com.example.diplomaapplication.ui.map.model

import com.google.gson.annotations.SerializedName

data class NearbyPlaces(
    val html_attributions: List<Any>?,
    val next_page_token: String?,
    @SerializedName("results")
    val searchResults: List<SearchResult>?,
    val status: String?,
    val error: String?
) {
    data class SearchResult(
        val business_status: String?,
        val geometry: Geometry?,
        val icon: String?,
        val name: String?,
        val opening_hours: OpeningHours?,
        val permanently_closed: Boolean?,
        val photos: List<Photo>?,
        val place_id: String?,
        val plus_code: PlusCode?,
        val price_level: Int?,
        val rating: Double?,
        val reference: String?,
        val scope: String?,
        val types: List<String>?,
        val user_ratings_total: Int?,
        val vicinity: String?
    ) {
        data class Geometry(
            val location: Location?,
            val viewport: Viewport?
        ) {
            data class Location(
                val lat: Double?,
                val lng: Double?
            )

            data class Viewport(
                val northeast: Northeast?,
                val southwest: Southwest?
            ) {
                data class Northeast(
                    val lat: Double?,
                    val lng: Double?
                )

                data class Southwest(
                    val lat: Double?,
                    val lng: Double?
                )
            }
        }

        data class OpeningHours(
            val open_now: Boolean?
        )

        data class Photo(
            val height: Int?,
            val html_attributions: List<String>?,
            val photo_reference: String?,
            val width: Int?
        )

        data class PlusCode(
            val compound_code: String?,
            val global_code: String?
        )
    }
}