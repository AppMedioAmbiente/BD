package com.example.firelogin.ui.map;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.DrawableWrapper;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IGeoPoint;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.CopyrightOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentMapBinding;
import com.example.firelogin.ui.groups.GroupsViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapFragment extends Fragment {
    private MapView map;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentMapBinding binding;

    AutoCompleteTextView searchEditText;
    Button searchButton, waypointBtn;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_map, container, false);

        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        if (isStoragePermissionGranted()) {
            setupMap(ctx, root);
            setupSearch(root);
        }

        return root;
    }

    public boolean isStoragePermissionGranted() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    PERMISSION_REQUEST_CODE);
            Log.w(TAG, "no se aceptaron permisos");

            return true;
        } else {
            Log.w(TAG, "valio madre");

        }
        Log.w(TAG, "Se aceptaron permisos");

        return true;
    }

    private MapEventsOverlay currentEventOverlay = null;
    private boolean isWaitingForTap = false;

    private void setupMap(Context ctx, View root) {

        map = root.findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);
        map.getOverlayManager().add(new CopyrightOverlay(getContext()));


        mapController = map.getController();
        mapController.setZoom(18);

        map.setMultiTouchControls(true);

        Log.w(TAG, "ando aca");


        if (isStoragePermissionGranted() == true) {

            MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(ctx), map);
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.enableFollowLocation();
            map.getOverlays().add(mLocationOverlay);

            mLocationOverlay.runOnFirstFix(() -> {
                GeoPoint myLocation = mLocationOverlay.getMyLocation();
                Log.d(TAG, "MyLocation: " + myLocation);
                if (myLocation != null) {
                    requireActivity().runOnUiThread(() -> {
                        mapController.setCenter(myLocation);

                    });
                } else {
                    Log.w(TAG, "No se puede optener la ubicación");
                }

            });
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("geopoint")
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
    }

    public void agregarMarcador(org.osmdroid.util.GeoPoint punto) {
        Marker waypoint = new Marker(map);
        waypoint.setPosition(punto);
        waypoint.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        waypoint.setTitle("Ubicación guardada");

        map.getOverlays().add(waypoint);
        map.invalidate();
    }


    private void setupSearch(View root) {

        AutoCompleteTextView searchEditText = root.findViewById(R.id.searchEditText);
        Button searchButton = root.findViewById(R.id.searchButton);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 2) {
                    fetchSuggestions(s.toString(), root);
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

    private void fetchSuggestions(String locationName, View root) {
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

                requireActivity().runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, suggestions);
                    AutoCompleteTextView searchEditText = root.findViewById(R.id.searchEditText);
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

                    GeoPoint point = new GeoPoint(lat, lon);
                    requireActivity().runOnUiThread(() -> {
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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
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
                    GeoPoint point = new GeoPoint(lat, lon);

                    requireActivity().runOnUiThread(() -> {
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
                Log.v(TAG,  "Permisos otorgados correctamente");

                if (getView() != null) {
                    setupMap(requireContext(), getView());
                }
            } else {
                Log.v(TAG, "Uno o más permisos fueron denegados");
            }
        }
    }

}
