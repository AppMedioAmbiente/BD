package com.example.firelogin.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.firelogin.R;
import com.example.firelogin.register.Login;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class Settings_DA extends AppCompatActivity {

    Button btnDelete, cancelDABtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_deleteaccount);

        Toolbar tbDASettings = findViewById(R.id.tbDASettings);
        setSupportActionBar(tbDASettings);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        cancelDABtn = findViewById(R.id.cancelDABtn);
        cancelDABtn.setOnClickListener(view->{onSupportNavigateUp();});

        EditText getEmail = findViewById(R.id.email);
        EditText getPassword = findViewById(R.id.password);
        btnDelete = findViewById(R.id.delete);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userId = user.getUid();

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String email = getEmail.getText().toString().trim();
                    String password = getPassword.getText().toString().trim();

                    if (email.isEmpty() || password.isEmpty()) {
                        Toast.makeText(getApplicationContext(), "Ingrese su email y contraseña", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    AuthCredential credential = EmailAuthProvider.getCredential(email, password);

                    user.reauthenticate(credential).addOnCompleteListener(authTask -> {
                        if (authTask.isSuccessful()) {
                            db.collection("usuarios").document(userId).delete()
                                    .addOnCompleteListener(deleteTask -> {
                                        user.delete().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Intent intent = new Intent(Settings_DA.this, Login.class);
                                                startActivity(intent);
                                                Toast.makeText(getApplicationContext(), "Cuenta eliminada correctamente", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(getApplicationContext(), "Error al intentar eliminar cuenta: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(), "Autenticación fallida: " + authTask.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
