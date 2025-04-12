package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import java.util.HashMap;

public class Settings extends AppCompatActivity {

    LinearLayout btnPersonal_data, btnNotification, btnAutentication, btnInterface;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        Toolbar tbSettings = findViewById(R.id.tbSettings);
        setSupportActionBar(tbSettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        btnPersonal_data = (LinearLayout) findViewById(R.id.personal_data);

        btnPersonal_data.setOnClickListener(view ->  {
            Intent intent = new Intent(Settings.this, Settings_PD.class);
            startActivity(intent);

        });

        btnNotification = (LinearLayout) findViewById(R.id.notification);

        btnNotification.setOnClickListener(view ->  {
                Intent intent = new Intent(Settings.this, Settings_Notifications.class);
                startActivity(intent);

        });

        btnAutentication = (LinearLayout) findViewById(R.id.autentication);

        btnAutentication.setOnClickListener(view ->  {
            Intent intent = new Intent(Settings.this, Settings_Autentication.class);
            startActivity(intent);

        });

        btnInterface = (LinearLayout) findViewById(R.id.s_interface);

        btnInterface.setOnClickListener(view ->{
            Intent intent = new Intent(Settings.this, Settings_Interface.class);
            startActivity(intent);
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        Intent intent = new Intent(Settings.this, Home.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
        return true;
    }
}