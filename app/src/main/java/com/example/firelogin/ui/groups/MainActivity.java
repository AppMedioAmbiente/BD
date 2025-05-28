package com.example.firelogin.ui.groups;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
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
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    //private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private CollectionReference mensajesRef;

    private String nickname_msg;

    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;

    private LinearLayout chatLayout;
    private LinearLayout gruposLayout;  // Para ocultar la lista de grupos
    private Button btnJoin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_groups);

        // Referencias UI
        nombre = findViewById(R.id.nombre);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        // Referencias a los layouts
        chatLayout = findViewById(R.id.chatLayout);
        gruposLayout = findViewById(R.id.layoutGrupos);

        btnJoin = findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ocultar la lista de grupos
                gruposLayout.setVisibility(View.GONE);
                // Mostrar el chat
                chatLayout.setVisibility(View.VISIBLE);
            }
        });

        // Inicializar Firebase
        storage = FirebaseStorage.getInstance();
        firestore = FirebaseFirestore.getInstance();
        mensajesRef = firestore.collection("chat");

        // Configurar RecyclerView
        adapter = new AdapterMensajes(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rvMensajes.setLayoutManager(layoutManager);
        rvMensajes.setAdapter(adapter);

        // Escuchar nuevos mensajes en Firestore
        mensajesRef.orderBy("hora").addSnapshotListener((snapshots, e) -> {
            if (e != null) return;
            for (DocumentChange dc : snapshots.getDocumentChanges()) {
                if (dc.getType() == DocumentChange.Type.ADDED) {
                    MensajeRecibir m = dc.getDocument().toObject(MensajeRecibir.class);
                    adapter.addMensaje(m);
                }
            }
        });


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

        // Botón enviar texto
        btnEnviar.setOnClickListener(view -> {
            String texto = txtMensaje.getText().toString().trim();
            if (texto.isEmpty()) return;
            Log.d("nickname que recibe: ", nickname_msg);

            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("nombre", nickname_msg);
            mensaje.put("mensaje", texto);
            mensaje.put("type_mensaje", "1");
            mensaje.put("hora", FieldValue.serverTimestamp());

            mensajesRef.add(mensaje);
            txtMensaje.setText("");
        });

        // Ajustar scrollbar automático al agregar mensaje
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                rvMensajes.scrollToPosition(adapter.getItemCount() - 1);
            }
        });
    }
}
