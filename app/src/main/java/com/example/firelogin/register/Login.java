package com.example.firelogin.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.LoginTemplate;
import com.example.firelogin.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends LoginTemplate {

    EditText etContact, etPassword;
    Button btn_redirect, btn_log;
    private FirebaseHandler fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        etContact = findViewById(R.id.contact);
        etPassword = findViewById(R.id.password);
        btn_redirect = findViewById(R.id.Rsing_up);

//        getUserData();
        fb = new FirebaseHandler(1);

        btn_redirect.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Register.class);
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

                fb.firebase.signInWithEmailAndPassword(contact,password)
                        .addOnCompleteListener(listener->{
                    if (!listener.isSuccessful()){
                        showAlert("Inicio de sesión","El inicio de sesión ha fallado, intente de nuevo ");
                        return;
                    }
                    user=fb.getUser();
                    getUserData();
                });

            }
        });
    }

    protected void getUserData(){
        Log.d("regerencia","getUserData");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        if(user==null){
            return ;
        }
        fb.abrirDocumento("usuarios",getUserId(),(exito,doc)->{
            if(exito && doc.exists()){
                showHome();
            }
        });
    }

    private String getFieldDefaultValue(String field) {
        switch (field){
            case "accountStatus":
                //active, suspended,blocked,inactive,pending
                return "valid";
            default:
                return "";
        }
    }
}
