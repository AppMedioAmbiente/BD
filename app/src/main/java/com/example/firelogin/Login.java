package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText etContact, etPassword;
    Button btn_redirect, btn_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etContact = findViewById(R.id.contact);
        etPassword = findViewById(R.id.password);
        btn_redirect = findViewById(R.id.Rsing_up);

        btn_redirect.setOnClickListener(view -> {

            Intent intent = new Intent(Login.this,Register.class);
            startActivity(intent);
        });

        btn_log = findViewById(R.id.Send);

        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String contact = etContact.getText().toString().replace(" ","");
                final String password = etPassword.getText().toString();

                if(contact.equals("") || password.equals("")){
                    showAlert("Campos","Debes llenar correo y contraseña");
                    return ;
                }
                FirebaseAuth firebase=FirebaseAuth.getInstance();
                firebase.signInWithEmailAndPassword(contact,password)
                        .addOnCompleteListener(listener->{
                    if (!listener.isSuccessful()){
                        showAlert("Inicio de Sesion","El inicio de Sesion ha fallado: "+listener.getException().toString());
                        return;
                    }
                    getUserData(firebase,contact);

                });

            }
        });
    }

    private String getUserId(FirebaseAuth auth){
        //Obtener usuario:
        FirebaseUser user = auth.getCurrentUser();
        //user.getEmail();
        return (user != null) ? user.getUid() : null;
    }
    private void getUserData(FirebaseAuth firebase,String contact){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(getUserId(firebase))  // Usa el ID del usuario como clave del documento
                .get()
                .addOnSuccessListener(document->{
                    if(document.exists()){
                        showHome(document,ProviderType.BASIC,contact);
                    }
                });
    }

    private void showAlert(String titulo,String mensaje){
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

    private void showHome(DocumentSnapshot document, ProviderType provider,String contact) {
        Intent user=new Intent(Login.this,Usuario.class);
        user.putExtra("contact",contact);
        user.putExtra("provider",provider);
        user.putExtra("name",document.getString("name"));
        user.putExtra("surname",document.getString("surname"));
        user.putExtra("birthdate",document.getString("birthdate"));
        //showAlert("nacimiento",user.getStringExtra("birthdate"));
        startActivity(user);
    }
}
