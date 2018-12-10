package com.example.gaijin.countriestocities.rest;

import com.example.gaijin.countriestocities.dataclasses.GeonamesResult;
import com.example.gaijin.countriestocities.dataclasses.Info;

import java.time.Duration;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface GeonamesAPI {
    String CITY_NAME = "q";
    String COUNTRY_CODE = "country";
    String MAX_ROWS = "maxRows";
    String USER = "username";
    String BASE_URL = "http://api.geonames.org";

    @GET("/searchJSON?")
    Call<Info> getCountryInfo(@Query(CITY_NAME) String cityName,
                              @Query(MAX_ROWS) int maxRows,
                              @Query(USER) String user);

    @GET("/wikipediaSearchJSON?")
    Call<GeonamesResult> getCityInfo(@Query(CITY_NAME) String cityName,
                                     @Query(COUNTRY_CODE) String countryCode,
                                     @Query(MAX_ROWS) int maxRows,
                                     @Query(USER) String user);

    @GET()
    Call<ResponseBody> loadMainJson(@Url String fileUrl);

    @GET()
    Call<ResponseBody> loadCountriesJson(@Url String fileUrl);

    class Factory {

        private static GeonamesAPI api;

        public static GeonamesAPI getInstance() {
            if (api == null) {
                // Init REST API
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(GeonamesAPI.class);
                return api;
            } else {
                return api;
            }
        }
    }

}
