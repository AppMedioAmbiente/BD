package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.firelogin.urlRequest.CountryTemplate;
import com.mukesh.countrypicker.CountryPicker;
import com.example.firelogin.urlRequest.CountryHandler;

import java.util.ArrayList;
import java.util.List;

public class Register1 extends CountryTemplate {
    Button btnNext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);
        btnNext=findViewById(R.id.next);

        initCountryViews(R.id.select_country,R.id.select_state,Register1.this
                ,"Seleccionar un Pais","Seleccionar un Estado");

        btnNext.setOnClickListener(view -> {
            if(!wasCountrySelected()){
                return;
            }
            String stateSelected = stateSelector.getSelectedItem().toString();

            Intent intent=getIntent();
            String values=intent.getStringExtra("values");

            intent = new Intent(Register1.this, Register2.class);
            values+=",country:"+countrySelcted
                +",state:"+stateSelected;
            intent.putExtra("values",values);
            startActivity(intent);
        });

    }
//    public void updateSpinner() {
//        Toast.makeText(Register1.this, "Error al obtener los estados", Toast.LENGTH_SHORT).show();
//        isNextOpen=false;
//    }
//    public void updateSpinner(List<String> states){
//        ArrayAdapter<String> adapter = new ArrayAdapter<>(
//                Register1.this,
//                android.R.layout.simple_spinner_item,
//                states
//        );
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        stateSelector.setAdapter(adapter);
//    }

    private void onClick(View view) {

        CountryPicker picker = new CountryPicker.Builder().with(Register1.this)
                .listener(country -> {
                    countrySelcted=country.getName();
                    countrySelector.setText(countrySelcted);
                    //spiner
                    Log.d("creacion","voy a crear en handler");
                    new CountryHandler(Register1.this, countrySelcted).getStates();
                }).build();
        picker.showDialog(Register1.this);


    }
}