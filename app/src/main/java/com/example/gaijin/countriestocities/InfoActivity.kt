package com.example.gaijin.countriestocities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gaijin.countriestocities.dataclasses.GeonamePart
import com.example.gaijin.countriestocities.dataclasses.GeonamesResult
import com.example.gaijin.countriestocities.fragments.InfoFragment
import com.example.gaijin.countriestocities.rest.GeonamesAPI
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Response
import java.lang.Exception


class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        var cityName: String = intent.getStringExtra(getString(R.string.CITY_NAME))
        var country: String = intent.getStringExtra(getString(R.string.COUNTRY_CODE))
        var countryCode: String = country.split(", ")[1]
        var user = getString(R.string.geonames_user)
        var geonamesApi = GeonamesAPI.Factory.getInstance()
        var response: Response<GeonamesResult>? = null

        Observable.just<String>(cityName)
                .map { city ->
                    try {
                        var response = geonamesApi.getCityInfo(city, countryCode, 1, user).execute()
                        if (response.isSuccessful) {
                            var info = response.body()
                            try {
                                Log.d("12", "Response Body " + info!!.geonames[0].toString());
                                return@map info!!.geonames[0]
                            } catch (ex: Exception) {
                                Log.d("12", "Error Body " + response.errorBody());
                            }
                        } else {
                            Log.d("12", "Error Body " + response.errorBody());
                        }
                    } catch (ex: Exception) {
                        ex.stackTrace
                    }
                    return@map null
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cityInfo -> setInfo(cityInfo!!) }
    }

    private fun setInfo(info: GeonamePart) {
        if (info == null) {
            // Load Null fragment Info
            return
        }

        var infoFragment = InfoFragment()
        infoFragment.setInfo(info)
        var fragmentManager = this.supportFragmentManager
        val transaction = fragmentManager.beginTransaction()
        transaction.add(R.id.fragment, infoFragment)
        transaction.commit()

    }
}
