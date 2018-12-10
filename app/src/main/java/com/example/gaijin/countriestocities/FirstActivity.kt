package com.example.gaijin.countriestocities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v7.app.AppCompatActivity
import android.util.Log

import android.widget.Toast
import com.example.gaijin.countriestocities.fragments.CountiesFragment
import com.example.gaijin.countriestocities.fragments.ErrorFragment
import com.example.gaijin.countriestocities.fragments.InProcessFragment
import com.example.gaijin.countriestocities.services.LoadCountriesService
import io.realm.Realm


class FirstActivity : AppCompatActivity() {

    var loaderReceiver: LoaderDBReceiver? = null;

    var realmDB: Realm? = null;
    var fragmentManager: FragmentManager? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create DB
        realmDB = Realm.getDefaultInstance();
        fragmentManager = supportFragmentManager

        registerReceiver()
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

    // This function load data from DB or file, if DB id empty
    private fun loadData() {
        //Loading data process view
        var inProcessFragment = InProcessFragment()
        replaceFragment(inProcessFragment)

        if (realmDB!!.isEmpty) {
            // Check internet connection
            if (App.hasConnection(applicationContext)) {
                startService(Intent(this, LoadCountriesService::class.java))
                return;
            } else {
                sendNokBroadcast(getString(R.string.check_internet))
            }
        } else {
            val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
            broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK))
            sendBroadcast(broadcastIntent)
        }

    }

    private fun sendNokBroadcast(wrongInfo: String) {
        val broadcastIntent = Intent(getString(R.string.BROADCAST_ACTION))
        broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK))
        broadcastIntent.putExtra(getString(R.string.EXTRA_CONNECTION_RESULT), wrongInfo)
        sendBroadcast(broadcastIntent)
    }

    private fun replaceFragment(fragmentForChange: Fragment) {
        val transaction = fragmentManager!!.beginTransaction()
        transaction.replace(R.id.first_fragment_container, fragmentForChange)
        transaction.commit()
    }


    inner class LoaderDBReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {

            var status: String = intent.getStringExtra(getString(R.string.EXTRA_STATUS))
//            Toast.makeText(context, status, Toast.LENGTH_LONG).show()
            when (status) {
                getString(R.string.STATUS_OK) -> {
                    unregisterLoaderReceiver(loaderReceiver)
                    var countryFragment = CountiesFragment()
                    replaceFragment(countryFragment)
                }
                getString(R.string.STATUS_NOK) -> {
                    var response: String? = intent.getStringExtra(getString(R.string.EXTRA_CONNECTION_RESULT))
                    var errorFragment = ErrorFragment()
                    errorFragment.errorText = "Error! $response"
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
    }

    private fun unregisterLoaderReceiver(loaderReceiver: LoaderDBReceiver?) {
        try {
            unregisterReceiver(loaderReceiver);
        } catch (ex: java.lang.Exception) {
            ex.stackTrace
        }
    }

}
