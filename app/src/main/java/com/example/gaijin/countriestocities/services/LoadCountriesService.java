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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;

public class LoadCountriesService extends Service {

    private final String JSON_URL = "https://raw.githubusercontent.com/David-Haim/CountriesToCitiesJSON/master/countriesToCities.json";
    private final int TIME_OUT = 5000;
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

        Runnable runnable = () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(JSON_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(TIME_OUT);
                connection.setReadTimeout(TIME_OUT);
                connection.connect();
                if (connection.getResponseCode() == connection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
                    StringBuilder strBuilder = new StringBuilder();
                    int symbol = 0;
                    for (symbol = reader.read(); symbol != -1; symbol = reader.read()) {
                        char sign = (char) symbol;
                        strBuilder.append(sign);
                        if (sign == ']') {
                            Observable.just(strBuilder.toString())
                                    .map(new Function<String, CountryPOJO>() {
                                        @Override
                                        public CountryPOJO apply(String countryString) throws Exception {
                                            InfoParser parser = new InfoParser();
                                            return parser.parseCountryInfo(countryString);
                                        }
                                    })
                                    .subscribeOn(Schedulers.newThread())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(country -> addCitiesToDB(country));
                            strBuilder = new StringBuilder();
                        }
                    }
                    reader.close();

                    sleep(500);
                    Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                    sendBroadcast(broadcastIntent);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread thread = new Thread(runnable);
        thread.start();

        return super.onStartCommand(intent, flags, startId);
//        return Service.START_STICKY;
    }

    private void addCitiesToDB(CountryPOJO country) {
        realmDB.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                // Add cities to DB
                List<String> citiesName = country.getCities();
                ArrayList<City> cities = new ArrayList<>(citiesName.size());
                for (String cityName : citiesName) {
                    cities.add(new City(cityName, country.getCountryName()));
                }
                realm.insertOrUpdate(cities);
                // Add country to DB
                realm.insertOrUpdate(new Country(country.getCountryName()));
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                Log.d("12", "Country was added : " + country.getCountryName());
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                error.getStackTrace();
                Log.e("12", "Country was NOT added : " + country.getCountryName());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
