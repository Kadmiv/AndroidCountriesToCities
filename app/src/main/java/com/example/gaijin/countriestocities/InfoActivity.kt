package com.example.gaijin.countriestocities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_info.*


class InfoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)
        var cityName: String = "odessa"//intent.getStringExtra(Intent.EXTRA_TEXT)
        name_of_city.text = cityName

        var geonamesAPI = App.getGeonamesApi()
        var coutryCode = "UA"


//        Observable.just<String>(cityName)
//            .map { cityName ->
//                var response = geonamesAPI
//                    .getData(cityName, coutryCode, 1, getString(R.string.geonames_user))
//                    .execute()
//                if (response.isSuccessful()) {
//                    var info: GeonamesInfo? = response.body()
//                    Log.d("12", "Response Body " + info!!.geonames.toString());
//                } else {
//                    Log.d("12", "Error Body " + response.errorBody());
//                }
//
//            }
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe { }


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
