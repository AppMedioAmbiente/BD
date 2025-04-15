package com.example.firelogin;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Settings_DA extends AppCompatActivity {

    Button btnDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_deleteaccount);

        btnDelete = findViewById(R.id.delete);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        if(user != null){

            btnDelete.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                }
            }
            );

        }
    }

}
