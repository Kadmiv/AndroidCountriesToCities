package com.example.gaijin.countriestocities.rest;

import com.example.gaijin.countriestocities.dataclasses.CountryAlphaCode;
import com.example.gaijin.countriestocities.dataclasses.CountryInfo;
import com.example.gaijin.countriestocities.dataclasses.CountryPOJO;
import com.example.gaijin.countriestocities.dataclasses.GeonamesInfo;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

import java.util.List;

public interface GeonamesAPI {
    String CITY_NAME = "q";
    String COUNTRY_CODE = "country";
    String MAX_ROWS = "maxRows";
    String USER = "username";

    @GET("/searchJSON?")
    Call<GeonamesInfo> getData(@Query(CITY_NAME) String cityName,
                               @Query(MAX_ROWS) int maxRows,
                               @Query(USER) String user);

    @GET("/searchJSON?")
    Call<GeonamesInfo> getData(@Query(CITY_NAME) String cityName,
                               @Query(COUNTRY_CODE) String countryCode,
                               @Query(MAX_ROWS) int maxRows,
                               @Query(USER) String user);

    @GET()
    Call<ResponseBody> loadMainJson(@Url String fileUrl);

}
