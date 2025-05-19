package com.example.firelogin.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firelogin.R;
import com.example.firelogin.ui.countryTemplate.CountrySelector;
import com.example.firelogin.urlRequest.CountryTemplate;
import com.mukesh.countrypicker.CountryPicker;
import com.example.firelogin.urlRequest.CountryHandler;

public class Register1 extends AppCompatActivity {
    Button btnNext;
    private CountrySelector cs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);
        btnNext=findViewById(R.id.next);

        cs=new CountrySelector(null,"Seleccionar un Pais","Seleccionar un Estado");
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.countryContainer,cs)
                .commit();

//        initCountryViews(R.id.select_country,R.id.select_state,Register1.this
//                ,"Seleccionar un Pais","Seleccionar un Estado");

        btnNext.setOnClickListener(view -> {
            if(!cs.wasCountrySelected()){
                cs.showToastAlert("Debes Seleccionar un Pais");
                return;
            }
            String[] countryData = cs.getSelection();

            Intent intent=getIntent();
            String values=intent.getStringExtra("values");

            intent = new Intent(Register1.this, Register2.class);
            values+=",country:"+countryData[0]
                +",state:"+countryData[1];
            intent.putExtra("values",values);
            startActivity(intent);
        });

    }
//    private void onClick(View view) {
//
//        CountryPicker picker = new CountryPicker.Builder().with(Register1.this)
//                .listener(country -> {
//                    countrySelcted=country.getName();
//                    countrySelector.setText(countrySelcted);
//                    //spiner
//                    Log.d("creacion","voy a crear en handler");
//                    new CountryHandler(Register1.this, countrySelcted).getStates();
//                }).build();
//        picker.showDialog(Register1.this);
//    }
}