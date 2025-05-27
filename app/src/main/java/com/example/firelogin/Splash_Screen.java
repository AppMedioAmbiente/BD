package com.example.firelogin;
import static com.example.firelogin.StaticFunctions.print;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firelogin.register.Login;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Splash_Screen extends LoginTemplate {
    @Override
    protected void onCreate(Bundle savedIntanceStarte) {
        super.onCreate(savedIntanceStarte);
        setContentView(R.layout.splash_screen);
//        Log.d("D","Log d");
//        Log.w("W","Log w");
//        Log.e("E","Log e");
//        Log.i("I","Log i");
//        Log.v("V","Log v");
//        Log.wtf("WTF","Log wf");
        fb=new FirebaseHandler(2);
        user=fb.getUser();
        new Handler().postDelayed(new  Runnable() {
            @Override
            public void run() {
                redirect();
            }},2000);
    }
    public void redirect(){
        if(user==null){
            print("No está verificado");
            Intent intent = new  Intent (Splash_Screen.this, Login.class);

            startActivity(intent);
            finish();
        }else{
            user.reload().addOnSuccessListener(view -> {
                if (user != null && user.isEmailVerified()) {
                    print("showHome in Splash_Screen.this");
                    onAuthStateChanged();
                    //            Intent intent = new  Intent (Splash_Screen.this,Home.class);
                    //            startActivity(intent);
                }else{
                    user=null;
                    redirect();
                }
            }).addOnFailureListener(view->{
                fb.cerrarSesion();
                user=null;
                redirect();
            });
        }
    }
}
