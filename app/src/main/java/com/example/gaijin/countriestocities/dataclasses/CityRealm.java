package com.example.gaijin.countriestocities.dataclasses;

import io.realm.RealmObject;

public class CityRealm extends RealmObject {

    String cityName;
//    String country;

    public CityRealm() {
    }

    public CityRealm(String cityName) {//, String country) {
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
