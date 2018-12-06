package com.example.gaijin.countriestocities

import com.google.gson.Gson
import java.lang.Exception

class InfoParser() {

    public fun parseCountryInfo(countryString: String): CountryPOJO? {
        //Remove extra characters
        var cleanSample = countryString
        if (countryString[0] == ',') {
            cleanSample = countryString.substring(1)
        }
        try {
            // Normalize of object string
            var cityParameters: List<String> = cleanSample.split(":")
            cleanSample = "{country_name:${cityParameters[0]},cities:${cityParameters[1]}}"
            // Convert string to CountryPOJO object
            var gson: Gson = Gson()
            var country: CountryPOJO = gson.fromJson(cleanSample, CountryPOJO::class.java)
//            System.out.println(country.toString());
            return country
        } catch (ex: Exception) {
            ex.stackTrace
        }

        return null
    }
}