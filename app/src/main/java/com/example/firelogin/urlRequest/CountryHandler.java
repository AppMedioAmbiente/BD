package com.example.firelogin.urlRequest;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.example.firelogin.Register1;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CountryHandler {
    private Register1 activity;
    private String countryName;
    public CountryHandler(Register1 activity, String country){
        this.activity=activity;
        this.countryName=country;
        Log.d("creacion 2","has creado el CH");
    }
    public void getStates(){
        Log.d("get states","get states");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://countriesnow.space/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        ApiService apiService = retrofit.create(ApiService.class);

        // Reemplaza "Mexico" con el país que selecciones en el picker
        CountryRequest request = new CountryRequest(countryName);

        Call<StateResponse> call = apiService.getStates(request);
        call.enqueue(new Callback<StateResponse>() {
            @Override
            public void onResponse(Call<StateResponse> call, Response<StateResponse> response) {
                if (response.isSuccessful()) {
                    List<String> states = response.body().getData().getStates();
                    CountryHandler.this.activity.updateSpinner(states);
                }else {
                    CountryHandler.this.activity.updateSpinner();
                }
            }

            @Override
            public void onFailure(Call<StateResponse> call, Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });

    }
}
