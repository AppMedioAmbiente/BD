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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.Date;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

public class Register1 extends AppCompatActivity implements View.OnClickListener {

    EditText etContact, etPassword,etRepeatPassword;
    Button btnRegister;
    String contact, password, name,surname;
    String birthdate;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private String getUserId(FirebaseAuth auth){
        //Obtener usuario:
        FirebaseUser user = auth.getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }
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
                    showHome(contact,ProviderType.BASIC);
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
            showAlert("Campos Vacios","Debes llenar todos los campos");
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
        contact = etContact.getText().toString().replace(" ","");
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
    private void showHome(String email, ProviderType provider){
        //, String name, String surname, String birthdate
        Intent user=new Intent(Register1.this,Usuario.class);
        user.putExtra("contact",email);
        user.putExtra("provider",provider);
        user.putExtra("name",name);
        user.putExtra("surname",surname);
        user.putExtra("birthdate",birthdate);

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

