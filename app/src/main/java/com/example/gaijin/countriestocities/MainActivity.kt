package com.example.gaijin.countriestocities

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.gaijin.countriestocities.adapters.CityAdapter
import com.example.gaijin.countriestocities.services.LoadCountriesService
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.IntentFilter
import android.util.Log
import android.widget.Toast
import android.net.ConnectivityManager
import android.net.NetworkInfo


class MainActivity : AppCompatActivity(), CityAdapter.OnItemClickListener {

    var realmDB: Realm? = null
    var adapter: CityAdapter? = null
    var manager: RecyclerView.LayoutManager? = null
    var loaderReceiver: LoaderDBReceiver? = null;
    var countries: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //
        realmDB = Realm.getDefaultInstance()

        // Create and connect to Broadcast
        val intentFilter = IntentFilter()
        intentFilter.addAction(getString(R.string.BROADCAST_ACTION))
        loaderReceiver = LoaderDBReceiver()
        registerReceiver(loaderReceiver, intentFilter)

        loadData()

    }

    private fun loadData() {
        // Check internet connection
        if (hasInternet(applicationContext)) {
            if (realmDB!!.isEmpty) {
                startService(Intent(this, LoadCountriesService::class.java))
                Toast.makeText(this, "DB is empty", Toast.LENGTH_SHORT).show()
                return;
            } else {
                val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK))
                Toast.makeText(this, "DB is NOT empty", Toast.LENGTH_SHORT).show()
                sendBroadcast(broadcastIntent)
            }
        } else {
            val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
            broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK))
            broadcastIntent.putExtra(
                getString(R.string.EXTRA_CONNECTION_RESULT),
                getString(R.string.check_internet)
            )
            Toast.makeText(this, "Problem with internet", Toast.LENGTH_SHORT).show()
            sendBroadcast(broadcastIntent)
        }
    }

    override fun onStart() {
        super.onStart()
        //Preparation of view components
        country_spinner.visibility = View.INVISIBLE
        city_list.visibility = View.INVISIBLE
        error_text.visibility = View.INVISIBLE

        manager = LinearLayoutManager(this)
        city_list.layoutManager = manager
        adapter = CityAdapter();
        adapter!!.setOnItemClickListener(this)
        city_list.adapter = adapter
    }

    fun hasInternet(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        var wifiInfo: NetworkInfo? = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
        if (wifiInfo != null && wifiInfo.isConnected) {
            return true
        }
        wifiInfo = cm.activeNetworkInfo
        return wifiInfo != null && wifiInfo.isConnected
    }

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
                Snackbar.make(
                    view!!,
                    "${countries!![position]} selected. Count of cities: ${adapter!!.itemCount}",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun loadCountries(): ArrayList<String> {
        realmDB!!.beginTransaction()
        var countries: RealmResults<Country> = realmDB!!.where(Country::class.java).findAll()
        var countryList = ArrayList<String>()
        for (city in countries) {
            countryList.add(city.countryName)
        }
        realmDB!!.commitTransaction()
        countryList.sort()
        return countryList
    }

    private fun loadCities(country: String): List<String> {
        // Load countries form DB
        realmDB!!.beginTransaction()
        var countries: RealmResults<Country> =
            realmDB!!.where(Country::class.java).equalTo("countryName", country).findAll()
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
                    country_spinner.visibility = View.VISIBLE
                    city_list.visibility = View.VISIBLE
                    prepareView()
                }
                getString(R.string.STATUS_NOK) -> {
                    var response: String? = intent.getStringExtra(getString(R.string.EXTRA_CONNECTION_RESULT))
                    error_text.text = "Error! $response"
                    error_text.visibility = View.VISIBLE
                }
                else -> {
                }
            }


        }
    }
}
