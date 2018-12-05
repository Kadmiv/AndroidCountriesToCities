package com.example.gaijin.countriestocities

import io.realm.RealmObject

data class City(var name: String, var country: String) : RealmObject() {
}