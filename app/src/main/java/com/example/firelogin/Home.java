package com.example.firelogin;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.firelogin.databinding.HomeBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.checkerframework.checker.nullness.qual.NonNull;

public class Home extends AppCompatActivity {


    private AppBarConfiguration mAppBarConfiguration;
    private HomeBinding binding;

    String fragmentDestination;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.settings) {
            Intent intentStng = new Intent(Home.this, Settings.class);
            startActivity(intentStng);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setDarkMode(Home.this);
        binding = HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        fragmentDestination = getIntent().getStringExtra("fragmentToLoad");

        FirebaseUser cu = FirebaseAuth.getInstance().getCurrentUser();
        if (cu != null) {
            Toast.makeText(this, "Bienvenido " + cu.getEmail(), Toast.LENGTH_SHORT).show();
        }

        setSupportActionBar(binding.appBarHome.toolbar);
        binding.appBarHome.contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(Home.this, R.id.nav_host_fragment_content_home);
                navController.navigate(R.id.contactus, null, new NavOptions.Builder()
                        .setPopUpTo(R.id.contactus, false)
                        .build());
            }
        });
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_groups, R.id.navEv_events, R.id.navEv_myevents,
                R.id.navEv_calendar, R.id.navEv_map, R.id.navEv_history)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        //Menu menu = navigationView.getMenu();

        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_profile) {
                Intent intent = new Intent(Home.this, Settings_PD.class);
                startActivity(intent);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }

            boolean handled = NavigationUI.onNavDestinationSelected(item, navController);
            if (handled) {
                drawer.closeDrawer(GravityCompat.START);
            }

            if (id == R.id.nav_logout) {
                findViewById(R.id.nav_logout).setOnClickListener(l -> {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(Home.this, Login.class));
                });
            }

            return handled;
        });

        if (fragmentDestination != null && fragmentDestination.equals("fragment_events")) {
            navController.popBackStack(R.id.nav_home, true);
            navigationView.setCheckedItem(R.id.navEv_events);
            navController.navigate(R.id.navEv_events);

        } else if (fragmentDestination != null && fragmentDestination.equals("fragment_my_events")) {
            navController.popBackStack(R.id.nav_home, true);
            navigationView.setCheckedItem(R.id.navEv_myevents);
            navController.navigate(R.id.navEv_myevents);

        } else if (fragmentDestination != null && fragmentDestination.equals("fragment_history")) {
            navController.popBackStack(R.id.nav_home, true);
            navigationView.setCheckedItem(R.id.navEv_history);
            navController.navigate(R.id.navEv_history);
        }

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("usuarios").document(cu.getUid()).get().addOnSuccessListener(user -> {
            TextView username = navigationView.getHeaderView(0).findViewById(R.id.usernameH);
            TextView email = navigationView.getHeaderView(0).findViewById(R.id.emailH);
            username.setText(user.getString("nickname"));
            email.setText(cu.getEmail());
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public static void setDarkMode(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences("shrdPrf", Context.MODE_PRIVATE);
        // defValue: 0=DarkMode, 1=LightMode
        int theme = sharedPref.getInt("Theme", 1);

        if (theme == 0) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else if (theme == 1) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
    }
}