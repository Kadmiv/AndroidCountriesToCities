package com.example.gaijin.countriestocities.dataclasses
import com.google.gson.annotations.SerializedName


data class GeonamesInfo(
    @SerializedName("geonames")
    var geonames: List<Geoname>
)

data class Geoname(
    @SerializedName("countryCode")
    var countryCode: String,
    @SerializedName("elevation")
    var elevation: Int,
    @SerializedName("feature")
    var feature: String,
    @SerializedName("geoNameId")
    var geoNameId: Int,
    @SerializedName("lang")
    var lang: String,
    @SerializedName("lat")
    var lat: Double,
    @SerializedName("lng")
    var lng: Double,
    @SerializedName("rank")
    var rank: Int,
    @SerializedName("summary")
    var summary: String,
    @SerializedName("thumbnailImg")
    var thumbnailImg: String,
    @SerializedName("title")
    var title: String,
    @SerializedName("wikipediaUrl")
    var wikipediaUrl: String
)