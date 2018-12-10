package com.example.gaijin.countriestocities.dataclasses

import com.google.gson.annotations.SerializedName


data class Country(

        @SerializedName("country_name")
        var name: String,

        @SerializedName("country_code")
        var code: String,

        @SerializedName("flag")
        var flage: String,

        @SerializedName("cities")
        var cities: List<String>
)