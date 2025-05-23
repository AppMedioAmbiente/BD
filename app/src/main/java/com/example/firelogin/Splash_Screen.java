package com.example.firelogin;

import static androidx.core.content.ContextCompat.startActivity;
import static com.example.firelogin.StaticFunctions.print;
import static com.example.firelogin.StaticFunctions.showHome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firelogin.register.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedIntanceStarte) {
        super.onCreate(savedIntanceStarte);
        setContentView(R.layout.splash_screen);


        new Handler().postDelayed(new  Runnable() {
            @Override
            public void run() {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

                if (currentUser != null && currentUser.isEmailVerified()){

//                    showHome(Splash_Screen.this);
                    print("showHome in Splash_Screen.this");
                    Intent intent = new  Intent (Splash_Screen.this,Home.class);
                    startActivity(intent);
                } else{
                    Intent intent = new  Intent (Splash_Screen.this, Login.class);
                    startActivity(intent);
                }

                finish();
            }},2000);
    }
}
