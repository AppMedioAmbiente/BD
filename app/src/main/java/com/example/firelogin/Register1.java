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

import com.mukesh.countrypicker.CountryPicker;
import com.example.firelogin.urlRequest.CountryHandler;

import java.util.ArrayList;
import java.util.List;

public class Register1 extends AppCompatActivity {
    Button countrySelector,btnNext;
    Spinner stateSelector;
    Boolean isNextOpen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);
        btnNext=findViewById(R.id.next);

        countrySelector = findViewById(R.id.select_country);
        stateSelector = findViewById(R.id.select_state);

        countrySelector.setOnClickListener(this::onClick);

        btnNext.setOnClickListener(view -> {
            Log.d("open",isNextOpen.toString());
            if(!isNextOpen){
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
        stateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                isNextOpen=true;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Esto se ejecuta si no se selecciona nada (rara vez ocurre)
            }
        });
        List<String> defaultValue =new ArrayList<>() ;
        defaultValue.add("Selecciona un Estado");
        updateSpinner(defaultValue);
        stateSelector.setSelection(0);
    }
    public void updateSpinner() {
        Toast.makeText(Register1.this, "Error al obtener los estados", Toast.LENGTH_SHORT).show();
        isNextOpen=false;
    }
    public void updateSpinner(List<String> states){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                Register1.this,
                android.R.layout.simple_spinner_item,
                states
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSelector.setAdapter(adapter);
    }
String countrySelcted;
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