package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Create_Event extends AppCompatActivity {

    Calendar calendar;
    EditText etEventName, etEventType, etEventDescrip, etEventLat, etEventLong, etEventDate, etEventEndDate, etEventMaterials, etEventMinVolun;
    Button btnCancel, btnSaveEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        etEventName = findViewById(R.id.etEventName);
        etEventType = findViewById(R.id.etEventType);
        etEventDescrip = findViewById(R.id.etEventDescrip);
        etEventLat = findViewById(R.id.etEventLat);
        etEventLong = findViewById(R.id.etEventLong);
        etEventDate = findViewById(R.id.etEventDate);
        etEventEndDate = findViewById(R.id.etEventEndDate);
        etEventMaterials = findViewById(R.id.etEventMaterials);
        etEventMinVolun = findViewById(R.id.etEventMinVolun);
        btnSaveEvent = findViewById(R.id.btnSaveEvent);
        btnCancel = findViewById(R.id.btnCancel);

        btnCancel.setOnClickListener(view -> onSupportNavigateUp());

        Toolbar tbCreateEvent = findViewById(R.id.tbCreateEvent);
        setSupportActionBar(tbCreateEvent);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        etEventDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            showDatePicker(etEventDate);
        });

        etEventEndDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            showDatePicker(etEventEndDate);
        });

        etEventDescrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (etEventDescrip.hasFocus()){
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        etEventMaterials.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (etEventMaterials.hasFocus()){
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK){
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

    }

    public void showDatePicker(EditText etDate) {
        calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(Create_Event.this,
                (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                    Calendar calendarSelected = Calendar.getInstance();
                    calendarSelected.set(selectedYear, selectedMonth, selectedDay);
                    showTimePicker(calendarSelected, etDate);
                    //String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    //etEventDate.setText(date);

                }, year, month, day);
        datePickerDialog.show();
    }

    public void showTimePicker(Calendar calendar, EditText etDate){
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Create_Event.this,
                (TimePicker view2, int hourOfDay, int minuteOfHour) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfHour);
                    updateEtEventDate(calendar, etDate);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    public void updateEtEventDate(Calendar calendar, EditText etDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        etDate.setText(sdf.format(calendar.getTime()));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}