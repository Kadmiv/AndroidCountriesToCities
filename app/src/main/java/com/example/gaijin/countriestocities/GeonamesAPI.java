package com.example.gaijin.countriestocities;

import com.example.gaijin.countriestocities.dataclasses.CountryAlphaCode;
import com.example.gaijin.countriestocities.dataclasses.CountryInfo;
import com.example.gaijin.countriestocities.dataclasses.CountryPOJO;
import com.example.gaijin.countriestocities.dataclasses.GeonamesInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import java.util.List;

public interface GeonamesAPI {
    String CITY_NAME = "q";
    String COUNTRY_CODE = "country";
    String MAX_ROWS = "maxRows";
    String USER = "username";

    @GET("/searchJSON?q=kiev&maxRows=2&username=demo")
    Call<GeonamesInfo> getData(@Query(CITY_NAME) String cityName,
                               @Query(MAX_ROWS) int maxRows,
                               @Query(USER) String user);

    @GET("/searchJSON?q=kiev&maxRows=2&username=demo")
    GeonamesInfo getData(@Query(CITY_NAME) String cityName,
                         @Query(COUNTRY_CODE) String countryCode,
                         @Query(MAX_ROWS) int maxRows,
                         @Query(USER) String user);

    // Request for additional countries information
    @GET("https://restcountries.eu/rest/v2/all?fields=name;alpha2Code;flag")
    Call<List<CountryInfo>> getCountryInfo();
}
