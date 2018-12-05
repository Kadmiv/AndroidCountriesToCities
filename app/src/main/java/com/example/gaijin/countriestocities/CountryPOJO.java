package com.example.gaijin.countriestocities;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryPOJO {

    @SerializedName("country_name")
    @Expose
    private String countryName;
    @SerializedName("cities")
    @Expose
    private List<String> cities = new ArrayList<String>();

    public String getCountryName(){
        return countryName;
    }

    public void setCountryName(String country) {
        this.countryName = country;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return String.format("Country : %s\nCities : %s", countryName, cities.toString());
    }
}
