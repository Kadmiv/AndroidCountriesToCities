package com.example.gaijin.countriestocities.dataclasses;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import io.realm.annotations.PrimaryKey;

public class CountryPOJO {

    @SerializedName("country_name")
    @Expose
    String countryName;
    @SerializedName("country_code")
    @Expose
    String countryCode;
    @SerializedName("flag")
    @Expose
    String flag;
    @SerializedName("cities")
    @Expose
    List<String> cities = new ArrayList<String>();

    public CountryPOJO() {
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String country) {
        this.countryName = country;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<String> getCities() {
        return cities;
    }

    public void setCities(List<String> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return String.format("Country: %s\nAlpha-code: %s\nFlag: %s\nCities: %s\n",
                countryName,
                countryCode,
                flag,
                cities.toString());
    }
}
