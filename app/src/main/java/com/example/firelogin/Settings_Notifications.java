package com.example.firelogin;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class Settings_Notifications extends AppCompatActivity {

    Button cancelNotifBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_notifications);

        Toolbar tbNotifSettings = findViewById(R.id.tbNotifSettings);
        setSupportActionBar(tbNotifSettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cancelNotifBtn = findViewById(R.id.cancelNotifBtn);
        cancelNotifBtn.setOnClickListener(view->{onSupportNavigateUp();});
    }

    @Override
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

}
