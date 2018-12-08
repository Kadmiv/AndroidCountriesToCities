package com.example.gaijin.countriestocities.dataclasses;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CountryRealm extends RealmObject {

    @PrimaryKey
    private String countryName;
    private String alphaCode;
    private RealmList<CityRealm> cities;

    public CountryRealm() {
    }

    public CountryRealm(String countryName, RealmList<CityRealm> cities) {
        this.countryName = countryName;
        this.cities = cities;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public RealmList<CityRealm> getCities() {
        return cities;
    }

    public void setCities(RealmList<CityRealm> cities) {
        this.cities = cities;
    }

    public String getAlphaCode() {
        return alphaCode;
    }

    public void setAlphaCode(String alphaCode) {
        this.alphaCode = alphaCode;
    }
}
