package com.example.gaijin.countriestocities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_info.*

class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        var cityName: String = intent.getStringExtra(Intent.EXTRA_TEXT)
        city_name.text = cityName
    }
}
