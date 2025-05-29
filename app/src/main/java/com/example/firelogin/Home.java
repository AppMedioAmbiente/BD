package com.example.firelogin;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES;

import static com.example.firelogin.StaticFunctions.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firelogin.notifications.NotificationUtil;
import com.example.firelogin.register.Login;
import com.example.firelogin.settings.Settings;
import com.example.firelogin.settings.Settings_PD;
import com.example.firelogin.ui.groups.GroupsFragment;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Home extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private HomeBinding binding;
    private FirebaseHandler fh;
    private FirebaseUser cu;
    String nickname="";
    Boolean wasHeaderLoaded=false;


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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setDarkMode(Home.this);
        super.onCreate(savedInstanceState);

        binding = HomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_groups, R.id.navEv_events, R.id.navEv_myevents,
                R.id.navEv_calendar, R.id.navEv_map, R.id.navEv_history)
                .setOpenableLayout(drawer)
                .build();
        setSupportActionBar(binding.appBarHome.toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_home);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        Boolean wasRedirected=redirectFragments();

        //showToastAlert(this,"inicio de Home");
        fh = new FirebaseHandler(2);
        cu = fh.getUser();

        fh.abrirDocumento("usuarios",cu.getUid(),(exito,doc)->{
            if(exito) {
                nickname=doc.getString("nickname");
                if(wasHeaderLoaded){
                    print("Parece que header cargó antes que esta consulta");
                    setUserTexts();
                }else{
                    print("todo normal y sin problemas");
                }
            }else{
                print("no hubo exito");
            }
        });


        ImageCarousel carousel = findViewById(R.id.carousel);
        List<CarouselItem> list = new ArrayList<>();
        list.add(new CarouselItem(R.drawable.logo, "Icono de SECOVO"));
        carousel.setData(list);
        if (!wasRedirected && cu != null) {
            Toast.makeText(this, "Bienvenido " + cu.getEmail(), Toast.LENGTH_SHORT).show();
        }

        findViewById(R.id.notif).setOnClickListener(view->{
            int[][] now = NotificationUtil.getCurrentDate();
            now[1][2]+=2;

            NotificationUtil.createNotificationChannel(getApplicationContext());
            NotificationUtil.createNotification(getApplicationContext(),
                    "Notificacion de Prueba",now[0],now[1]);
        });

        // Toast.makeText(this, "Bienvenido " +cu.getEmail(), Toast.LENGTH_SHORT).show();
        binding.navView.post(()->{
            setUserTexts();
        });
//        setUserTexts();
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

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.


        navigationView.setNavigationItemSelectedListener(item -> {
            //redirectFragments();
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

    }
    private void setUserTexts() {
        TextView email = binding.navView.findViewById(R.id.emailH);
        TextView name = binding.navView.findViewById(R.id.usernameH);

        if(name==null || email==null){
            showToastAlert(this,"WTF, name e email nulos");
            return;
        }
        if(nickname.equals("")){
            wasHeaderLoaded=true;
            return;
        }
        email.setText(cu.getEmail());
        name.setText(nickname);
        print("name e email setted");
    }

    public Boolean redirectFragments() {
        String fragmentEvent = getIntent().getStringExtra("fragmentToLoad");
        if(fragmentEvent!=null){
            //Toast.makeText(this, "Redirigir al fragmento "+ ((fragmentEvent!=null) ? fragmentEvent : "Nada"), Toast.LENGTH_SHORT).show();
            NavController navController = Navigation.findNavController(Home.this, R.id.nav_host_fragment_content_home);

            int fragmentId=getFragmentId(fragmentEvent);
            print("El fragmento es "+fragmentEvent+" de ID: "+String.valueOf(fragmentId));
            navController.navigate(fragmentId, null, new NavOptions.Builder()
                    .setPopUpTo(fragmentId, false)
                    .build());
            return (fragmentId!=R.id.nav_home);
        }
        return false;
    }
    private int getFragmentId(String fragmentEvent){
        switch(fragmentEvent){
            case "fragment_events":
                return R.id.navEv_events;
            case "fragment_my_events":
                return R.id.navEv_myevents;
            case "fragment_history":
                return R.id.navEv_history;
            case "fragment_calendar":
                return R.id.navEv_calendar;
            default:
                print("REGRESA NAV HOME");
                return R.id.nav_home;
        }
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

        if (theme == 0 && currentMode!=MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        } else if (theme == 1 && currentMode!=MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO);
        }
    }

}