package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class Home extends AppCompatActivity {

    Spinner spEvents;
    String[] eventsOpt;
    ArrayAdapter<String> adapter;
    Button btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        Intent intent = getIntent();

        btnProfile = findViewById(R.id.profile);
        spEvents = findViewById(R.id.events);

        eventsOpt = getResources().getStringArray(R.array.spinner_events);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, eventsOpt);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEvents.setAdapter(adapter);

        spEvents.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) return;
                String selection = parent.getItemAtPosition(position).toString();
                System.out.println("Selección: "+selection);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spEvents.setSelection(0, false);

        btnProfile.setOnClickListener(view -> {
            Intent intentUser = new Intent(Home.this, Usuario.class);
            intentUser.putExtra("name", intent.getStringExtra("name"));
            intentUser.putExtra("surname", intent.getStringExtra("surname"));
            intentUser.putExtra("birthdate", intent.getStringExtra("birthdate"));
            intentUser.putExtra("contact", intent.getStringExtra("contact"));
            startActivity(intentUser);
        });

    }
}