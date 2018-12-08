package com.example.gaijin.countriestocities.services;

import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import com.example.gaijin.countriestocities.*;
import com.example.gaijin.countriestocities.dataclasses.CityRealm;
import com.example.gaijin.countriestocities.dataclasses.CountryInfo;
import com.example.gaijin.countriestocities.dataclasses.CountryRealm;
import com.example.gaijin.countriestocities.dataclasses.CountryPOJO;
import com.google.gson.Gson;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import io.realm.RealmList;
import retrofit2.Response;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Thread.sleep;

public class LoadCountriesService extends Service {

    private final String JSON_URL = "https://raw.githubusercontent.com/David-Haim/CountriesToCitiesJSON/master/countriesToCities.json";
    private final String JSON_URL_2 = "https://github.com/CoalaWeb/cw-country-iso-code/blob/master/src/cw-country-iso-code.json";

    String ROOT_FOLDER = Environment.getExternalStorageDirectory().getPath() + "/" + Environment.DIRECTORY_DOWNLOADS;

    private final int TIME_OUT = 30000;

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
                        loadCountriesAndCities(loadAlphaCodes());
                        return new Object();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return super.onStartCommand(intent, flags, startId);
//        return Service.START_STICKY;
    }

    private HashMap<String, CountryInfo> loadAlphaCodes() {
        // Load addtional countries information
        HashMap<String, CountryInfo> alphaCodes = null;
        try {
            Response<List<CountryInfo>> response = App.getGeonamesApi().getCountryInfo().execute();
            if (response.isSuccessful()) {
                alphaCodes = new HashMap<>(250);
                for (CountryInfo country : response.body()) {
                    alphaCodes.put(country.getName().toLowerCase(), country);
                }
            } else {
                Log.d("12", " Bad response of server !! ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return alphaCodes;
    }

    private void loadCountriesAndCities(HashMap<String, CountryInfo> loadAlphaCodes) {
        // Init REST API
        // Load countries and alpha-codes
        try {

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

                String folder = makeFolder(ROOT_FOLDER, "Json");
                scanningFolder(Environment.getExternalStorageDirectory().getPath());
                File jsonFile = new File(folder + "/country.json");
                jsonFile.createNewFile();
                BufferedWriter bw = new BufferedWriter(new FileWriter(jsonFile));
                for (String country : countries) {
                    if (!country.contains(getString(R.string.END_OF_COUNTRY))) {
                        country += getString(R.string.END_OF_COUNTRY);
                    }
                    Observable.just(country)
                            .map(new Function<String, CountryPOJO>() {
                                @Override
                                public CountryPOJO apply(String countryString) throws Exception {
                                    InfoParser parser = new InfoParser();
                                    CountryPOJO country = parser.parseCountryInfo(countryString, loadAlphaCodes);
                                    try {
                                        Log.d("12", country.toString());
                                        Gson gson = new Gson();
                                        bw.write(gson.toJson(country));
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

                sleep(2000);
                reader.close();
                bw.close();
                Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_OK));
                sendBroadcast(broadcastIntent);
            } else {
                Intent broadcastIntent = new Intent(getString(R.string.BROADCAST_ACTION));
                broadcastIntent.putExtra(getString(R.string.EXTRA_STATUS), getString(R.string.STATUS_NOK));
                broadcastIntent.putExtra(getString(R.string.EXTRA_CONNECTION_RESULT), Integer.toString(response));
                sendBroadcast(broadcastIntent);
            }
        } catch (Exception ex) {
            ex.getStackTrace();
        }
    }

    private void addCountriesToDB(ArrayList<CountryPOJO> countries) {
        Realm realmDB = App.getDB();
        if (countries == null || countries.isEmpty()) {
            return;
        }
        realmDB.beginTransaction();
        // Add country to DB
        for (CountryPOJO country : countries) {
            RealmList<CityRealm> cities = new RealmList<>();
            for (String city : country.getCities()) {
                cities.add(new CityRealm(city));
            }
            try {
                realmDB.insertOrUpdate(new CountryRealm(country.getCountryName(), cities));
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
        Realm realmDB = App.getDB();
        realmDB.beginTransaction();
        // Add country to DB
        RealmList<CityRealm> cities = new RealmList<>();
        for (String city : country.getCities()) {
            cities.add(new CityRealm(city));
        }
        try {
            realmDB.insertOrUpdate(new CountryRealm(country.getCountryName(), cities));
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

    protected String makeFolder(String mainPath, String name) {
        /*Create path for new folder*/
        String folderPath = mainPath + "/" + name + "/";
        File folder = new File(folderPath);
        //Log.d(LOG_TAG, "Path folder " + folderPath);
        /*Check folder on disc*/
        if (!folder.exists()) {
            folder.mkdir();
            //Log.d(LOG_TAG, "Folder is exist " + folder.getAbsolutePath());
        }
        return folder.getAbsolutePath();
    }

    protected void scanningFolder(String mainFolderPath) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri contentUri = Uri.fromFile(new File(mainFolderPath));
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}
