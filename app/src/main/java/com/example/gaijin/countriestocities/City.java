package com.example.gaijin.countriestocities;

import io.realm.RealmObject;

public class City extends RealmObject {

    String cityName;
//    String country;

    public City() {
    }

    public City(String cityName) {//, String country) {
        this.cityName = cityName;
//        this.country = country;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
