package com.example.gaijin.countriestocities;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Country extends RealmObject {

    @PrimaryKey
    private String countryName;
    private RealmList<City> cities;

    public Country() {
    }

    public Country(String countryName, RealmList<City> cities) {
        this.countryName = countryName;
        this.cities = cities;
    }

    public String getCountryName() {
        return countryName;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public RealmList<City> getCities() {
        return cities;
    }

    public void setCities(RealmList<City> cities) {
        this.cities = cities;
    }
}
