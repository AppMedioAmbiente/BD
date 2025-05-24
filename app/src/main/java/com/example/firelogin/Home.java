package com.example.firelogin;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import static com.example.firelogin.StaticFunctions.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firelogin.register.Login;
import com.example.firelogin.settings.Settings;
import com.example.firelogin.settings.Settings_PD;
import com.example.firelogin.ui.groups.GroupsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.imaginativeworld.whynotimagecarousel.ImageCarousel;
import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;
import com.example.firelogin.databinding.HomeBinding;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private HomeBinding binding;
//    FirebaseHandler fh;
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
    private void changeFragment(NavController navController){
        String fragmentDef=getIntent().getStringExtra("Fragment");
        int fragmentId=0;
        if(fragmentDef==null)return;
        switch (fragmentDef){
            case "Groups":
                fragmentId = R.id.nav_groups;
                break;
            case "Groups_Creator":
                fragmentId = R.id.GroupCreator;
                break;
        }
        if(fragmentId==0)return;
        navController.navigate(fragmentId);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDarkMode(Home.this);

        super.onCreate(savedInstanceState);
        showToastAlert(this,"inicio de Home");

        binding = HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ImageCarousel carousel = findViewById(R.id.carousel);
        if(carousel!=null) {
            List<CarouselItem> list = new ArrayList<>();
            list.add(new CarouselItem(R.drawable.logo, "Icono de SECOVO"));
            carousel.setData(list);
        }

//        fh = new FirebaseHandler(2);
//        FirebaseUser cu = fh.getUser();
//        if(cu==null) {
//            Toast.makeText(this, "WTF. Que haces aquí?", Toast.LENGTH_SHORT).show();
//            return;
//        }
//        Toast.makeText(this, "Bienvenido " +cu.getEmail(), Toast.LENGTH_SHORT).show();
        setSupportActionBar(binding.appBarHome.toolbar);

//        fh.abrirDocumento("usuarios",cu.getUid(),(exito,doc)->{
//            if(exito) {
//                TextView email = binding.navView.findViewById(R.id.emailH);
//                email.setText(cu.getEmail());
//
//                TextView name = binding.navView.findViewById(R.id.usernameH);
//                name.setText(doc.get("nickname").toString());
//            }else{
//                print("no hubo exito");
//            }
//        });

        binding.appBarHome.contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavController navController = Navigation.findNavController(
                        Home.this, R.id.nav_host_fragment_content_home);
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

        changeFragment(navController);

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

            if (id == R.id.nav_logout){
            findViewById(R.id.nav_logout).setOnClickListener(l->{
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(Home.this, Login.class));
            });
            }

            return handled;
        });
        showToastAlert(this,"Fin de Home");
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
        int theme = sharedPref.getInt("Theme", 0);
        int currentMode = AppCompatDelegate.getDefaultNightMode();
        if (theme == 0 && currentMode!=0) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else if (theme == 1 && currentMode!=1) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
    }

}