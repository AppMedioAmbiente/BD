package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends LoginTemplate {

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
    protected void getUserData(FirebaseAuth firebase,String contact){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("usuarios")
                .document(getUserId(firebase))  // Usa el ID del usuario como clave del documento
                .get()
                .addOnSuccessListener(document->{
                    if(document.exists()){
                        showHome(ProviderType.BASIC,
                                contact,
                                document.getString("name"),
                                document.getString("surname"),
                                document.getString("birthdate"));
                    }
                });
    }
}
