package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Settings_PD extends AppCompatActivity {

    EditText names, show_birthdate, surnames, show_email, show_password, show_nickname, show_country, show_state;
    Button cancelPDBtn;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_personaldata);

        Toolbar tbPDSettings = findViewById(R.id.tbPDSettings);
        setSupportActionBar(tbPDSettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cancelPDBtn = findViewById(R.id.cancelPDBtn);
        cancelPDBtn.setOnClickListener(view->{onSupportNavigateUp();});

        names = findViewById(R.id.show_name);
        surnames = findViewById(R.id.show_surnname);
        show_birthdate = findViewById(R.id.show_birthdate);
        show_nickname = findViewById(R.id.nickname);
        show_country = findViewById(R.id.country);
        show_state = findViewById(R.id.state);
        show_email = findViewById(R.id.show_email);
        show_password = findViewById(R.id.show_password);
        Button btnSave = findViewById(R.id.save);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            names.setText(documentSnapshot.getString("name"));
                            surnames.setText(documentSnapshot.getString("surname"));
                            show_birthdate.setText(documentSnapshot.getString("birthdate"));
                            show_nickname.setText(documentSnapshot.getString("nickname"));
                            show_country.setText(documentSnapshot.getString("country"));
                            show_state.setText(documentSnapshot.getString("state"));
                            show_email.setText(user.getEmail());

                            show_email.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (!s.toString().trim().equals(user.getEmail())) {
                                        show_password.setVisibility(View.VISIBLE);
                                    } else {
                                        show_password.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {}
                            });

                        } else {
                            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    });
        }

        btnSave.setOnClickListener(v -> {
            if (user != null) {
                String new_name = names.getText().toString().trim();
                String new_surname = surnames.getText().toString().trim();
                String new_birthdate = show_birthdate.getText().toString().trim();
                String new_nickname = show_nickname.getText().toString().trim();
                String new_country = show_country.getText().toString().trim();
                String new_state = show_state.getText().toString().trim();
                String new_email = show_email.getText().toString().trim();
                String new_password = show_password.getText().toString().trim();

                if (!new_name.isEmpty() && !new_surname.isEmpty() && !new_birthdate.isEmpty()
                        && !new_nickname.isEmpty() && !new_country.isEmpty() && !new_state.isEmpty()
                        && !new_email.isEmpty()) {

                    if (!new_email.equals(user.getEmail())) {
                        if (new_password.isEmpty()) {
                            show_password.setError("Ingrese su contraseña para cambiar el correo");
                            return;
                        }

                        Log.d("Contraseña que recibe: ", new_password);
                        AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), new_password);

                        user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                            if(authTask.isSuccessful()) {

                                user.updateEmail(new_email).addOnCompleteListener(updateTask -> {
                                    if (updateTask.isSuccessful()) {
                                        actualizarDatosFirestore(db, user, new_name, new_surname, new_birthdate, new_nickname, new_country, new_state);
                                        FirebaseAuth.getInstance().signOut();
                                        Intent intent = new Intent(Settings_PD.this, Home.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Exception e = updateTask.getException();
                                        Toast.makeText(getApplicationContext(), "Error al actualizar el email: " + e.getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                });

                            } else {
                                Exception e = authTask.getException();
                                Toast.makeText(getApplicationContext(), "Reautenticación fallida: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

                    } else {
                        actualizarDatosFirestore(db, user, new_name, new_surname, new_birthdate, new_nickname, new_country, new_state);
                    }

                } else {
                    Toast.makeText(getApplicationContext(), "Por favor, llene todos los datos", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.w("FirebaseAuth", "Usuario no autenticado");
            }
        });
    }

    private void actualizarDatosFirestore(FirebaseFirestore db, FirebaseUser user,
                                          String name, String surname, String birthdate,
                                          String nickname, String country, String state) {
        DocumentReference docRef = db.collection("usuarios").document(user.getUid());
        Map<String, Object> new_data = new HashMap<>();
        new_data.put("name", name);
        new_data.put("surname", surname);
        new_data.put("birthdate", birthdate);
        new_data.put("nickname", nickname);
        new_data.put("country", country);
        new_data.put("state", state);

        docRef.set(new_data, SetOptions.merge());
        Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
