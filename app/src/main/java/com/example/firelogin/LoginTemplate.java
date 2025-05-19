package com.example.firelogin;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginTemplate extends AppCompatActivity {

    public FirebaseUser user;
    protected String getUserId(){
        //Obtener usuario:
//        user.getUid();
        //user.getEmail();
        return (user != null) ? user.getUid() : null;
    }
    protected void showAlert(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        print(mensaje);
        // Configura el titulo.
        alertDialogBuilder.setTitle(titulo);

        // Configura el mensaje.
        alertDialogBuilder
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Cerrar",null)
                .create().show();
    }
    protected void showToastAlert(String mensaje){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show();
        print(mensaje);
    }
    protected void print(String msg){
        Log.d("_DEBUG_",msg);
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
