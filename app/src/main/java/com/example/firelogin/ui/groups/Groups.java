package com.example.firelogin.ui.groups;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.firelogin.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.example.firelogin.ui.groups.MensajeEnviar;

public class Groups extends AppCompatActivity {

    private static final String TAG = "Groups ";
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private CollectionReference mensajesRef;

    private String nickname_msg;
    private List<MensajeRecibir> listMensaje = new ArrayList<>();


    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;

    private Button btnJoin;

    private void obtenerMensajes() {
        mensajesRef.orderBy("hora", Query.Direction.ASCENDING).get().addOnSuccessListener(queryDocumentSnapshots -> {
                    listMensaje.clear();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        MensajeRecibir m = doc.toObject(MensajeRecibir.class);
                        listMensaje.add(m);
                    }
                    adapter.notifyDataSetChanged();
                    rvMensajes.scrollToPosition(listMensaje.size() - 1);
                })
                .addOnFailureListener(e -> Log.e("Firestore", "Error obteniendo mensajes", e));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.groups);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            db.collection("usuarios").document(userId)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        nickname_msg = documentSnapshot.getString("nickname");
                        if (nickname_msg != null) {
                            // Do something with the nickname, like updating a UI element
                            Toast.makeText(this, "Nickname: " + nickname_msg, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Nickname not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(this, "Error al obtener datos", Toast.LENGTH_SHORT).show();
                    });
        }

        Log.d(TAG,"si senti el click");

        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.mensajeInput);
        btnEnviar = findViewById(R.id.btnEnviar);

        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mensajesRef = firestore.collection("chat");

        adapter = new AdapterMensajes(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(layoutManager);
        rvMensajes.setAdapter(adapter);

        mensajesRef.orderBy("hora").addSnapshotListener((snapshots, e) -> {
            if (e != null) return;
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    MensajeRecibir m = dc.getDocument().toObject(MensajeRecibir.class);
                    adapter.addMensaje(m);
                }
            }
        });

        btnEnviar.setOnClickListener(view -> {
            String texto = txtMensaje.getText().toString().trim();

            if (texto.isEmpty()) return;

            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("nombre", nickname_msg);
            mensaje.put("mensaje", texto);
            mensaje.put("type_mensaje", "1");
            mensaje.put("hora", FieldValue.serverTimestamp());

            mensajesRef.add(mensaje).addOnSuccessListener(documentReference -> {
                txtMensaje.setText("");

                obtenerMensajes();
            });
        });

        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }
}
