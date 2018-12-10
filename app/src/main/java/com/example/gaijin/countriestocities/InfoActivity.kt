package com.example.gaijin.countriestocities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.util.Log
import android.widget.Toast
import com.example.gaijin.countriestocities.dataclasses.GeonamePart
import com.example.gaijin.countriestocities.dataclasses.GeonamesResult
import com.example.gaijin.countriestocities.fragments.CountiesFragment
import com.example.gaijin.countriestocities.fragments.ErrorFragment
import com.example.gaijin.countriestocities.fragments.InProcessFragment
import com.example.gaijin.countriestocities.fragments.InfoFragment
import com.example.gaijin.countriestocities.rest.GeonamesAPI
import com.example.gaijin.countriestocities.services.LoadCountriesService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception


class InfoActivity : AppCompatActivity() {

    var loaderReceiver: LoaderDBReceiver? = null;
    var fragmentManager: FragmentManager? = null;
    var cityName: String? = null
    var countryCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        //Loading data process view
        cityName = intent.getStringExtra(getString(R.string.CITY_NAME))
        var country: String = intent.getStringExtra(getString(R.string.COUNTRY_CODE))
        countryCode = country.split(", ")[1]

        fragmentManager = supportFragmentManager
        var inProcessFragment = InProcessFragment()
        replaceFragment(inProcessFragment)

        loadData()
    }

    override fun onStart() {
        super.onStart()
        registerReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterLoaderReceiver(loaderReceiver)
    }

    private fun loadData() {
        var user = getString(R.string.geonames_user)
        var geonamesApi = GeonamesAPI.Factory.getInstance()

        var call: Call<GeonamesResult> = geonamesApi.getCityInfo(cityName, countryCode, 1, user)
        call.enqueue(object : Callback<GeonamesResult> {

            override fun onResponse(call: Call<GeonamesResult>?, response: Response<GeonamesResult>?) {
                var result: GeonamePart? = null
                try {
                    if (response!!.isSuccessful) {
                        var info = response.body()
                        try {
                            result = info!!.geonames[0]
                            setInfo(result)
                        } catch (ex: Exception) {
                        }
                    } else {
                        sendNokBroadcast(Integer.toString(response.code()))
                    }
                } catch (ex: Exception) {
                    sendNokBroadcast(getString(R.string.something_wrong))
                    ex.stackTrace
                }
            }

            override fun onFailure(call: Call<GeonamesResult>?, t: Throwable?) {
                sendNokBroadcast(getString(R.string.something_wrong))
            }
        })
    }

    private fun sendNokBroadcast(wrongInfo: String) {
        val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
        broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK))
        broadcastIntent.putExtra(getString(R.string.EXTRA_CONNECTION_RESULT), wrongInfo)
        sendBroadcast(broadcastIntent)
    }

    private fun setInfo(info: GeonamePart) {
        if (info == null) {
            // Load Null fragment Info
            sendNokBroadcast(getString(R.string.info_not_found))
            return
        }

        var infoFragment = InfoFragment()
        infoFragment.info = info
        replaceFragment(infoFragment)
    }

    private fun replaceFragment(fragmentForChange: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.info_fragment_container, fragmentForChange)
        transaction.commit()
    }

    inner class LoaderDBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            var status: String = intent.getStringExtra(getString(R.string.EXTRA_STATUS))
            when (status) {
                getString(R.string.STATUS_OK) -> {
                    unregisterLoaderReceiver(loaderReceiver)
                    var countryFragment = CountiesFragment()
                    replaceFragment(countryFragment)
                }
                getString(R.string.STATUS_NOK) -> {
                    var response: String? = intent.getStringExtra(getString(R.string.EXTRA_CONNECTION_RESULT))
                    var errorFragment = ErrorFragment()
                    errorFragment.errorText = "$response"
                    replaceFragment(errorFragment)
                }
                getString(R.string.STATUS_RELOAD) -> {
                    loadData()
                }
                else -> {
                }
            }
        }
    }

    private fun registerReceiver() {
        // Create and connect to Broadcast
        val intentFilter = IntentFilter()
        intentFilter.addAction(getString(R.string.BROADCAST_ACTION))
        loaderReceiver = LoaderDBReceiver()
        try {
            registerReceiver(loaderReceiver, intentFilter)
        } catch (ex: Exception) {
            unregisterLoaderReceiver(loaderReceiver)
            registerReceiver(loaderReceiver, intentFilter)
        }

        loadData()
    }


    private fun unregisterLoaderReceiver(loaderReceiver: LoaderDBReceiver?) {
        try {
            unregisterReceiver(loaderReceiver);
        } catch (ex: java.lang.Exception) {
            ex.stackTrace
        }
    }
}
