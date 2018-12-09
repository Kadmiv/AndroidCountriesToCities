package com.example.gaijin.countriestocities.rest;

import com.example.gaijin.countriestocities.dataclasses.CountryInfo;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface RestcountriesAPI {

    @GET("https://restcountries.eu/rest/v2/name/{country}?fields=alpha2Code;flag")
    Call<CountryInfo> getCountryInfo(@Path("country") String country);
}
