package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.Button;

public class Settings_PD extends AppCompatActivity {

    EditText names, show_birthdate, surnames, show_email, show_password ;
    Button cancelPDBtn;

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
                            String name = documentSnapshot.getString("name");
                            String surname = documentSnapshot.getString("surname");
                            String birthdate = documentSnapshot.getString("birthdate");
                            String email = user.getEmail();
                            names.setText(name);
                            surnames.setText(surname);
                            show_birthdate.setText(birthdate);
                            show_email.setText(email);


                            show_email.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    if (!s.toString().trim().isEmpty()) {
                                        show_password.setVisibility(View.VISIBLE);
                                    } else {
                                        show_password.setVisibility(View.GONE);
                                    }
                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    // no usado
                                }
                            });

                        } else {
                            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    });
            }

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String change_email = show_email.getText().toString().trim();
                    String change_password = show_password.getText().toString().trim();
                    Log.d("Email que recibe 1: ", change_password);
                    Log.d("Email que recibe 1: ", change_email);

                    if (user != null) {
                        String new_name = names.getText().toString().trim();
                        String new_surname = surnames.getText().toString().trim();
                        String new_birthdate = show_birthdate.getText().toString().trim();
                        String new_email = show_email.getText().toString().trim();

                        if (!new_name.isEmpty() && !new_surname.isEmpty() && !new_birthdate.isEmpty() && !new_email.isEmpty()) {

                            Log.d("Email que recibe 2: ", change_password);
                            Log.d("Email que recibe 2: ", change_email);

                            AuthCredential credential = EmailAuthProvider.getCredential(change_email, change_password);

                            user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                                if(authTask.isSuccessful()) {

                                    user.updateEmail(new_email).addOnCompleteListener(updateTask -> {
                                        if (updateTask.isSuccessful()) {

                                            DocumentReference docRef = db.collection("usuarios").document(user.getUid());
                                            Map<String, Object> new_data = new HashMap<>();
                                            new_data.put("name", new_name);
                                            new_data.put("surname", new_surname);
                                            new_data.put("birthdtae", new_birthdate);
                                            new_data.put("email", new_email);

                                            docRef.set(new_data, SetOptions.merge());

                                            Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Error al actualizar el email", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }
                            });


                        } else {
                            Toast.makeText(getApplicationContext(), "Por favor, llene todos los datos", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Log.w("FirebaseAuth", "Usuario no autenticado");
                    }
                }
            });
        }


    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
