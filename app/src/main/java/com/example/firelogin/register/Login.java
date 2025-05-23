package com.example.firelogin.register;

import static com.example.firelogin.StaticFunctions.*;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.LoginTemplate;

import com.example.firelogin.R;

public class Login extends LoginTemplate {

    EditText etContact, etPassword;
    Button btn_redirect, btn_log;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        print("inicio del OnCreate de Login");
        etContact = findViewById(R.id.contact);
        etPassword = findViewById(R.id.password);
        btn_redirect = findViewById(R.id.Rsing_up);

//        getUserData();
        fb = new FirebaseHandler(2);
        user=fb.getUser();
        if(user!=null) {
            setMAuthListener();
        }
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
                    showAlert(Login.this,"Campos","Debes llenar correo y contraseña");
                    return ;
                }

                fb.firebase.signInWithEmailAndPassword(contact,password)
                        .addOnCompleteListener(listener->{
                    if (!listener.isSuccessful()){

                        showAlert(Login.this,"Inicio de sesión","El inicio de sesión ha fallado : "+listener.getException().getMessage());
                        return;
                    }
                    setMAuthListener();
                    onAuthStateChanged();
                });

            }
        });

        findViewById(R.id.ResetPsw).setOnClickListener(view->{
            String contact= etContact.getText().toString().replace(" ","");
            if(contact.isEmpty()){
                showAlert(Login.this,"Campo Faltante","Debes ingresar un correo");
                return;
            }
            fb.firebase.sendPasswordResetEmail(contact).addOnCompleteListener(aVOid->{
                showAlert(Login.this,"Restablecimiento de Contraseña","Se ha enviado un correo de restablecimiento de contraseña");
            }).addOnFailureListener(aVoid->{
                showAlert(Login.this,"Error","Ocurrio un error:"+aVoid.getMessage());
            });
        });
        print("fin del OnCreate de Login");
    }
}