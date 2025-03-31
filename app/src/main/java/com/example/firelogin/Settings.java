package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.HashMap;

public class Settings extends AppCompatActivity {

    LinearLayout btnNotification;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        btnNotification = (LinearLayout) findViewById(R.id.notification);

        btnNotification.setOnClickListener(view ->  {
                Intent intent = new Intent(Settings.this, Settings_Notifications.class);
                startActivity(intent);

        });
    }
}