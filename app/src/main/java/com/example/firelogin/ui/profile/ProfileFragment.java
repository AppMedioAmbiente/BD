package com.example.firelogin.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentProfileBinding;
import com.example.firelogin.ui.profile.ProfileFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class ProfileFragment extends Fragment {

    View rootView ;
    private FragmentProfileBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ProfileViewModel profileViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        rootView=inflater.inflate(R.layout.settings_personaldata, container, false);

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        //final TextView textView = binding.textProfile;
        //profileViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    protected void updateUser(){
        // Referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referencia al documento en Firestore
        DocumentReference userRef = db.collection("users").document("userId123");

        // Crear un Map con los campos a actualizar o agregar
        Map<String, Object> updates = new HashMap<>();
        rootView.findViewById(R.id.stateOfResidence);
        updates.put("age", 25);  // Modificar o agregar el campo "age"
        updates.put("email", "nuevoemail@example.com");  // Modificar o agregar el campo "email"

        // Actualizar el documento con los nuevos valores
        userRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    // Se ha actualizado correctamente
                    Log.d("Firestore", "Documento actualizado con éxito!");
                })
                .addOnFailureListener(e -> {
                    // Ocurrió un error al actualizar
                    Log.w("Firestore", "Error al actualizar el documento", e);
                });

    }

    protected void deleteData(){
        // Referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referencia al documento
        DocumentReference userRef = db.collection("users").document("userId123");

        // Crear un Map para eliminar el campo "email"
        Map<String, Object> updates = new HashMap<>();
        updates.put("email", FieldValue.delete());

        // Actualizar el documento para eliminar el campo
        userRef.update(updates)
            .addOnSuccessListener(aVoid -> {
                // Campo eliminado con éxito
                Log.d("Firestore", "Campo eliminado con éxito!");
            })
            .addOnFailureListener(e -> {
                // Error al eliminar el campo
                Log.w("Firestore", "Error al eliminar el campo", e);
            });
    }
    protected void addNonExistentData(){
        // Referencia a Firestore
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Referencia al documento
        DocumentReference userRef = db.collection("users").document("userId123");

        // Crear un Map con los nuevos campos
        Map<String, Object> newField = new HashMap<>();
        newField.put("phone", "+1234567890");

        // Usar set() con merge para agregar solo si no existe
        userRef.set(newField, SetOptions.merge())
        .addOnSuccessListener(aVoid -> {
            // Campo agregado con éxito
            Log.d("Firestore", "Campo agregado con éxito!");
        })
        .addOnFailureListener(e -> {
            // Error al agregar el campo
            Log.w("Firestore", "Error al agregar el campo", e);
        });
    }
}