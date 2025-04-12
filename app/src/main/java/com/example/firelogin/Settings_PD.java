package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;

public class Settings_PD extends AppCompatActivity {

    Button cancelPDBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_personaldata);

        Toolbar tbPDSettings = findViewById(R.id.tbPDSettings);
        setSupportActionBar(tbPDSettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cancelPDBtn = findViewById(R.id.cancelPDBtn);
        cancelPDBtn.setOnClickListener(view->{onSupportNavigateUp();});

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
