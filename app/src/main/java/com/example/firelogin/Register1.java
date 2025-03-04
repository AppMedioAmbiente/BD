package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;

public class Register1 extends AppCompatActivity implements View.OnClickListener {

    EditText etContact, etPassword;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);

        etContact = findViewById(R.id.contact);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.send);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String birthdateString = getIntent().getStringExtra("birthdate");

        Date birthdate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            birthdate = dateFormat.parse(birthdateString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        String contact = etContact.getText().toString();
        String password = etPassword.getText().toString();
    }
}

