package com.example.firelogin;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginTemplate extends AppCompatActivity {
    FirebaseAuth firebase=FirebaseAuth.getInstance();
    FirebaseUser user = getCurrentUser();
    protected FirebaseUser getCurrentUser(){
        if(firebase==null){
            return null;
        }
        return firebase.getCurrentUser();
    }
    protected String getUserId(){
        //Obtener usuario:
        user.getUid();
        //user.getEmail();
        return (user != null) ? user.getUid() : null;
    }
    protected void showAlert(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Configura el titulo.
        alertDialogBuilder.setTitle(titulo);

        // Configura el mensaje.
        alertDialogBuilder
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Cerrar",null)
                .create().show();


    }

    protected void showHome() {
        Intent home=new Intent(this,Home.class);
        /*
        home.putExtra("contact",contact);
        home.putExtra("provider",provider);
        home.putExtra("name",name);
        home.putExtra("surname",surname);
        home.putExtra("birthdate",birthdate);
         */
        startActivity(home);
    }
}
