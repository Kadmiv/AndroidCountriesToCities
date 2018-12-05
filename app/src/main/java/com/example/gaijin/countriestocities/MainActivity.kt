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


class MainActivity : AppCompatActivity(), CityAdapter.OnItemClickListener {

    var realmDB: Realm? = null
    var adapter: CityAdapter? = null
    var manager: RecyclerView.LayoutManager? = null
    var loaderReceiver: LoaderDBReceiver? = null;
    var countries: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        realmDB = Realm.getDefaultInstance()

        // создаем фильтр для BroadcastReceiver
        val intentFilter = IntentFilter(getString(R.string.BROADCAST_ACTION))
        // регистрируем (включаем) BroadcastReceiver
        registerReceiver(loaderReceiver, intentFilter)

        if (realmDB!!.isEmpty) {
            startService(Intent(this, LoadCountriesService::class.java))
            return;
        } else {

        }

    }


    private fun prepareView() {
        // Prepare view components
        manager = LinearLayoutManager(this)
        city_list.layoutManager = manager

        // Load list of countries from DB
        countries = loadCountries()
        val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, countries)
        country_spinner.adapter = arrayAdapter
        country_spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener,
            AdapterView.OnItemClickListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                Snackbar.make(view!!, "Country ${countries!![position]} selected", Snackbar.LENGTH_SHORT).show()
                loadCities(countries!![position])
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
//        return cities.toArray() as Array<String>?
        return countryList
    }

    private fun loadCities(country: String) {
        // Load countries form DB
        realmDB!!.beginTransaction()
        var cities: RealmResults<City> = realmDB!!.where(City::class.java).equalTo("country", country).findAll()
        var cityList = ArrayList<String>()
        for (city in cities) {
            cityList.add(city.name)
        }
        realmDB!!.commitTransaction()
        // Prepare view components
        adapter = CityAdapter(cities)
        adapter!!.setOnItemClickListener(this)
        city_list.adapter = adapter
    }


    override fun onStart() {
        super.onStart()
        city_list.adapter = adapter
        city_list.layoutManager = manager
    }

    override fun onItemClick(view: View?, position: Int) {
        var city: City? = null
        try {
            city = adapter!!.cities.get(position)
        } catch (ex: Exception) {
            return
        }

        if (city != null) {
            var intent = Intent(getString(R.string.EXTRAS_INFO))
            intent.putExtra(Intent.EXTRA_TEXT, city.name)
            startActivity(intent)
        }
    }

    private fun unregisterLoaderReceiver(loaderReceiver: LoaderDBReceiver?) {
        unregisterReceiver(loaderReceiver);
    }

    inner class LoaderDBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            unregisterLoaderReceiver(loaderReceiver);
            Log.d("12", "Broadcast was receive")
            prepareView()
        }
    }
}
