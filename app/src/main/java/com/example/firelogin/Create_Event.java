package com.example.firelogin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.firelogin.ui.groups.GroupsViewModel;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Create_Event extends AppCompatActivity implements View.OnClickListener {

    private MapView map;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private  Marker lastMarker;

    AutoCompleteTextView searchEditText;
    Button searchButton, waypointBtn;

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
            showDatePicker(etEventDate, this);
        });

        etEventEndDate.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                return;
            }
            showDatePicker(etEventEndDate, this);
        });

        spEventType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                EventType selectedType = (EventType) parent.getItemAtPosition(position);

                Integer typeId = selectedType.getId();
                String typeName = selectedType.getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
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

        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        if (isStoragePermissionGranted()) {
            setupMap();
            setupSearch();
            Log.w(TAG, "aca ando wey");
        }

        btnSaveEvent.setOnClickListener(this);


    }


    @Override
    public void onClick(View v) {
        if (checkFields(etEventName, spEventType, etEventDescrip, etEventDate, etEventEndDate, etEventMaterials,
                etEventMinVolun)) {

            EventType selectedType = (EventType) spEventType.getSelectedItem();
            try {

                Map<String, Object> data = new HashMap<>();
                data.put("event_name", etEventName.getText().toString().trim());
                data.put("type", db.collection("event_type").document(selectedType.getId().toString()));
                data.put("description", etEventDescrip.getText().toString().trim());
                data.put("date", new Timestamp(sdf.parse(etEventDate.getText().toString())));
                data.put("end_date", new Timestamp(sdf.parse(etEventEndDate.getText().toString())));
                data.put("materials", etEventMaterials.getText().toString().trim());
                data.put("min_volunteers", Integer.parseInt(etEventMinVolun.getText().toString()));
                data.put("status", db.collection("event_status").document("1"));
                data.put("organizer", db.collection("usuarios").document(user.getUid()));

                com.google.firebase.firestore.GeoPoint firestoreGeoPoint = null;
                if (lastMarker != null) {
                    org.osmdroid.util.GeoPoint point = lastMarker.getPosition();
                    double latitude = point.getLatitude();
                    double longitude = point.getLongitude();

                    com.google.firebase.firestore.GeoPoint finalFirestoreGeoPoint =
                            new com.google.firebase.firestore.GeoPoint(latitude, longitude);
                    data.put("location", finalFirestoreGeoPoint);


                    db.collection("eventos").add(data).addOnSuccessListener(s -> {

                                Map<String, Object> dataEvUser = new HashMap<>();
                                dataEvUser.put("id_event", db.collection("eventos").document(s.getId()));
                                dataEvUser.put("id_usuario", db.collection("usuarios").document(user.getUid()));
                                db.collection("event_has_usuarios").add(dataEvUser).addOnFailureListener(f1 -> {
                                    Log.d("Error al guardar el evento y usuario: ", f1.getMessage());
                                });

                                Map<String, Object> geoData = new HashMap<>();
                                geoData.put("location", finalFirestoreGeoPoint);
                                geoData.put("id_event", s);
                                db.collection("geopoint").add(geoData)
                                        .addOnSuccessListener(gRef -> Log.d("Firebase", "GeoPoint guardado correctamente"))
                                        .addOnFailureListener(e -> Log.e("Firebase", "Error al guardar geopoint", e));


                                Intent intent = new Intent(this, Home.class);
                                intent.putExtra("fragmentToLoad", "fragment_events");
                                startActivity(intent);
                            })
                            .addOnFailureListener(f -> {
                                showToastAlert("Error al guardar los datos");
                                Log.d("Error: ", f.getMessage());
                            });
                }

            } catch (ParseException ex) {
                showToastAlert("Error al guardar la fecha");
                Log.d("Error parse: ", ex.getMessage());
            }

        } else {
            showToastAlert("Debe llenar correctamente todos los campos");
        }
    }

    public void showDatePicker(EditText etDate, Context context) {
        calendar = Calendar.getInstance();

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (DatePicker view1, int selectedYear, int selectedMonth, int selectedDay) -> {
                    Calendar calendarSelected = Calendar.getInstance();
                    calendarSelected.set(selectedYear, selectedMonth, selectedDay);
                    showTimePicker(calendarSelected, etDate, context);

                }, year, month, day);
        datePickerDialog.show();
    }

    public void showTimePicker(Calendar calendar, EditText etDate, Context context) {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context,
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

    public boolean checkFields(EditText name, Spinner type, EditText descrip, EditText date,
                               EditText endDate, EditText materials, EditText volun) {

        if (name.getText().toString().trim().isEmpty()) return false;

        if (type.getSelectedItem().toString().isEmpty()) return false;

        if (descrip.getText().toString().trim().isEmpty()) return false;

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

        /*map.setTileSource(new OnlineTileSourceBase(
=======
        map.setTileSource(new OnlineTileSourceBase(
>>>>>>> Stashed changes
                "Carto Light",
                1, 20, 256, "",
                new String[] { "a", "b", "c" }) {

            @Override
            public String getTileURLString(final long pMapTileIndex) {
                int zoom = MapTileIndex.getZoom(pMapTileIndex);
                int x = MapTileIndex.getX(pMapTileIndex);
                int y = MapTileIndex.getY(pMapTileIndex);


                return "https://" + getBaseUrl() + ".basemaps.cartocdn.com/light_all/"
                        + zoom + "/" + x + "/" + y + ".png";
            }
        });*/
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

        Button waypoint = findViewById(R.id.waypointBtn);

        waypoint.setOnClickListener(v-> {
            Log.w(TAG,"si detecto el clic en el boton");

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

    private void setupSearch() {

        AutoCompleteTextView searchEditText = findViewById(R.id.searchEditText);
        Button searchButton = findViewById(R.id.searchButton);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchSuggestions(s.toString());
                }
            }
            @Override public void afterTextChanged(Editable s) {}
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

    private void fetchSuggestions(String locationName ) {
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
    };

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