package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register1 extends LoginTemplate implements View.OnClickListener {

    EditText etContact, etPassword,etRepeatPassword;
    Button btnRegister;
    String contact, password, name,surname;
    String birthdate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public void insertValues(String userId){
        Intent intent = getIntent();

        // Crea un objeto que deseas insertar (puede ser cualquier tipo de objeto o mapa)
        Map<String, Object> data = new HashMap<>();

        data.put("name", intent.getStringExtra("name"));
        data.put("surname", intent.getStringExtra("surname"));
        data.put("birthdate", intent.getStringExtra("birthdate"));
        // Inserta los datos usando el ID del usuario como el documento

        db.collection("usuarios")
                .document(userId)  // Usa el ID del usuario como clave del documento
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Si la inserción es exitosa
                    showHome(ProviderType.BASIC,
                            contact,name,surname,birthdate);
                    //showAlert("Firestore", "DocumentSnapshot added with ID: " + userId);
                })
                .addOnFailureListener(e -> {
                    // Si hay un error al insertar
                    failedRegister(e.toString());
                });
    }

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
            showAlert("Error de Contraseña","Ambas Contraseñas deben coincidir");
            return ;
        }
        name = getIntent().getStringExtra("name");
        surname = getIntent().getStringExtra("surname");
        //String birthdateString = getIntent().getStringExtra("birthdate");

        birthdate=getIntent().getStringExtra("birthdate");
        /*
        birthdate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            birthdate = dateFormat.parse(birthdateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        contact = etContact.getText().toString().replace(" ",""); // elimino espacios en el correo
        password = etPassword.getText().toString();

        FirebaseAuth firebase=FirebaseAuth.getInstance();

        firebase.createUserWithEmailAndPassword(contact,password)
                .addOnCompleteListener(listener->{
                    if (!listener.isSuccessful()){
                        failedRegister(listener.getException().toString());
                        return;
                    }
                    insertValues(getUserId(firebase));
                });
    }
    private void failedRegister(String exception){
        showAlert("Registro","El registro ha fallado:"+exception);
    }
}

