package com.example.gaijin.countriestocities.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.example.gaijin.countriestocities.R
import com.example.gaijin.countriestocities.adapters.CityAdapter
import com.example.gaijin.countriestocities.dataclasses.RealmCountry
import io.realm.Realm
import io.realm.RealmResults
import kotlinx.android.synthetic.main.fragment_counties.*


class CountiesFragment : Fragment(), CityAdapter.OnItemClickListener {

    internal var adapter: CityAdapter? = null
    internal var manager: RecyclerView.LayoutManager? = null
    internal var realmDB: Realm? = null;

    var countries: ArrayList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        realmDB = Realm.getDefaultInstance()
        manager = LinearLayoutManager(context)
        adapter = CityAdapter();
        adapter!!.setOnItemClickListener(this)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_counties, container, false)
    }

    override fun onStart() {
        super.onStart()
        city_list.layoutManager = manager
        city_list.adapter = adapter
        prepareView()
    }


    //This function prepare all views which contains information about countries and cities
    private fun prepareView() {
        // Load list of countries from DB
        countries = loadCountries()
        val arrayAdapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, countries)
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
        var countries: RealmResults<RealmCountry> = realmDB!!.where(RealmCountry::class.java).findAll()
        var countryList = ArrayList<String>()
        for (city in countries) {
            countryList.add("${city.countryName}, ${city.alphaCode}")
        }
        realmDB!!.commitTransaction()
        countryList.sort()
        return countryList
    }

    // Load all cities that are specific to a country
    private fun loadCities(country: String): List<String> {
        realmDB!!.beginTransaction()
        var name = country.split(", ")[0]
        var countries: RealmResults<RealmCountry> =
                realmDB!!.where(RealmCountry::class.java).equalTo("countryName", name).findAll()
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
            intent.putExtra(getString(R.string.CITY_NAME), city)
            var country = countries!![country_spinner.selectedItemPosition]
            intent.putExtra(getString(R.string.COUNTRY_CODE), country)
            startActivity(intent)
        }
    }

    override fun onDetach() {
        super.onDetach()
    }
}