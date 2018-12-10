package com.example.gaijin.countriestocities.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.example.gaijin.countriestocities.*;
import com.example.gaijin.countriestocities.dataclasses.Country;
import com.example.gaijin.countriestocities.dataclasses.GeonamePart;
import com.example.gaijin.countriestocities.dataclasses.Info;
import com.example.gaijin.countriestocities.dataclasses.RealmCity;
import com.example.gaijin.countriestocities.dataclasses.RealmCountry;
import com.example.gaijin.countriestocities.rest.GeonamesAPI;
import com.google.gson.Gson;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import okhttp3.ResponseBody;
import retrofit2.Response;

import java.io.*;

import static java.lang.Thread.sleep;

public class LoadCountriesService extends Service {

    private GeonamesAPI geonamesApi;

    public LoadCountriesService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Observable.just("Http Connection")
                .map(new Function<String, Object>() {
                    @Override
                    public Object apply(String s) throws Exception {
                        loadCountriesAndCities();
                        return new Object();
                    }
                })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return super.onStartCommand(intent, flags, startId);
//        return Service.START_STICKY;
    }

    private void loadCountriesAndCities() {
        // Load countries and alpha-codes
        geonamesApi = GeonamesAPI.Factory.getInstance();
        try {
            Response<ResponseBody> response = geonamesApi.loadMainJson(getString(R.string.countries_url)).execute();
            if (response.isSuccessful()) {
                // Read all JSON file
                InputStreamReader streamReader = new InputStreamReader(response.body().byteStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(streamReader);
                String line = reader.readLine();
                reader.close();

                parseJsonString(line);
                // Send OK broadcast
                Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK));
                sendBroadcast(broadcastIntent);
            } else {
                // Send ERROR broadcast
                sendNokBroadcast(Integer.toString(response.code()));
            }
        } catch (Exception ex) {
            ex.getStackTrace();
            sendNokBroadcast(getString(R.string.check_internet));
        }
    }

    // This function parse all JSON string for separate string-object
    // After that string convert to Country object
    private void parseJsonString(String line) {
        //Remove extra characters
        line = line.replace("{", "");
        line = line.replace("}", "");
        String[] countries = line.split(getString(R.string.END_OF_COUNTRY));
        for (String country : countries) {
            if (!country.contains("]")) {
                country += "]";
            }

            Country countryObject = null;
            try {
                countryObject = convertToCountryObject(country);
            } catch (Exception ex) {
                ex.getStackTrace();
                Log.e("12", country);
            }

            Observable.just(country)
                    .map(new Function<String, Country>() {
                        @Override
                        public Country apply(String countryString) throws Exception {

                            Country country = null;
                            try {
                                country = convertToCountryObject(countryString);
                                country = addAdditionalInfo(country);
                                Log.d("12", country.toString());
                            } catch (Exception ex) {
                                ex.getStackTrace();
                                Log.e("12", countryString);
                            }
                            return country;
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends Country>>() {
                        @Override
                        public ObservableSource<? extends Country> apply(Throwable throwable) throws Exception {
                            throwable.getStackTrace();
                            return null;
                        }
                    })
                    .filter(country_ -> country_ != null)
                    .filter(country_ -> !country_.getName().equals("%s"))
                    .subscribe(country_ -> addCountryToDB(country_));
        }
    }

    public Country convertToCountryObject(String countryString) {
        Country country = null;
        String pattern = "{country_name:%s,country_code:%s,flag:%s,cities:%s}";
        String format = "\"%s\"";
        try {
            // Normalize of object string
            String[] cityParameters = countryString.split(":");
            //Get information for country
            String name = cityParameters[0];
            name = name.replace("[", "(")
                    .replace("]", ")");
            String cities = cityParameters[1];
            String code = format;
            String flag = format;
            String cleanSample = String.format(pattern, name, code, flag, cities);
            // Convert string to Country object
            Gson gson = new Gson();
            country = gson.fromJson(cleanSample, Country.class);

        } catch (Exception ex) {
            ex.getStackTrace();
            String cleanSample = String.format(pattern, format, format, format, "[]");
            // Convert string to Country object
            Gson gson = new Gson();
            country = gson.fromJson(cleanSample, Country.class);
        }
        return country;
    }


    // This function send request and add additional info (alpha-code) for country
    private Country addAdditionalInfo(Country country) {
        try {
            Response<Info> response = geonamesApi
                    .getCountryInfo(country.getName(), 1, getString(R.string.geonames_user))
                    .execute();
            if (response.isSuccessful()) {
                Info countryInfo = response.body();
                GeonamePart geonamePart = countryInfo.getGeonames().get(0);
                String newCode = String.format(country.getCode(), geonamePart.getCountryCode());
                country.setCode(newCode);
            } else {

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return country;
    }

    private void addCountryToDB(Country country) {
        Realm realmDB = Realm.getDefaultInstance();
        realmDB.beginTransaction();
        // Create cityList that belong to country
        RealmList<RealmCity> cities = new RealmList<>();
        for (String city : country.getCities()) {
            cities.add(new RealmCity(city));
        }
        try {
            RealmCountry newCountry = new RealmCountry(country.getName(), cities);
            newCountry.setAlphaCode(country.getCode());
            realmDB.insert(newCountry);
        } catch (Exception ex) {
//            sendNokBroadcast(getString(R.string.check_internet));
        }
        realmDB.commitTransaction();
    }

    private void sendNokBroadcast(String wrongInfo) {
        Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
        broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK));
        broadcastIntent.putExtra(getString(R.string.EXTRA_CONNECTION_RESULT), wrongInfo);
        sendBroadcast(broadcastIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
