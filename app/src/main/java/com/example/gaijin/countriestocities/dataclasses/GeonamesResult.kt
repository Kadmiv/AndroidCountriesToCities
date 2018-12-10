package com.example.gaijin.countriestocities.dataclasses
import com.google.gson.annotations.SerializedName
data class GeonamesResult(
    @SerializedName("geonames")
    var geonames: List<GeonamePart>
)
