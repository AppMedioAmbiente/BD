package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import java.util.HashMap;

public class Settings extends AppCompatActivity {

    LinearLayout btnPersonal_data;
    LinearLayout btnNotification;
    LinearLayout btnAutentication;
    LinearLayout btnInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

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
}