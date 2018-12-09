package com.example.gaijin.countriestocities.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.example.gaijin.countriestocities.*;
import com.example.gaijin.countriestocities.dataclasses.CityRealm;
import com.example.gaijin.countriestocities.dataclasses.CountryInfo;
import com.example.gaijin.countriestocities.dataclasses.CountryRealm;
import com.example.gaijin.countriestocities.dataclasses.CountryPOJO;
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return super.onStartCommand(intent, flags, startId);
//        return Service.START_STICKY;
    }


    private void loadCountriesAndCities() {
        // Init REST API
        // Load countries and alpha-codes
        try {
            Response<ResponseBody> response = App.getGeonamesApi().loadMainJson(getString(R.string.countries_url)).execute();
            if (response.isSuccessful()) {
                InputStreamReader streamReader = new InputStreamReader(response.body().byteStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(streamReader);

                String line = reader.readLine();
                reader.close();
                parseJsonString(line);
                reader.close();

                // Send OK broadcast
                Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK));
                sendBroadcast(broadcastIntent);
            } else {
                // Send ERROR broadcast
                Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK));
                broadcastIntent.putExtra(getString(R.string.EXTRA_CONNECTION_RESULT), Integer.toString(response.code()));
                sendBroadcast(broadcastIntent);
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    private void parseJsonString(String line) {
        //Remove extra characters
        line = line.replace("{", "");
        line = line.replace("}", "");
        String[] countries = line.split(getString(R.string.END_OF_COUNTRY));
        for (String country : countries) {
            if (!country.contains("]")) {
                country += "]";
            }
            Observable.just(country)
                    .map(new Function<String, CountryPOJO>() {
                        @Override
                        public CountryPOJO apply(String countryString) throws Exception {
                            CountryPOJO country = parseCountryInfo(countryString);
//                            //Get and convert additional information for country
//                            Response<CountryInfo> countryInfo = App.getGeonamesApi().getCountryInfo(country.getName()).execute();
//                            if (countryInfo.isSuccessful()) {
//                                String newCode = String.format(country.getCode(), countryInfo.body().getAlpha2Code());
//                                String newFlag = String.format(country.getFlag(), countryInfo.body().getFlag());
//                                country.setCode(newCode);
//                                country.setFlag(newFlag);
//                            }
                            try {
                                Log.d("12", country.toString());
                            } catch (Exception ex) {
                                Log.e("12", countryString);
                            }
                            return country;
                        }
                    })
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .onErrorResumeNext(new Function<Throwable, ObservableSource<? extends CountryPOJO>>() {
                        @Override
                        public ObservableSource<? extends CountryPOJO> apply(Throwable throwable) throws Exception {
                            throwable.getStackTrace();
                            return null;
                        }
                    })
                    .filter(countryPOJO -> countryPOJO != null)
                    .filter(countryPOJO -> !countryPOJO.getName().equals(""))
                    .subscribe(countryPOJO -> addCountryToDB(countryPOJO));
        }
    }

    public CountryPOJO parseCountryInfo(String countryString) {
        CountryPOJO country = null;
        try {
            // Normalize of object string
            String[] cityParameters = countryString.split(":");
            //Get information for country
            String name = cityParameters[0];
            String cities = cityParameters[1];
            String format = "\"%s\"";
            String code = format;
            String flag = format;
            String pattern = "{country_name:%s,country_code:%s,flag:%s,cities:%s}";
            String cleanSample = String.format(pattern, name, code, flag, cities);
            // Convert string to CountryPOJO object
            Gson gson = new Gson();
            country = gson.fromJson(cleanSample, CountryPOJO.class);

        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return country;
    }

    private void addCountryToDB(CountryPOJO country) {
        Realm realmDB = App.getDB();
        realmDB.beginTransaction();
        // Create cityList that belong to country
        RealmList<CityRealm> cities = new RealmList<>();
        for (String city : country.getCities()) {
            cities.add(new CityRealm(city));
        }
        try {
            realmDB.insertOrUpdate(new CountryRealm(country.getName(), cities));
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
