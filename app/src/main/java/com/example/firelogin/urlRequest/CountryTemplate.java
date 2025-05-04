package com.example.firelogin.urlRequest;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firelogin.R;
import com.example.firelogin.Register1;
import com.mukesh.countrypicker.CountryPicker;
//rajas,1 rojo,  2 queso
import java.util.ArrayList;
import java.util.List;

public class CountryTemplate extends AppCompatActivity {
    protected TextView countrySelector;
    protected Spinner stateSelector;
    protected String countrySelcted,stateSelected;
    CountryTemplate activity;
    Boolean onlyViewMode=true;
    protected void initCountryViews(
            int id_country_selector, int id_state_selector,
            CountryTemplate activity,
            String countryText, String stateText
    ){
        this.activity=activity;
        this.countrySelcted=countryText;
        stateSelected=stateText;
        try{
            countrySelector = findViewById(id_country_selector);
            stateSelector = findViewById(id_state_selector);
//        (R.id.select_country,R.id.select_state)
            countrySelector.setOnClickListener(this::onClick);

            stateSelector.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (onlyViewMode) {
                        new CountryHandler(activity, countrySelcted).getStates();
                        onlyViewMode = false;
                    }
                    return false;
                }
            });
            stateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(stateSelector.getSelectedItem()!=null) {
                        stateSelected = stateSelector.getSelectedItem().toString();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch(Exception ex) {
            Log.d("selctores",ex.getMessage().toString());
            Toast.makeText(activity, "Error, no se encontraron los selectores", Toast.LENGTH_SHORT).show();
            return;
        }
        countrySelector.setText(countryText);

        List<String> defaultValue =new ArrayList<>() ;
        defaultValue.add(stateText);

        updateSpinner(defaultValue);
        stateSelector.setSelection(0);
    }
    private void onClick(View view) {
        CountryPicker picker = new CountryPicker.Builder().with(activity)
                .listener(country -> {
                    countrySelcted=country.getName();
                    countrySelector.setText(countrySelcted);
                    //spiner
                    Log.d("creacion","voy a crear en handler");
                    new CountryHandler(activity, countrySelcted).getStates();
                    onlyViewMode=false;
                }).build();
        picker.showDialog(activity);
    }
    protected String getCountrySelected(){
        countrySelcted= (String) countrySelector.getText();
        return countrySelcted;
    }
    protected String getStateSelected(){
        stateSelected=stateSelector.getSelectedItem().toString();
//        Toast.makeText(activity, "Estado:"+stateSelected, Toast.LENGTH_SHORT).show();
        return stateSelected;
    }
    protected void updateSpinner(List<String> states){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                activity,
                android.R.layout.simple_spinner_item,
                states
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSelector.setAdapter(adapter);
    }
    protected void updateSpinner() {
        Toast.makeText(activity, "Error al obtener los estados", Toast.LENGTH_SHORT).show();
    }
    protected Boolean wasCountrySelected(){
        return (!countrySelcted.equals("Seleccionar un Pais"));
    }
    protected void showToastAlert(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
        Log.d("nuestro sistema",mensaje);
    }
}
