package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.mukesh.countrypicker.Country;
import com.mukesh.countrypicker.CountryPicker;
public class Register2 extends AppCompatActivity {
    Button countrySelector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);

        countrySelector = findViewById(R.id.select_country);

        countrySelector.setOnClickListener(this::onClick);
    }

    private void onClick(View view) {

        CountryPicker picker = new CountryPicker.Builder().with(Register2.this)
                .listener(country -> {
                    /*
                    String info = "Nombre: " + country.getName() +
                            "\nCódigo: " + country.getCode() +
                            "\nDial: " + country.getDialCode();
                    */
                    countrySelector.setText(country.getName());
                }).build();
        picker.showDialog(Register2.this);


    }
}