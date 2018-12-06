package com.example.gaijin.countriestocities.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.example.gaijin.countriestocities.*;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class LoadCountriesService extends Service {

    private final String JSON_URL = "https://raw.githubusercontent.com/David-Haim/CountriesToCitiesJSON/master/countriesToCities.json";
    private final int TIME_OUT = 30000;
    private Realm realmDB = null;

    public LoadCountriesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        realmDB = Realm.getDefaultInstance();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Observable.just("Http Connection")
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String s) throws Exception {
                        HttpURLConnection connection = (HttpURLConnection) new URL(JSON_URL).openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(TIME_OUT);
                        connection.setReadTimeout(TIME_OUT);
                        connection.connect();
                        int response = connection.getResponseCode();
                        if (response == connection.HTTP_OK) {
                            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                            StringBuilder strBuilder = new StringBuilder();
                            int symbol = 0;
                            String line = reader.readLine();
                            //Remove extra characters
                            line = line.replace("{", "");
                            line = line.replace("}", "");
                            String[] countries = line.split(getString(R.string.END_OF_COUNTRY));
                            for (String country : countries) {
                                if (!country.contains(getString(R.string.END_OF_COUNTRY))) {
                                    country += getString(R.string.END_OF_COUNTRY);
                                }
                                Observable.just(country)
                                        .map(new Function<String, CountryPOJO>() {
                                            @Override
                                            public CountryPOJO apply(String countryString) throws Exception {
                                                InfoParser parser = new InfoParser();
                                                CountryPOJO country = parser.parseCountryInfo(countryString);
                                                try {
                                                    Log.d("12", country.toString());
                                                } catch (Exception ex) {
                                                    Log.e("12", countryString);
                                                }
                                                return country;
                                            }
                                        })
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .filter(countryPOJO -> countryPOJO != null)
                                        .subscribe(countryPOJO -> addCountryToDB(countryPOJO));
                            }
                            reader.close();
                            Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                            broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK));
                            sendBroadcast(broadcastIntent);
                        } else {
                            Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                            broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK));
                            broadcastIntent.putExtra(getString(R.string.EXTRA_CONNECTION_RESULT), Integer.toString(response));
                            sendBroadcast(broadcastIntent);
                        }
                        return new Object();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return super.onStartCommand(intent, flags, startId);
//        return Service.START_STICKY;
    }

    private void addCountriesToDB(ArrayList<CountryPOJO> countries) {
        if (countries == null || countries.isEmpty()) {
            return;
        }
        realmDB.beginTransaction();
        // Add country to DB
        for (CountryPOJO country : countries) {
            RealmList<City> cities = new RealmList<>();
            for (String city : country.getCities()) {
                cities.add(new City(city));
            }
            try {
                realmDB.insertOrUpdate(new Country(country.getCountryName(), cities));
            } catch (Exception ex) {
                ex.getStackTrace();
                System.err.println(country.toString());
                Log.e("12", country.toString());
            }
        }

        realmDB.commitTransaction();

        Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
        sendBroadcast(broadcastIntent);
    }

    private void addCountryToDB(CountryPOJO country) {
        realmDB.beginTransaction();
        // Add country to DB
        RealmList<City> cities = new RealmList<>();
        for (String city : country.getCities()) {
            cities.add(new City(city));
        }
        try {
            realmDB.insertOrUpdate(new Country(country.getCountryName(), cities));
            Log.d("12", country.toString());
        } catch (Exception ex) {
            ex.getStackTrace();
            System.err.println(country.toString());
            Log.e("12", country.toString());
        }
        realmDB.commitTransaction();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
