package com.example.firelogin.urlRequest;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("api/v0.1/countries/states")
    Call<StateResponse> getStates(@Body CountryRequest countryRequest);
}
