package com.example.gaijin.countriestocities.dataclasses;

import io.realm.RealmObject;

public class RealmCity extends RealmObject {

    String cityName;

    public RealmCity() {
    }

    public RealmCity(String cityName) {
        this.cityName = cityName;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}