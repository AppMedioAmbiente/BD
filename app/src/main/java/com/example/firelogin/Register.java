package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.DatePicker;
import android.app.DatePickerDialog;
import java.util.Calendar;

public class Register extends AppCompatActivity {

    EditText etNames, etSurnames, etBirthdate;
    Button btnNext;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        etNames = findViewById(R.id.names);
        etSurnames = findViewById(R.id.surnames);
        etBirthdate = findViewById(R.id.birthdate);
        btnNext = findViewById(R.id.next);

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


            Intent intent = new Intent(Register.this,Register1.class);
            intent.putExtra("name", name);
            intent.putExtra("surname", surname);
            intent.putExtra("birthdate", birthdate);
            startActivity(intent);
        });

    }
}
