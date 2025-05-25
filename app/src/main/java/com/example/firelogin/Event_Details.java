package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Event_Details extends AppCompatActivity {

    Calendar calendar;
    EditText eventName, eventDescrip, eventLat, eventLong, eventDate, eventEndDate, eventMaterials, eventMinVolun, eventNumPpl;
    Spinner eventType, eventStatus;
    Button btnLeave, btnJoin, btnModify;
    FloatingActionButton btnDelete;
    String idEvent;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebase = FirebaseAuth.getInstance();
    private final FirebaseUser user = firebase.getCurrentUser();
    Create_Event createEvent = new Create_Event();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details);

        Toolbar tbCreateEvent = findViewById(R.id.tbEventDetails);
        setSupportActionBar(tbCreateEvent);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        idEvent = getIntent().getStringExtra("id_event");

        eventName = findViewById(R.id.dtEventName);
        eventDescrip = findViewById(R.id.dtEventDescrip);
        eventLat = findViewById(R.id.dtEventLat);
        eventLong = findViewById(R.id.dtEventLong);
        eventDate = findViewById(R.id.dtEventDate);
        eventEndDate = findViewById(R.id.dtEventEndDate);
        eventMaterials = findViewById(R.id.dtEventMaterials);
        eventMinVolun = findViewById(R.id.dtEventMinVolun);
        eventNumPpl = findViewById(R.id.dtEventNumPpl);
        eventType = findViewById(R.id.dtEventType);
        eventStatus = findViewById(R.id.dtEventStatus);
        btnDelete = findViewById(R.id.deleteEvent);
        btnJoin = findViewById(R.id.btnJoin);
        btnLeave = findViewById(R.id.btnLeave);
        btnModify = findViewById(R.id.btnModifyEvent);

        btnLeave.setOnClickListener(v -> {onSupportNavigateUp();});

        db.collection("eventos").document(idEvent).get().addOnSuccessListener(documentSnapshot -> {
           documentSnapshot.getDocumentReference("organizer").get().addOnSuccessListener(doc -> {
               if (doc.getId().equals(user.getUid())) {
                   organizerView();
               } else {
                   userView();
               }
           });

           eventName.setText(documentSnapshot.getString("event_name"));
           eventDescrip.setText(documentSnapshot.getString("description"));
           eventLat.setText(documentSnapshot.getGeoPoint("location").getLatitude()+"");
           eventLong.setText(documentSnapshot.getGeoPoint("location").getLongitude()+"");
           eventDate.setText(sdf.format(documentSnapshot.getDate("date")));
           eventEndDate.setText(sdf.format(documentSnapshot.getDate("end_date")));
           eventMaterials.setText(documentSnapshot.getString("materials"));
           eventMinVolun.setText(documentSnapshot.get("min_volunteers", Integer.TYPE).toString());
        });

        eventDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            createEvent.showDatePicker(eventDate);
        });

        eventEndDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            createEvent.showDatePicker(eventEndDate);
        });

    }

    public void userView() {
        eventName.setEnabled(false);
        eventDescrip.setEnabled(false);
        eventLat.setEnabled(false);
        eventLong.setEnabled(false);
        eventDate.setEnabled(false);
        eventEndDate.setEnabled(false);
        eventMaterials.setEnabled(false);
        eventMinVolun.setEnabled(false);
        eventType.setEnabled(false);
        eventStatus.setEnabled(false);
        btnDelete.setVisibility(View.GONE);
        btnModify.setVisibility(View.GONE);

        db.collection("event_has_usuarios")
                .whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                .whereEqualTo("id_usuario", db.collection("usuarios").document(user.getUid()))
                .get().addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        btnJoin.setOnClickListener(v -> {
                            Map<String, Object> data = new HashMap<>();
                            data.put("id_event", db.collection("eventos").document(idEvent));
                            data.put("id_usuario", db.collection("usuarios").document(user.getUid()));

                            db.collection("event_has_usuarios").add(data).addOnSuccessListener(s ->{
                                        btnJoin.setEnabled(false);
                                    })
                                    .addOnFailureListener(f -> {
                                        showToastAlert("Error al unirte al evento");
                                        Log.d("Error al unirse al evento: ", f.getMessage());
                                    });
                        });

                    } else btnJoin.setEnabled(false);

                });
    }

    public void organizerView() {
        btnJoin.setVisibility(View.GONE);
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