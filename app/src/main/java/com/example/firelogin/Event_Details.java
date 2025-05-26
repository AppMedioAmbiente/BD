package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.SetOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Event_Details extends AppCompatActivity implements View.OnClickListener {

    Calendar calendar;
    EditText eventName, eventOrganizer, eventDescrip, eventLat, eventLong, eventDate, eventEndDate, eventMaterials, eventMinVolun, eventNumPpl;
    Spinner eventType, eventStatus;
    Button btnLeave, btnJoin, btnModify, btnLeaveEvent;
    FloatingActionButton btnDelete;
    String idEvent, fromFragment;
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
        fromFragment = getIntent().getStringExtra("fromFragment");

        eventName = findViewById(R.id.dtEventName);
        eventOrganizer = findViewById(R.id.dtEventOrganizer);
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
        btnLeaveEvent = findViewById(R.id.btnLeaveEvent);

        btnLeave.setOnClickListener(v -> {
            if (fromFragment.equals("Events")) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("fragmentToLoad", "fragment_events");
                startActivity(intent);

            } else if (fromFragment.equals("MyEvents")) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("fragmentToLoad", "fragment_my_events");
                startActivity(intent);

            } else if (fromFragment.equals("History")) {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("fragmentToLoad", "fragment_history");
                startActivity(intent);
            }
        });


        List<EventType> types = new ArrayList<>();
        types.add(new EventType(-1, ""));

        db.collection("event_type").get().addOnSuccessListener(documentSnapshot -> {
            documentSnapshot.getDocuments().forEach(type -> {
                types.add(new EventType(type.get("id_type", Integer.TYPE), type.getString("type")));
            });

            ArrayAdapter<EventType> adapterType = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
            adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventType.setAdapter(adapterType);

            db.collection("eventos").document(idEvent).get().addOnSuccessListener(doc -> {
                for (int i = 0; i < types.size(); i++) {
                    if (types.get(i).getId() == Integer.parseInt(doc.getDocumentReference("type").getId()))
                        eventType.setSelection(i);
                }
            });
        });

        List<EventType> status = new ArrayList<>();
        status.add(new EventType(-1, ""));

        db.collection("event_status").get().addOnSuccessListener(documentSnapshot -> {
            documentSnapshot.getDocuments().forEach(stat -> {
                status.add(new EventType(stat.get("id_status", Integer.TYPE), stat.getString("status")));
            });

            ArrayAdapter<EventType> adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, status);
            adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            eventStatus.setAdapter(adapterStatus);

            db.collection("eventos").document(idEvent).get().addOnSuccessListener(doc -> {
                for (int i = 0; i < status.size(); i++) {
                    if (status.get(i).getId() == Integer.parseInt(doc.getDocumentReference("status").getId()))
                        eventStatus.setSelection(i);
                }
            });
        });


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
            eventLat.setText(documentSnapshot.getGeoPoint("location").getLatitude() + "");
            eventLong.setText(documentSnapshot.getGeoPoint("location").getLongitude() + "");
            eventDate.setText(sdf.format(documentSnapshot.getDate("date")));
            eventEndDate.setText(sdf.format(documentSnapshot.getDate("end_date")));
            eventMaterials.setText(documentSnapshot.getString("materials"));
            eventMinVolun.setText(documentSnapshot.get("min_volunteers", Integer.TYPE).toString());

            /*status.forEach(st -> {
                if (st.getId().equals(Integer.parseInt(documentSnapshot.getDocumentReference("status").getId())))
                    eventStatus.setSelection(st.getId());
            });*/

            DocumentReference organizerDoc = documentSnapshot.getDocumentReference("organizer");
            organizerDoc.get().addOnSuccessListener(doc -> {
                eventOrganizer.setText(doc.getString("name"));
            });
        });


        db.collection("event_has_usuarios").whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                .get().addOnSuccessListener(queryDocs -> {
                    eventNumPpl.setText(queryDocs.size() + "");
                });


        eventDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            createEvent.showDatePicker(eventDate, this);
        });

        eventEndDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            createEvent.showDatePicker(eventEndDate, this);
        });

        btnDelete.setOnClickListener(view -> {
            db.collection("event_has_usuarios").whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                    .get().addOnSuccessListener(documentSnapshots -> {
                        documentSnapshots.forEach(doc -> {
                            db.collection("event_has_usuarios").document(doc.getId()).delete()
                                    .addOnFailureListener(fail -> {
                                        Log.d("Error al eliminar la referencia: ", fail.getMessage());
                                    });
                        });
                    });

            db.collection("eventos").document(idEvent).delete().addOnSuccessListener(success -> {
                Intent intent = new Intent(this, Home.class);
                intent.putExtra("fragmentToLoad", "fragment_events");
                startActivity(intent);

            }).addOnFailureListener(failure -> {
                showToastAlert("Error al eliminar el evento ");
                Log.d("Erroral eliminar el evento: ", failure.getMessage());
            });
        });

        btnModify.setOnClickListener(this);

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

        if (fromFragment.equals("History")) {
            btnJoin.setVisibility(View.GONE);
            btnLeaveEvent.setVisibility(View.GONE);
        }

        db.collection("event_has_usuarios")
                .whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                .whereEqualTo("id_usuario", db.collection("usuarios").document(user.getUid()))
                .get().addOnSuccessListener(querySnapshot -> {

                    if (querySnapshot.isEmpty()) {
                        btnLeaveEvent.setEnabled(false);
                        btnJoin.setEnabled(true);

                    } else {
                        btnJoin.setEnabled(false);
                        btnLeaveEvent.setEnabled(true);
                    }
                });

        btnJoin.setOnClickListener(v -> {
            Map<String, Object> data = new HashMap<>();
            data.put("id_event", db.collection("eventos").document(idEvent));
            data.put("id_usuario", db.collection("usuarios").document(user.getUid()));

            db.collection("event_has_usuarios").add(data).addOnSuccessListener(s -> {
                        btnJoin.setEnabled(false);
                        btnLeaveEvent.setEnabled(true);
                        int numPpl = Integer.parseInt(eventNumPpl.getText().toString()) + 1;
                        eventNumPpl.setText(numPpl + "");
                    })
                    .addOnFailureListener(f -> {
                        showToastAlert("Error al unirte al evento");
                        Log.d("Error al unirse al evento: ", f.getMessage());
                    });
        });

        btnLeaveEvent.setOnClickListener(v -> {
            db.collection("event_has_usuarios")
                    .whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                    .whereEqualTo("id_usuario", db.collection("usuarios").document(user.getUid()))
                    .get().addOnSuccessListener(querySnapshot -> {

                        querySnapshot.forEach(query -> {
                            db.collection("event_has_usuarios").document(query.getId()).delete().addOnSuccessListener(s -> {
                                btnLeaveEvent.setEnabled(false);
                                btnJoin.setEnabled(true);
                                int numPpl = Integer.parseInt(eventNumPpl.getText().toString()) - 1;
                                eventNumPpl.setText(numPpl + "");

                            }).addOnFailureListener(f -> {
                                showToastAlert("Error al abandonar al evento");
                                Log.d("Error al abandonar al evento: ", f.getMessage());
                            });
                        });

                    });
        });
    }

    public void organizerView() {
        btnJoin.setVisibility(View.GONE);
        btnLeaveEvent.setVisibility(View.GONE);

        if (fromFragment.equals("History")) {
            btnModify.setVisibility(View.GONE);
            btnDelete.setVisibility(View.GONE);
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

    @Override
    public void onClick(View v) {
        if (createEvent.checkFields(eventName, eventType, eventDescrip, eventLat, eventLong, eventDate, eventEndDate, eventMaterials, eventMinVolun)
                && !eventStatus.getSelectedItem().toString().isEmpty()) {

            EventType selectedType = (EventType) eventType.getSelectedItem();
            EventType selectedStatus = (EventType) eventStatus.getSelectedItem();
            try {
                Map<String, Object> newData = new HashMap<>();
                newData.put("event_name", eventName.getText().toString().trim());
                newData.put("type", db.collection("event_type").document(selectedType.getId().toString()));
                newData.put("status", db.collection("event_status").document(selectedStatus.getId().toString()));
                newData.put("description", eventDescrip.getText().toString().trim());
                newData.put("location", new GeoPoint(Double.parseDouble(eventLat.getText().toString()), Double.parseDouble(eventLong.getText().toString())));
                newData.put("date", new Timestamp(sdf.parse(eventDate.getText().toString())));
                newData.put("end_date", new Timestamp(sdf.parse(eventEndDate.getText().toString())));
                newData.put("materials", eventMaterials.getText().toString().trim());
                newData.put("min_volunteers", Integer.parseInt(eventMinVolun.getText().toString()));
                //newData.put("organizer", db.collection("usuarios").document(user.getUid()));

                db.collection("eventos").document(idEvent).update(newData).addOnSuccessListener(sucs -> {
                    Intent intent = new Intent(this, Home.class);
                    intent.putExtra("fragmentToLoad", "fragment_events");
                    startActivity(intent);

                }).addOnFailureListener(fail -> {
                    showToastAlert("Error al modificar el evento");
                    Log.d("Error al modificar el evento: ", fail.getMessage());
                });

            } catch (ParseException ex) {
                showToastAlert("Error al guardar la fecha");
                Log.d("Error parse: ", ex.getMessage());
            }

        } else {
            showToastAlert("Debe llenar correctamente todos los campos");
        }
    }
}