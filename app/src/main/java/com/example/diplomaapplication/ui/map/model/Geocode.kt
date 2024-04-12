package com.example.diplomaapplication.ui.map.model

import com.google.gson.annotations.SerializedName

data class Geocode(
    @SerializedName("results")
    val searchResults: List<SearchResult>?,
    val status: String?,
    val error: String?
) {
    data class SearchResult(
        val address_components: List<AddressComponent>?,
        val formatted_address: String?,
        val geometry: Geometry?,
        val place_id: String?,
        val plus_code: PlusCode?,
        val types: List<String>?
    ) {
        data class AddressComponent(
            val long_name: String?,
            val short_name: String?,
            val types: List<String>?
        )

        data class Geometry(
            val location: Location?,
            val location_type: String?,
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

        data class PlusCode(
            val compound_code: String?,
            val global_code: String?
        )
    }
}