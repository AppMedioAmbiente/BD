package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

public class Usuario extends AppCompatActivity {

    TextView tvNames, tvSurnames, tvBirthdate, tvContact;

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario);

        tvNames = (TextView) findViewById(R.id.names);
        tvSurnames = (TextView) findViewById(R.id.surnames);
        tvBirthdate = (TextView) findViewById(R.id.birthdate);
        tvContact = (TextView) findViewById(R.id.contact);

        Intent intent = getIntent();
        String names = intent.getStringExtra("names");
        String surnames = intent.getStringExtra("surnames");
        String birthdate = intent.getStringExtra("birthdate");
        String contact = intent.getStringExtra("contact");

        tvNames.setText(names);
        tvSurnames.setText(surnames);
        tvBirthdate.setText(birthdate);
        tvContact.setText(contact);


    }
}
