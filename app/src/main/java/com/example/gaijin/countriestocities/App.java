package com.example.gaijin.countriestocities;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class App extends Application {

    private static GeonamesAPI geonamesAPI;
    private static Realm realmDB;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();
        // Init DB
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);
        realmDB = Realm.getDefaultInstance();

        // Init REST API
        retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.geoname_link))
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        geonamesAPI = retrofit.create(GeonamesAPI.class);
    }

    public static GeonamesAPI getGeonamesApi() {
        return geonamesAPI;
    }

    public static Realm getDB() {
        return realmDB;
    }

    //Check internet connection
    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }

}
