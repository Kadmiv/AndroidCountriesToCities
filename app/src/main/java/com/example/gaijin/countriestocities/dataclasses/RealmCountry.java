package com.example.gaijin.countriestocities.dataclasses;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class RealmCountry extends RealmObject {

    @PrimaryKey
    private String countryName;
    private String alphaCode;
    private RealmList<RealmCity> cities;

    public RealmCountry() {
    }

    public RealmCountry(String countryName, RealmList<RealmCity> cities) {
        this.countryName = countryName;
        this.cities = cities;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public RealmList<RealmCity> getCities() {
        return cities;
    }

    public void setCities(RealmList<RealmCity> cities) {
        this.cities = cities;
    }

    public String getAlphaCode() {
        return alphaCode;
    }

    public void setAlphaCode(String alphaCode) {
        this.alphaCode = alphaCode;
    }
}