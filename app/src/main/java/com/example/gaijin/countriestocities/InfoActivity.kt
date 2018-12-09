package com.example.gaijin.countriestocities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.gaijin.countriestocities.dataclasses.GeonamesInfo
import com.example.gaijin.countriestocities.rest.GeonamesAPI
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_info.*
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception


class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        var cityName: String = intent.getStringExtra(Intent.EXTRA_TEXT)
        var user = getString(R.string.geonames_user)
        var geonamesApi = GeonamesAPI.Factory.getInstance()
        var response: Response<GeonamesInfo>? = null

        Observable.just<String>(cityName)
                .map { city ->
                    try {
                        var response = geonamesApi.getData(city, 1, user).execute()
                        if (response.isSuccessful()) {
                            var info = response.body()
                            try {
                                Log.d("12", "Response Body " + info!!.geonames.toString());
                                return@map info!!.geonames.toString()
                            } catch (ex: Exception) {
                                Log.d("12", "Error Body " + response.errorBody());
                            }
                        } else {
                            Log.d("12", "Error Body " + response.errorBody());
                        }
                    } catch (ex: Exception) {
                        ex.stackTrace
                    }
                    return@map ""
                }
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { cityInfo -> name_of_city.text = cityInfo }


        //        Observable.just<String>(cityName)
//            .map { cityName ->
//                WebService.setUserName(getString(R.string.geonames_user))
//
//                val searchCriteria = ToponymSearchCriteria()
//                searchCriteria.q = cityName
//                searchCriteria.language = "ru"
//                val searchResult = WebService.search(searchCriteria)
//                for (toponym in searchResult.toponyms) {
//                    println(toponym.name + " " + toponym.countryName)
//                    Log.d("12", toponym.name + " " + toponym.countryName + " " + toponym.countryCode)
//                }
//
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { }

    }
}
