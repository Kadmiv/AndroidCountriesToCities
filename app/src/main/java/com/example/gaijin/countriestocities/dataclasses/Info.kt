package com.example.gaijin.countriestocities.dataclasses
import com.google.gson.annotations.SerializedName


data class Info(
        @SerializedName("geonames")
    var geonames: List<GeonamePart>,
        @SerializedName("totalResultsCount")
    var totalResultsCount: Int
)

data class Geoname(
    @SerializedName("adminCode1")
    var adminCode1: String,
    @SerializedName("adminCodes1")
    var adminCodes1: AdminCodes1,
    @SerializedName("adminName1")
    var adminName1: String,
    @SerializedName("countryCode")
    var countryCode: String,
    @SerializedName("countryId")
    var countryId: String,
    @SerializedName("countryName")
    var countryName: String,
    @SerializedName("fcl")
    var fcl: String,
    @SerializedName("fclName")
    var fclName: String,
    @SerializedName("fcode")
    var fcode: String,
    @SerializedName("fcodeName")
    var fcodeName: String,
    @SerializedName("geonameId")
    var geonameId: Int,
    @SerializedName("lat")
    var lat: String,
    @SerializedName("lng")
    var lng: String,
    @SerializedName("title")
    var name: String,
    @SerializedName("population")
    var population: Int,
    @SerializedName("toponymName")
    var toponymName: String
)

data class AdminCodes1(
    @SerializedName("ISO3166_2")
    var iSO31662: String
)