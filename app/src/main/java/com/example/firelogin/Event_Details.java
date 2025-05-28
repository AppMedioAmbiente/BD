package com.example.firelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Event_Details extends AppCompatActivity implements View.OnClickListener {

    private MapView map;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private Marker lastMarker;
    Calendar calendar;
    EditText eventName, eventOrganizer, eventDescrip, eventLat, eventLong, eventDate, eventEndDate, eventMaterials, eventMinVolun, eventNumPpl;
    Spinner eventType, eventStatus;
    Button btnLeave, btnJoin, btnModify, btnLeaveEvent, waypoint;
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
        //eventLat = findViewById(R.id.dtEventLat);
        //eventLong = findViewById(R.id.dtEventLong);
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
        waypoint = findViewById(R.id.waypointBtn);

        btnLeave.setOnClickListener(v -> {
            Intent intent=new Intent(this, Home.class);
            switch(fromFragment){
                case "Events":
//                    intent = new Intent(this, Home.class);
                    intent.putExtra("fragmentToLoad", "fragment_events");
//                    startActivity(intent);
//                    finish();
                    break;
                case "MyEvents":
//                    intent = new Intent(this, Home.class);
                    intent.putExtra("fragmentToLoad", "fragment_my_events");
//                    startActivity(intent);
//                    finish();
                    break;
                case "History":
//                    intent = new Intent(this, Home.class);
                    intent.putExtra("fragmentToLoad", "fragment_history");
//                    startActivity(intent);
//                    finish();
                    break;
                case "Calendar":
//                    intent = new Intent(this, Home.class);
                    intent.putExtra("fragmentToLoad", "fragment_calendar");
//                    startActivity(intent);
//                    finish();
                    break;
            }
            startActivity(intent);
            finish();
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
            //eventLat.setText(documentSnapshot.getGeoPoint("location").getLatitude() + "");
            //eventLong .setText(documentSnapshot.getGeoPoint("location").getLongitude() + "");
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

        if (isStoragePermissionGranted()) {
            setupMap();
            setupSearch();
        }

        findViewById(R.id.mapView).setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    break;
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }
            return false;
        });

    }

    public void userView() {
        eventName.setEnabled(false);
        eventDescrip.setEnabled(false);
        //eventLat.setEnabled(false);
        //eventLong.setEnabled(false);
        eventDate.setEnabled(false);
        eventEndDate.setEnabled(false);
        eventMaterials.setEnabled(false);
        eventMinVolun.setEnabled(false);
        eventType.setEnabled(false);
        eventStatus.setEnabled(false);
        btnDelete.setVisibility(View.GONE);
        btnModify.setVisibility(View.GONE);
        waypoint.setVisibility(View.GONE);

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

        db.collection("eventos").document(idEvent).get().addOnSuccessListener(doc -> {
           if (doc.getDocumentReference("status").getId().equals("3") || doc.getDocumentReference("status").getId().equals("4")) {
               btnModify.setVisibility(View.GONE);
               btnDelete.setVisibility(View.GONE);
           }
        });

        btnModify.setOnClickListener(this);

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

            db.collection("geopoint").whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                    .get().addOnSuccessListener(docsGeo -> {
                        docsGeo.forEach(doc -> {
                            db.collection("geopoint").document(doc.getId()).delete()
                                    .addOnFailureListener(f -> {
                                        Log.d("Error al eliminar el geopoint: ", f.getMessage());
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
        if (createEvent.checkFields(eventName, eventType, eventDescrip, eventDate, eventEndDate, eventMaterials, eventMinVolun)
                && !eventStatus.getSelectedItem().toString().isEmpty()) {

            EventType selectedType = (EventType) eventType.getSelectedItem();
            EventType selectedStatus = (EventType) eventStatus.getSelectedItem();
            try {
                Map<String, Object> newData = new HashMap<>();
                newData.put("event_name", eventName.getText().toString().trim());
                newData.put("type", db.collection("event_type").document(selectedType.getId().toString()));
                newData.put("status", db.collection("event_status").document(selectedStatus.getId().toString()));
                newData.put("description", eventDescrip.getText().toString().trim());
                //newData.put("location", new GeoPoint(Double.parseDouble(eventLat.getText().toString()), Double.parseDouble(eventLong.getText().toString())));
                newData.put("date", new Timestamp(sdf.parse(eventDate.getText().toString())));
                newData.put("end_date", new Timestamp(sdf.parse(eventEndDate.getText().toString())));
                newData.put("materials", eventMaterials.getText().toString().trim());
                newData.put("min_volunteers", Integer.parseInt(eventMinVolun.getText().toString()));

                com.google.firebase.firestore.GeoPoint firestoreGeoPoint = null;
                if (lastMarker != null) {
                    org.osmdroid.util.GeoPoint point = lastMarker.getPosition();
                    double latitude = point.getLatitude();
                    double longitude = point.getLongitude();

                    com.google.firebase.firestore.GeoPoint finalFirestoreGeoPoint =
                            new com.google.firebase.firestore.GeoPoint(latitude, longitude);
                    newData.put("location", finalFirestoreGeoPoint);

                    Map<String, Object> geoData = new HashMap<>();
                    geoData.put("location", finalFirestoreGeoPoint);
                    //geoData.put("id_event", sucs);

                    db.collection("geopoint").whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                            .get().addOnSuccessListener(docGeo -> {
                                docGeo.forEach(doc -> {
                                    db.collection("geopoint").document(doc.getId()).update(geoData)
                                            .addOnSuccessListener(gRef -> Log.d("Firebase", "GeoPoint guardado correctamente"))
                                            .addOnFailureListener(e -> Log.e("Firebase", "Error al guardar geopoint", e));


                                });
                            });
                }

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

    public boolean isStoragePermissionGranted() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
            Log.w(TAG, "se aceptaron permisos");

            return true;
        } else {
            Log.w(TAG, "valio madre");

        }
        Log.w(TAG, "Se aceptaron permisos");

        return true;
    }

    private MapEventsOverlay currentEventOverlay = null;
    private boolean isWaitingForTap = false;

    private void setupMap() {

        map = findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getOverlayManager().add(new CopyrightOverlay(this));


        mapController = map.getController();
        mapController.setZoom(18);

        map.setMultiTouchControls(true);

        Log.w(TAG, "ando aca");


        if (isStoragePermissionGranted() == true) {

            MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(this), map);
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.enableFollowLocation();
            map.getOverlays().add(mLocationOverlay);

            mLocationOverlay.runOnFirstFix(() -> {
                org.osmdroid.util.GeoPoint myLocation = mLocationOverlay.getMyLocation();
                Log.d(TAG, "MyLocation: " + myLocation);
                if (myLocation != null) {
                    this.runOnUiThread(() -> {
                        mapController.setCenter(myLocation);

                    });
                } else {
                    Log.w(TAG, "No se puede optener la ubicación");
                }

            });
        }

        db.collection("geopoint").whereEqualTo("id_event", db.collection("eventos").document(idEvent))
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        com.google.firebase.firestore.GeoPoint geoPoint = document.getGeoPoint("location");
                        if (geoPoint != null) {
                            double lat = geoPoint.getLatitude();
                            double lon = geoPoint.getLongitude();

                            org.osmdroid.util.GeoPoint punto = new org.osmdroid.util.GeoPoint(lat, lon);
                            agregarMarcador(punto);
                        }

                    }
                })
                .addOnFailureListener(e -> Log.e("Firebase", "Error al obtener los geopoints", e));


        waypoint.setOnClickListener(v -> {
            Log.w(TAG, "si detecto el clic en el boton");

            if (isWaitingForTap) return;

            if (currentEventOverlay != null) {
                map.getOverlays().remove(currentEventOverlay);
                map.invalidate();
            }

            isWaitingForTap = true;

            MapEventsReceiver mReceive = new MapEventsReceiver() {

                @Override
                public boolean singleTapConfirmedHelper(org.osmdroid.util.GeoPoint p) {
                    org.osmdroid.util.GeoPoint startPoint = new org.osmdroid.util.GeoPoint(p.getLatitude(), p.getLongitude());

                    lastMarker = new Marker(map);
                    lastMarker.setPosition(startPoint);
                    lastMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    lastMarker.setTitle("Waypoint");
                    map.getOverlays().add(lastMarker);
                    Marker waypoint = new Marker(map);
                    waypoint.setPosition(startPoint);
                    waypoint.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
                    map.getOverlays().add(waypoint);
                    map.invalidate();

                    Log.w(TAG, "Clic detectado en: " + p.getLatitude() + ", " + p.getLongitude());

                    map.getOverlays().remove(currentEventOverlay);
                    currentEventOverlay = null;
                    isWaitingForTap = false;

                    return true;
                }

                @Override
                public boolean longPressHelper(org.osmdroid.util.GeoPoint p) {
                    return true;
                }
            };

            currentEventOverlay = new MapEventsOverlay(mReceive);
            map.getOverlays().add(currentEventOverlay);
            map.invalidate();
        });

    }

    public void agregarMarcador(org.osmdroid.util.GeoPoint punto) {
        Marker waypoint = new Marker(map);
        waypoint.setPosition(punto);
        waypoint.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        waypoint.setTitle("Ubicación guardada");

        map.getOverlays().add(waypoint);
        map.invalidate();
    }

    private void setupSearch() {

        AutoCompleteTextView searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchSuggestions(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        searchEditText.setOnItemClickListener((parent, view, position, id) -> {
            String selectedText = (String) parent.getItemAtPosition(position);
            searchLocation(selectedText);
        });

        searchButton.setOnClickListener(v -> {
            String locationName = searchEditText.getText().toString().trim();
            if (!locationName.isEmpty()) {
                searchLocation(locationName);
            }
        });
    }

    private void fetchSuggestions(String locationName) {
        new Thread(() -> {
            try {
                String urlStr = "https://photon.komoot.io/api/?q=" +
                        URLEncoder.encode(locationName, "UTF-8") + "&limit=5";


                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray features = jsonObject.getJSONArray("features");

                List<String> suggestions = new ArrayList<>();
                for (int i = 0; i < features.length(); i++) {
                    JSONObject feature = features.getJSONObject(i);
                    JSONObject properties = feature.getJSONObject("properties");
                    String label = properties.getString("name");
                    String city = properties.optString("city", "");
                    String fullLabel = city.isEmpty() ? label : label + ", " + city;
                    suggestions.add(fullLabel);
                }

                this.runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, suggestions);
                    AutoCompleteTextView searchEditText = findViewById(R.id.searchEditText);
                    searchEditText.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                });


                if (features.length() > 0) {
                    JSONObject firstFeature = features.getJSONObject(0);
                    JSONObject geometry = firstFeature.getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    Log.d(TAG, "ni entra aca1");
                    double lon = coordinates.getDouble(0);
                    double lat = coordinates.getDouble(1);

                    org.osmdroid.util.GeoPoint point = new org.osmdroid.util.GeoPoint(lat, lon);
                    this.runOnUiThread(() -> {
                        mapController.setCenter(point);
                        mapController.setZoom(18);
                        Log.d(TAG, "ni entra aca2");
                    });

                } else {
                    Log.d(TAG, "Ubicación no encontrada");
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "Valio madre");
            }
            Log.d(TAG, "ni entra aca3");
        }).start();
    }

    private void searchLocation(String locationName) {
        new Thread(() -> {
            try {
                String email = (user != null) ? user.getEmail() : "unknown_user@example.com";

                String urlStr = "https://photon.komoot.io/api/?q=" +
                        URLEncoder.encode(locationName, "UTF-8") + "&limit=1";

                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "FireLoginApp/1.0 (" + email + ")");
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) result.append(line);

                JSONObject jsonObject = new JSONObject(result.toString());
                JSONArray features = jsonObject.getJSONArray("features");

                if (features.length() > 0) {
                    JSONObject geometry = features.getJSONObject(0).getJSONObject("geometry");
                    JSONArray coordinates = geometry.getJSONArray("coordinates");
                    double lon = coordinates.getDouble(0);
                    double lat = coordinates.getDouble(1);
                    org.osmdroid.util.GeoPoint point = new GeoPoint(lat, lon);

                    this.runOnUiThread(() -> {
                        mapController.setCenter(point);
                        mapController.setZoom(18);
                    });
                } else {
                    Log.d("Location", "Ubicación no encontrada");
                }

            } catch (Exception e) {
                Log.e("Location", "Error buscando ubicación", e);
            }
        }).start();
    }


    @Override
    public void onResume() {
        super.onResume();
        if (map != null)
            map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null)
            map.onPause();
    }

    ;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int i = 0; i < permissions.length; i++) {
                Log.d(TAG, "Permiso " + permissions[i] + ": " + grantResults[i]);
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                }
            }

            if (allGranted) {
                Log.v(TAG, "Permisos otorgados correctamente");
            }
        }
    }
}