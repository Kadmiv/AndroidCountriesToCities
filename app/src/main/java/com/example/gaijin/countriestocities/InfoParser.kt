package com.example.gaijin.countriestocities

import com.example.gaijin.countriestocities.dataclasses.CountryInfo
import com.example.gaijin.countriestocities.dataclasses.CountryPOJO
import com.google.gson.Gson
import java.lang.Exception
import java.util.HashMap

class InfoParser() {

    public fun parseCountryInfo(countryString: String, loadAlphaCodes: HashMap<String, CountryInfo>): CountryPOJO? {
        //Remove extra characters
        var cleanSample = countryString
        if (countryString[0] == ',') {
            cleanSample = countryString.substring(1)
        }
        try {
            // Normalize of object string
            var cityParameters: List<String> = cleanSample.split(":")
            // Check empty tag name of country
            if (cityParameters[0] == "") {
                return null
            }
            //Get information for country
            var name = cityParameters[0]
            var cities = cityParameters[1]
            //Get and convert additional information for country
            var keyName = name.replace("\"", "").toLowerCase()
            var countryInfo = loadAlphaCodes[keyName]
            var code = "\"null\""
            var flag = "\"null\""
            if (countryInfo != null) {
                code = "\"${countryInfo!!.alpha2Code}\""
                flag = "\"${countryInfo!!.flag}\""
            }

            cleanSample = "{country_name:$name,country_code:$code,flag:$flag,cities:$cities}"
            // Convert string to CountryPOJO object
            var gson = Gson()
            var country: CountryPOJO = gson.fromJson(cleanSample, CountryPOJO::class.java)
//            System.out.println(country.toString());
            return country
        } catch (ex: Exception) {
            ex.stackTrace
        }

        return null
    }
}