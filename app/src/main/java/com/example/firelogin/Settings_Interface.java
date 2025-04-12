package com.example.firelogin;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Settings_Interface extends AppCompatActivity {

    Button cancelInterfBtn;

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

        cancelInterfBtn = findViewById(R.id.cancelInterfBtn);
        cancelInterfBtn.setOnClickListener(view->{onSupportNavigateUp();});
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

}
