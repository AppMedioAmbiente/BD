package com.example.firelogin.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firelogin.Home;
import com.example.firelogin.R;

public class Settings_Interface extends AppCompatActivity {

    Button cancelInterfBtn;
    Switch swDarkMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_interface);

        Toolbar tbInterfSettings = findViewById(R.id.tbInterfSettings);
        setSupportActionBar(tbInterfSettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        //cancelInterfBtn = findViewById(R.id.cancelInterfBtn);
        //cancelInterfBtn.setOnClickListener(view->{onSupportNavigateUp();});

        //final Home homeAct = new Home();
        SharedPreferences sharedPref = getSharedPreferences("shrdPrf", MODE_PRIVATE);
        final SharedPreferences.Editor editSharedPref = sharedPref.edit();
        int theme = sharedPref.getInt("Theme", 0);
        swDarkMode = findViewById(R.id.swDarkMode);

        if (theme == 0) {
            swDarkMode.setChecked(true);
        } else if (theme == 1) {
            swDarkMode.setChecked(false);
        }
        swDarkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                editSharedPref.putInt("Theme", 0);
            } else {
                editSharedPref.putInt("Theme", 1);
            }
            editSharedPref.apply();
            Home.setDarkMode(Settings_Interface.this);
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
