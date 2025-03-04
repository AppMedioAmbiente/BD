package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;


public class Login extends AppCompatActivity {

    EditText etContact, etPassword;
    Button btn_redirect, btn_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etContact = findViewById(R.id.contact);
        etPassword = findViewById(R.id.password);
        btn_redirect = findViewById(R.id.Rsing_up);

        btn_redirect.setOnClickListener(view -> {

            Intent intent = new Intent(Login.this,Register.class);
            startActivity(intent);
        });

        btn_log = findViewById(R.id.Send);

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String contact = etContact.getText().toString();
                final String password = etPassword.getText().toString();
            }
        });

    }
}
