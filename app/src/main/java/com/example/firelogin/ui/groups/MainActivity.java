package com.example.firelogin.ui.groups;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.firelogin.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    private CircleImageView fotoPerfil;
    private TextView nombre;
    private RecyclerView rvMensajes;
    private EditText txtMensaje;
    private Button btnEnviar;
    private AdapterMensajes adapter;
    private ImageButton btnEnviarFoto;

    private FirebaseStorage storage;
    private StorageReference storageReference;
    private FirebaseFirestore firestore;
    private CollectionReference mensajesRef;

    private static final int PHOTO_SEND = 1;
    private static final int PHOTO_PERFIL = 2;

    private String fotoPerfilCadena = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Solo una llamada setContentView, usa el layout correcto para esta activity
        setContentView(R.layout.fragment_groups);

        // Referencias UI
        fotoPerfil = findViewById(R.id.fotoPerfil);
        nombre = findViewById(R.id.nombre);
        rvMensajes = findViewById(R.id.rvMensajes);
        txtMensaje = findViewById(R.id.txtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);
        btnEnviarFoto = findViewById(R.id.btnEnviarFoto);

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

        // Botón enviar texto
        btnEnviar.setOnClickListener(view -> {
            String texto = txtMensaje.getText().toString().trim();
            if (texto.isEmpty()) return;

            Map<String, Object> mensaje = new HashMap<>();
            mensaje.put("mensaje", texto);
            mensaje.put("nombre", nombre.getText().toString());
            mensaje.put("fotoPerfil", fotoPerfilCadena);
            mensaje.put("tipo", "1"); // tipo 1 = mensaje texto
            mensaje.put("hora", FieldValue.serverTimestamp());

            mensajesRef.add(mensaje);
            txtMensaje.setText("");
        });

        // Botón enviar foto
        btnEnviarFoto.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PHOTO_SEND);
        });

        // Cambiar foto perfil
        fotoPerfil.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/jpeg");
            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
            startActivityForResult(Intent.createChooser(intent, "Selecciona una foto"), PHOTO_PERFIL);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) return;

        Uri uri = data.getData();

        if (requestCode == PHOTO_SEND) {
            // Subir foto de chat
            storageReference = storage.getReference("imagenes_chat");
            final StorageReference fotoRef = storageReference.child(uri.getLastPathSegment());

            fotoRef.putFile(uri).addOnSuccessListener(taskSnapshot ->
                    fotoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        Map<String, Object> mensaje = new HashMap<>();
                        mensaje.put("mensaje", nombre.getText().toString() + " te ha enviado una foto");
                        mensaje.put("foto", downloadUri.toString());
                        mensaje.put("nombre", nombre.getText().toString());
                        mensaje.put("fotoPerfil", fotoPerfilCadena);
                        mensaje.put("tipo", "2"); // tipo 2 = foto
                        mensaje.put("hora", FieldValue.serverTimestamp());

                        mensajesRef.add(mensaje);
                    })
            );

        } else if (requestCode == PHOTO_PERFIL) {
            // Subir foto de perfil
            storageReference = storage.getReference("foto_perfil");
            final StorageReference fotoRef = storageReference.child(uri.getLastPathSegment());

            fotoRef.putFile(uri).addOnSuccessListener(taskSnapshot ->
                    fotoRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        fotoPerfilCadena = downloadUri.toString();

                        Map<String, Object> mensaje = new HashMap<>();
                        mensaje.put("mensaje", nombre.getText().toString() + " ha actualizado su foto de perfil");
                        mensaje.put("foto", downloadUri.toString());
                        mensaje.put("nombre", nombre.getText().toString());
                        mensaje.put("fotoPerfil", fotoPerfilCadena);
                        mensaje.put("tipo", "2"); // tipo 2 = foto
                        mensaje.put("hora", FieldValue.serverTimestamp());

                        mensajesRef.add(mensaje);
                        Glide.with(this).load(downloadUri.toString()).into(fotoPerfil);
                    })
            );
        }
    }
}
