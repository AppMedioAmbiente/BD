package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.firelogin.ui.events.EventsFragment;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Create_Event extends AppCompatActivity implements View.OnClickListener {

    Calendar calendar;
    EditText etEventName, etEventDescrip, etEventLat, etEventLong, etEventDate, etEventEndDate, etEventMaterials, etEventMinVolun;
    Button btnCancel, btnSaveEvent;
    Spinner spEventType;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebase = FirebaseAuth.getInstance();
    private final FirebaseUser user = firebase.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_event);

        etEventName = findViewById(R.id.etEventName);
        spEventType = findViewById(R.id.spEventType);
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

        spEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventType selectedType = (EventType) parent.getItemAtPosition(position);

                Integer typeId = selectedType.getId();
                String typeName = selectedType.getName();
                showToastAlert("El id es :"+typeId.toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        etEventDescrip.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (etEventDescrip.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
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
                if (etEventMaterials.hasFocus()) {
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    switch (event.getAction() & MotionEvent.ACTION_MASK) {
                        case MotionEvent.ACTION_SCROLL:
                            v.getParent().requestDisallowInterceptTouchEvent(false);
                            return true;
                    }
                }
                return false;
            }
        });

        List<EventType> types = new ArrayList<>();
        types.add(new EventType(-1, ""));

        db.collection("event_type").get().addOnSuccessListener(documentSnapshot -> {
            documentSnapshot.getDocuments().forEach(type -> {
                types.add(new EventType(type.get("id_type", Integer.TYPE), type.getString("type")));
            });

            ArrayAdapter<EventType> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spEventType.setAdapter(adapter);
        });


        btnSaveEvent.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (checkFields(etEventName, spEventType, etEventDescrip, etEventLat, etEventLong, etEventDate, etEventEndDate, etEventMaterials,
                etEventMinVolun)) {

            EventType selectedType = (EventType) spEventType.getSelectedItem();
            try {
                Map<String, Object> data = new HashMap<>();
                data.put("event_name", etEventName.getText().toString().trim());
                data.put("type", db.collection("event_type").document(selectedType.getId().toString()));
                data.put("description", etEventDescrip.getText().toString().trim());
                data.put("location", new GeoPoint(Double.parseDouble(etEventLat.getText().toString()), Double.parseDouble(etEventLong.getText().toString())));
                data.put("date", new Timestamp(sdf.parse(etEventDate.getText().toString())));
                data.put("end_date", new Timestamp(sdf.parse(etEventEndDate.getText().toString())));
                data.put("materials", etEventMaterials.getText().toString().trim());
                data.put("min_volunteers", Integer.parseInt(etEventMinVolun.getText().toString()));
                data.put("status", db.collection("event_status").document("1"));
                data.put("organizer", db.collection("usuarios").document(user.getUid()));

                db.collection("eventos").add(data).addOnSuccessListener(s -> {

                            Map<String, Object> dataEvUser = new HashMap<>();
                            dataEvUser.put("id_event", db.collection("eventos").document(s.getId()));
                            dataEvUser.put("id_usuario", db.collection("usuarios").document(user.getUid()));
                            db.collection("event_has_usuarios").add(dataEvUser).addOnFailureListener(f1 -> {
                                Log.d("Error al guardar el evento y usuario: ", f1.getMessage());
                            });

                            Intent intent = new Intent(this, Home.class);
                            intent.putExtra("fragmentToLoad", "fragment_events");
                            startActivity(intent);
                        })
                        .addOnFailureListener(f -> {
                            showToastAlert("Error al guardar los datos");
                            Log.d("Error: ", f.getMessage());
                        });

            } catch (ParseException ex) {
                showToastAlert("Error al guardar la fecha");
                Log.d("Error parse: ", ex.getMessage());
            }

        } else {
            showToastAlert("Debe llenar correctamente todos los campos");
        }
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

                }, year, month, day);
        datePickerDialog.show();
    }

    public void showTimePicker(Calendar calendar, EditText etDate) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(Create_Event.this,
                (TimePicker view2, int hourOfDay, int minuteOfHour) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    calendar.set(Calendar.MINUTE, minuteOfHour);
                    updateEtDate(calendar, etDate);
                }, hour, minute, false);
        timePickerDialog.show();
    }

    public void updateEtDate(Calendar calendar, EditText etDate) {
        etDate.setText(sdf.format(calendar.getTime()));
    }

    public boolean checkFields(EditText name, Spinner type, EditText descrip, EditText latitude, EditText longitude, EditText date,
                               EditText endDate, EditText materials, EditText volun) {

        if (name.getText().toString().trim().isEmpty()) return false;

        if (type.getSelectedItem().toString().isEmpty()) return false;

        if (descrip.getText().toString().trim().isEmpty()) return false;

        if (latitude.getText().toString().trim().isEmpty() || Double.parseDouble(latitude.getText().toString()) > 90
                || Double.parseDouble(latitude.getText().toString()) < -90) return false;

        if (longitude.getText().toString().trim().isEmpty() || Double.parseDouble(longitude.getText().toString()) > 180
                || Double.parseDouble(longitude.getText().toString()) < -180) return false;

        if (materials.getText().toString().trim().isEmpty()) return false;

        if (volun.getText().toString().trim().isEmpty()) return false;

        if (!isDatetimeValid(date.getText().toString())) return false;

        if (!isDatetimeValid(endDate.getText().toString())) return false;

        return true;
    }

    public boolean isDatetimeValid(String datetime) {
        sdf.setLenient(false);

        try {
            Date date = sdf.parse(datetime);
            return true;
        } catch (ParseException ex) {
            return false;
        }
    }

    protected void showToastAlert(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}