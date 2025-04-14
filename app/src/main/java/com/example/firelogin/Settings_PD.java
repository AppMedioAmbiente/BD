package com.example.firelogin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;

public class Settings_PD extends AppCompatActivity {

    EditText names, show_birthdate, surnames, show_email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_personaldata);

        names = findViewById(R.id.show_name);
        surnames = findViewById(R.id.show_surnname);
        show_birthdate = findViewById(R.id.show_birthdate);
        show_email = findViewById(R.id.show_email);
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
                        } else {
                            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    });

            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null) {
                        String new_name = names.getText().toString().trim();
                        String new_surname = surnames.getText().toString().trim();
                        String new_birthdate = show_birthdate.getText().toString().trim();
                        String new_email = show_email.getText().toString().trim();

                        if (!new_name.isEmpty() && !new_surname.isEmpty() && !new_birthdate.isEmpty() && !new_email.isEmpty()) {
                            user.updateEmail(new_email).addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    DocumentReference docRef = db.collection("usuarios").document(user.getUid());
                                    Map<String, Object> new_data = new HashMap<>();
                                    new_data.put("name", new_name);
                                    new_data.put("surname", new_surname);
                                    new_data.put("birthdate", new_birthdate);
                                    new_data.put("email", new_email);

                                    docRef.set(new_data, SetOptions.merge());

                                    Toast.makeText(getApplicationContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Error al actualizar el email", Toast.LENGTH_SHORT).show();
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
    }
}
