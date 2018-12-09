package com.example.gaijin.countriestocities

import android.Manifest
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.gaijin.countriestocities.adapters.CityAdapter
import com.example.gaijin.countriestocities.services.LoadCountriesService
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.widget.Toast
import com.example.gaijin.countriestocities.dataclasses.CountryRealm
import io.realm.Realm
import java.io.File


class FirstActivity : AppCompatActivity(), CityAdapter.OnItemClickListener {

    var adapter: CityAdapter? = null
    var manager: RecyclerView.LayoutManager? = null
    var loaderReceiver: LoaderDBReceiver? = null;
    var countries: ArrayList<String>? = null
    var realmDB: Realm? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        realmDB = Realm.getDefaultInstance();
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


        try_again_btn.setOnClickListener({ loadData() })

        manager = LinearLayoutManager(this)
        city_list.layoutManager = manager
        adapter = CityAdapter();
        adapter!!.setOnItemClickListener(this)
        city_list.adapter = adapter

        loadData()

    }

    override fun onStart() {
        super.onStart()
//        setupPermissions()
    }

    override fun onStop() {
        super.onStop()
        try {
            unregisterLoaderReceiver(loaderReceiver)
        } catch (ex: Exception) {
        }

    }

    private fun loadData() {
        //Preparation of view components
        changeVisibilityOfInfoSection(View.INVISIBLE)
        load_progress.visibility = View.INVISIBLE
        // Error views
        changeVisibilityOfErrorSection(View.INVISIBLE)

        if (realmDB!!.isEmpty) {
            // Check internet connection
            if (App.hasConnection(applicationContext)) {
                load_progress.visibility = View.VISIBLE
                startService(Intent(this, LoadCountriesService::class.java))
                return;
            } else {
                val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK))
                broadcastIntent.putExtra(
                        getString(R.string.EXTRA_CONNECTION_RESULT),
                        getString(R.string.check_internet)
                )
                sendBroadcast(broadcastIntent)
            }
        } else {
            val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
            broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK))
            sendBroadcast(broadcastIntent)
        }

    }

    // Visibility switcher of error information views
    private fun changeVisibilityOfErrorSection(value: Int) {
        error_text.visibility = value
        try_again_btn.visibility = value
        not_connect_image.visibility = value
    }

    // Visibility switcher of information views
    private fun changeVisibilityOfInfoSection(value: Int) {
        country_spinner.visibility = value
        city_list.visibility = value
    }

    //This function prepare all views which contains information about countries and cities
    private fun prepareView() {
        // Load list of countries from DB
        countries = loadCountries()
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        country_spinner.adapter = arrayAdapter

        country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
                AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                adapter!!.cities = loadCities(countries!![position])
            }
        }
    }

    // Load all country from DB
    private fun loadCountries(): ArrayList<String> {
        realmDB!!.beginTransaction()
        var countries: RealmResults<CountryRealm> = realmDB!!.where(CountryRealm::class.java).findAll()
        var countryList = ArrayList<String>()
        for (city in countries) {
            countryList.add(city.countryName)
        }
        realmDB!!.commitTransaction()
        countryList.sort()
        return countryList
    }

    // Load all cities that are specific to a country
    private fun loadCities(country: String): List<String> {
        realmDB!!.beginTransaction()
        var countries: RealmResults<CountryRealm> =
                realmDB!!.where(CountryRealm::class.java).equalTo("countryName", country).findAll()
        var cityList = ArrayList<String>()
        for (city in countries[0]!!.cities) {
            cityList.add(city.cityName)
        }
        realmDB!!.commitTransaction()
        cityList.sort()
        return cityList
    }

    override fun onItemClick(view: View?, position: Int) {
        var city: String? = null
        try {
            city = adapter!!.cities[position]
        } catch (ex: Exception) {
            return
        }

        if (city != null) {
            var intent = Intent(getString(R.string.EXTRAS_INFO))
            intent.putExtra(Intent.EXTRA_TEXT, city)
            startActivity(intent)
        }
    }

    private fun unregisterLoaderReceiver(loaderReceiver: LoaderDBReceiver?) {
        unregisterReceiver(loaderReceiver);
    }

    inner class LoaderDBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            Log.d("12", "Broadcast was receive")
            Toast.makeText(applicationContext, "Broadcast was receive", Toast.LENGTH_SHORT).show()
            var status: String = intent.getStringExtra(getString(R.string.EXTRA_STATUS))
            load_progress.visibility = View.INVISIBLE
            when (status) {
                getString(R.string.STATUS_OK) -> {
                    unregisterLoaderReceiver(loaderReceiver)
                    changeVisibilityOfInfoSection(View.VISIBLE)
                    changeVisibilityOfErrorSection(View.INVISIBLE)
                    prepareView()
                }
                getString(R.string.STATUS_NOK) -> {
                    var response: String? = intent.getStringExtra(getString(R.string.EXTRA_CONNECTION_RESULT))
                    error_text.text = "Error! $response"
                    changeVisibilityOfInfoSection(View.INVISIBLE)
                    changeVisibilityOfErrorSection(View.VISIBLE)
                }
                else -> {
                }
            }
        }
    }

//    /*Next methods for check and get permissions from user*/
//    // From - https://www.techotopia.com/index.php/Kotlin_-_Making_Runtime_Permission_Requests_in_Android
//
//    val PERMISSIONS_REQUEST_CODE = 911
//    private val CALL_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE
//    private fun setupPermissions() {
//        val permission = ContextCompat.checkSelfPermission(
//            this,
//            CALL_PERMISSION
//        )
//
//        if (permission != PackageManager.PERMISSION_GRANTED) {
//            Log.i("12", "Permission denied")
//            makeRequest()
//        }
//    }
//
//    // Permissions request
//    private fun makeRequest() {
//        ActivityCompat.requestPermissions(
//            this,
//            arrayOf(CALL_PERMISSION),
//            PERMISSIONS_REQUEST_CODE
//        )
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//
//        when (requestCode) {
//            PERMISSIONS_REQUEST_CODE -> {
//                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                    Log.i("12", "Permission has been denied by user")
//                } else {
//                    Log.i("12", "Permission has been granted by user")
//                }
//            }
//        }
//    }

}
