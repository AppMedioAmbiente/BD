package com.example.firelogin.ui.events;

import static com.example.firelogin.StaticFunctions.showToastAlert;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.Create_Event;
import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.R;
import com.example.firelogin.cards.MyButtonsContainer;
import com.example.firelogin.databinding.FragmentEventsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class EventsFragment extends Fragment {
    private FragmentEventsBinding binding;
    FloatingActionButton btnAddEvent;
    LinearLayout eventsContainer;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebase = FirebaseAuth.getInstance();
    private final FirebaseUser user = firebase.getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventsContainer = root.findViewById(R.id.eventsContainer);
        btnAddEvent = root.findViewById(R.id.addEvent);
        btnAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent (getContext(), Create_Event.class);
            startActivity(intent);
        });

        loadEventsCards();

        //final TextView textView = binding.textEvents;
        //eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    public void loadEventsCards() {
        db.collection("eventos").get().addOnSuccessListener(documentSnapshot -> {
            documentSnapshot.getDocuments().forEach(event -> {
                View cardview = LayoutInflater.from(getContext()).inflate(R.layout.events_cardview, eventsContainer, false);

                DocumentReference organizerDoc = event.getDocumentReference("organizer");
                DocumentReference typeDoc = event.getDocumentReference("type");

                TextView name = cardview.findViewById(R.id.eventName);
                TextView location = cardview.findViewById(R.id.tvEventPlace);
                TextView date = cardview.findViewById(R.id.tvEventDate);
                TextView organizer = cardview.findViewById(R.id.tvEventOrganizer);
                TextView type = cardview.findViewById(R.id.tvEventType);
                Button joinEvent = cardview.findViewById(R.id.btnJoinEvent);

                name.setText(event.getString("event_name"));
                date.setText(sdf.format(event.getDate("date")));
                organizerDoc.get().addOnSuccessListener(doc -> {
                    organizer.setText(doc.getString("name"));
                });
                typeDoc.get().addOnSuccessListener(doc -> {
                    type.setText(doc.getString("type"));
                });

                db.collection("event_has_usuarios")
                        .whereEqualTo("id_event", db.collection("eventos").document(event.getId()))
                        .whereEqualTo("id_usuario", db.collection("usuarios").document(user.getUid()))
                        .get().addOnSuccessListener(querySnapshot -> {

                            if (querySnapshot.isEmpty()) {
                                //Log.d("Query Snapshot vacío: ", querySnapshot.toString());
                                joinEvent.setOnClickListener(v -> {
                                    Map<String, Object> data = new HashMap<>();
                                    data.put("id_event", db.collection("eventos").document(event.getId()));
                                    data.put("id_usuario", db.collection("usuarios").document(user.getUid()));

                                    db.collection("event_has_usuarios").add(data).addOnSuccessListener(s ->{
                                                joinEvent.setEnabled(false);
                                            })
                                            .addOnFailureListener(f -> {
                                                showToastAlert("Error al unirte al evento");
                                                Log.d("Error al unirse al evento: ", f.getMessage());
                                            });
                                });

                            } else {
                                //Log.d("Query Snapshot: ", querySnapshot.toString());
                                joinEvent.setEnabled(false);
                            }
                        });

                /*joinEvent.setOnClickListener(v -> {
                    Map<String, Object> data = new HashMap<>();
                    data.put("id_event", db.collection("eventos").document(event.getId()));
                    data.put("id_usuario", db.collection("usuarios").document(user.getUid()));

                    db.collection("event_has_usuarios").add(data).addOnSuccessListener(s ->{
                        joinEvent.setVisibility(View.GONE);
                    })
                            .addOnFailureListener(f -> {
                                showToastAlert("Error al unirte al evento");
                                Log.d("Error al unirse al evento: ", f.getMessage());
                            });
                });*/

                eventsContainer.addView(cardview);
            });
        })
                .addOnFailureListener(f -> {
                    showToastAlert("Error al obtener los datos");
                    Log.d("Error al obtener los eventos:", f.getMessage());
                });
    }

    protected void showToastAlert(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

//No. de personas en detalles del evento