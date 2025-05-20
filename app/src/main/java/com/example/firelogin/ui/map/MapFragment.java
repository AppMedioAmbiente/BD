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
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentMapBinding;
import com.example.firelogin.ui.groups.GroupsViewModel;

public class MapFragment extends Fragment {
    private MapView map;
    private IMapController mapController;
    private static final String TAG = "OsmActivity";
    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentMapBinding binding;

    @Override

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        Context ctx = requireContext();
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        Configuration.getInstance().setUserAgentValue(ctx.getPackageName());


        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentMapBinding.inflate(inflater, container, false);

        View root = binding.getRoot();


            if (isStoragePermissionGranted()) {
                setupMap(ctx, root);
            }

        return root;
    }

    public boolean isStoragePermissionGranted() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                    },
                    PERMISSION_REQUEST_CODE);
            return false;
        }
        return true;
    }

    private void setupMap(Context ctx, View root) {

        map = root.findViewById(R.id.mapView);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setBuiltInZoomControls(true);
        map.setMultiTouchControls(true);

        mapController = map.getController();
        mapController.setZoom(15);

        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(
                new GpsMyLocationProvider(ctx), map);
        mLocationOverlay.enableMyLocation();
        map.getOverlays().add(mLocationOverlay);

        mLocationOverlay.runOnFirstFix(() -> {
            GeoPoint myLocation = mLocationOverlay.getMyLocation();
            if (myLocation != null) {
                requireActivity().runOnUiThread(() -> {
                    mapController.setCenter(myLocation);
                });
            }
        });

    }



    @Override
    public void onResume() {
        super.onResume();
        if (map != null) map.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (map != null) map.onPause();
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }

            if (allGranted) {
                Log.v(TAG, "Permission granted");

                if (getView() != null) {
                    setupMap(requireContext(), getView());
                }

            } else {
                Log.v(TAG, "One or more permissions denied");
            }
        }
    }
}
