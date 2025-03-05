package com.example.firelogin;

import android.content.Intent;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginTemplate extends AppCompatActivity {
    protected String getUserId(FirebaseAuth auth){
        //Obtener usuario:
        FirebaseUser user = auth.getCurrentUser();
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

    protected void showHome(ProviderType provider,
            String contact,String name, String surname, String birthdate) {
        Intent user=new Intent(this,Usuario.class);
        user.putExtra("contact",contact);
        user.putExtra("provider",provider);
        user.putExtra("name",name);
        user.putExtra("surname",surname);
        user.putExtra("birthdate",birthdate);
        startActivity(user);
    }
}
