package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashMap;

public class Register extends AppCompatActivity {

    EditText etNames, etSurnames, etBirthdate;
    Button btnNext;
    Calendar calendar;
    TextView tvNamesMsg, tvSurnamesMsg, tvBirthdateMsg;

    public HashMap checkFields(String names, String surnames, String birthdate) {
        HashMap<String, String> msgs = new HashMap<>();

        if (names.isEmpty() || names.length() < 3 || names.length() > 50) {
            msgs.put("names", "No debe ser vacío, mínimo 3 caracteres y máximo 50");
        }

        if (surnames.isEmpty() || surnames.length() < 3 || surnames.length() > 50) {
            msgs.put("surnames", "No debe ser vacío, mínimo 3 caracteres y máximo 50");
        }

        if (birthdate.isEmpty()) {
            msgs.put("birthdate", "No debe ser vacío");
        }

        return msgs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        etNames = findViewById(R.id.names);
        etSurnames = findViewById(R.id.surnames);
        etBirthdate = findViewById(R.id.birthdate);
        btnNext = findViewById(R.id.next);
        tvNamesMsg = findViewById(R.id.namesMsg);
        tvSurnamesMsg = findViewById(R.id.surnamesMsg);
        tvBirthdateMsg = findViewById(R.id.birthdateMsg);

        calendar = Calendar.getInstance();

        etBirthdate.setOnFocusChangeListener((v, hasFocus)  -> {
            if (!hasFocus){
                return ;
            }
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(Register.this,
                    (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                        String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                        etBirthdate.setText(date);
                    }, year, month, day);
            datePickerDialog.show();
        });

        btnNext.setOnClickListener(view -> {

            String name = etNames.getText().toString();
            String surname = etSurnames.getText().toString();
            String birthdate = etBirthdate.getText().toString();

            HashMap msgs = checkFields(name, surname, birthdate);
            if (!msgs.isEmpty()) {
                if (msgs.containsKey("names")) {
                    tvNamesMsg.setText((CharSequence) msgs.get("names"));

                } else tvNamesMsg.setText("");

                if (msgs.containsKey("surnames")) {
                    tvSurnamesMsg.setText((CharSequence) msgs.get("surnames"));

                } else tvSurnamesMsg.setText("");

                if (msgs.containsKey("birthdate")) {
                    tvBirthdateMsg.setText((CharSequence) msgs.get("birthdate"));

                } else tvBirthdateMsg.setText("");

            } else {
                Intent intent = new Intent(Register.this, Register1.class);
                intent.putExtra("name", name);
                intent.putExtra("surname", surname);
                intent.putExtra("birthdate", birthdate);
                startActivity(intent);
            }
        });

    }
}
