package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;

public class Register1 extends AppCompatActivity implements View.OnClickListener {

    EditText etContact, etPassword,etRepeatPassword;
    Button btnRegister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance()

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);

        etContact = findViewById(R.id.contact);
        etPassword = findViewById(R.id.password);
        btnRegister = findViewById(R.id.send);
        etRepeatPassword=findViewById(R.id.repeat_password);

        btnRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(!etRepeatPassword.getText().toString()
                .equals(etPassword.getText().toString())){
            showAlert("Campos Vacios","Debes llenar todos los campos");
            return ;
        }
        String name = getIntent().getStringExtra("name");
        String surname = getIntent().getStringExtra("surname");
        String birthdateString = getIntent().getStringExtra("birthdate");

        Date birthdate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            birthdate = dateFormat.parse(birthdateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //

        String contact = etContact.getText().toString().replace(" ","");
        String password = etPassword.getText().toString();

        FirebaseAuth.getInstance()
                .createUserWithEmailAndPassword(contact,password)
                .addOnCompleteListener(listener->{
                    if (listener.isSuccessful()){
                        db.collection();
                        showHome(listener.getResult().getUser().getEmail(),ProviderType.BASIC);
                    }else{
                        showAlert("Registro","El registro ha fallado:"+listener.getException().toString());
                    }
                });
    }
    private void showHome(String email, ProviderType provider){
        Intent user=new Intent(Register1.this,Usuario.class);
        user.putExtra("contact",email);
        user.putExtra("provider",provider);

        startActivity(user);
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
}

