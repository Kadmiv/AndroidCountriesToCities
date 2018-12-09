package com.example.gaijin.countriestocities.dataclasses
import com.google.gson.annotations.SerializedName


data class CountryInfo(
    @SerializedName("alpha2Code")
    var alpha2Code: String,
    @SerializedName("flag")
    var flag: String
)