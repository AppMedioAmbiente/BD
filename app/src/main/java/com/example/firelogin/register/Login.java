package com.example.firelogin.register;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.LoginTemplate;
import com.example.firelogin.Manifest;
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
        fb = new FirebaseHandler(2);

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
                        showAlert("Inicio de sesión","El inicio de sesión ha fallado : "+listener.getException().getMessage());
                        return;
                    }
                    user=fb.updateUser();
                    getUserData();
                });

            }
        });

        findViewById(R.id.ResetPsw).setOnClickListener(view->{
            String contact= etContact.getText().toString().replace(" ","");
            if(contact.isEmpty()){
                showAlert("Campo Faltante","Debes ingresar un correo");
                return;
            }
            fb.firebase.sendPasswordResetEmail(contact).addOnCompleteListener(aVOid->{
                showAlert("Restablecimiento de Contraseña","Se ha enviado un correo de restablecimiento de contraseña");
            }).addOnFailureListener(aVoid->{
                showAlert("Error","Ocurrio un error:"+aVoid.getMessage());
            });
        });
    }

    protected void getUserData(){
        Log.d("regerencia","getUserData");

        if(user==null){
            print("El user es nulo");
            return ;
        }
        fb.abrirDocumento("usuarios",getUserId(),(exito,doc)->{
            print("Existe el doc?"+String.valueOf(doc.exists()));
            if(exito){
                showHome();
            }else{
                print("El documento fallo");
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
