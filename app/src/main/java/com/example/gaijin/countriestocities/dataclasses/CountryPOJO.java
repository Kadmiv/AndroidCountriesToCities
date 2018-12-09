package com.example.gaijin.countriestocities.dataclasses;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CountryPOJO {

    @SerializedName("country_name")
    @Expose
    String name;
    @SerializedName("country_code")
    @Expose
    String code;
    @SerializedName("flag")
    @Expose
    String flag;
    @SerializedName("cities")
    @Expose
    List<String> cities = new ArrayList<String>();

    public CountryPOJO() {
    }

    public String getName() {
        return name;
    }

    public void setName(String country) {
        this.name = country;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
                name,
                code,
                flag,
                cities.toString());
    }
}
