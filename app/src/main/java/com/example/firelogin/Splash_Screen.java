package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedIntanceStarte) {
        super.onCreate(savedIntanceStarte);
        setContentView(R.layout.splash_screen);

        new Handler().postDelayed(new  Runnable() {
            @Override
            public void run() {
                Intent intent = new  Intent (Splash_Screen.this,Home.class);
                startActivity(intent);
                finish();
            }
            },2000);
    }
}
