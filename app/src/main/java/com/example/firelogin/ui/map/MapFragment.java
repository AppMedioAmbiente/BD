package com.example.firelogin.ui.map;

import static androidx.core.content.ContextCompat.checkSelfPermission;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.view.LayoutInflater;
import android.view.ViewGroup;
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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MapFragment extends Fragment {
    private MapView map;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentMapBinding binding;

    EditText searchEditText;
    Button searchButton;


    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());

        Log.w(TAG, "entro aca");



        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

            if (isStoragePermissionGranted()) {
                setupMap(ctx, root);
                setupSearch();
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

    private void setupMap(Context ctx, View root) {

        map = root.findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(18);

        Log.w(TAG, "ando aca");


        if (isStoragePermissionGranted() == true) {

            MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(
                    new GpsMyLocationProvider(ctx), map);
            mLocationOverlay.enableMyLocation();
            mLocationOverlay.enableFollowLocation();
            map.getOverlays().add(mLocationOverlay);
            //GeoPoint startPoint = new GeoPoint(51.496994, -13.4733);

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
    }

    private void setupSearch() {
        EditText searchEditText = requireActivity().findViewById(R.id.searchEditText);
        Button searchButton = requireActivity().findViewById(R.id.searchButton);

        binding.searchButton.setOnClickListener(v -> {
            String locationName = binding.searchEditText.getText().toString().trim();
            if (!locationName.isEmpty()) {
                searchLocation(locationName);
            }
        });
    }

    private void searchLocation(String locationName) {
        new Thread(() -> {

            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String email = (user != null ) ? user.getEmail() : "unknown_user@example.com";

                Log.d("FirebaseAuth", "Correo del usuario: " + email);

            try {
                String urlStr = "https://nominatim.openstreetmap.org/search?q=" +
                        locationName.replace(" ", "+") +
                        "&format=json&limit=1";
                URL url = new URL(urlStr);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("User-Agent", "FireLoginApp/1.0 ("+ email +")");
                conn.connect();

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONArray results = new JSONArray(result.toString());
                if (results.length() > 0) {
                    JSONObject place = results.getJSONObject(0);
                    double lat = Double.parseDouble(place.getString("lat"));
                    double lon = Double.parseDouble(place.getString("lon"));

                    GeoPoint point = new GeoPoint(lat, lon);
                    requireActivity().runOnUiThread(() -> {
                        mapController.setCenter(point);
                        mapController.setZoom(18);
                    });

                } else {
                    Log.d(TAG, "Ubicación no encontrada");
                }

            } catch (Exception e) {
                e.printStackTrace();
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
