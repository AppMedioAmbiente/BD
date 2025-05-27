package com.example.firelogin.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.Event_Details;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentHistoryBinding;
import com.example.firelogin.ui.history.HistoryViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class HistoryFragment extends Fragment {
    private FragmentHistoryBinding binding;
    LinearLayout eventsContainer;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth firebase = FirebaseAuth.getInstance();
    private final FirebaseUser user = firebase.getCurrentUser();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HistoryViewModel historyViewModel =
                new ViewModelProvider(this).get(HistoryViewModel.class);

        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        eventsContainer = root.findViewById(R.id.eventsContainer);
        loadHistoryCards();

        return root;
    }

    public void loadHistoryCards() {
        db.collection("event_has_usuarios").whereEqualTo("id_usuario", db.collection("usuarios").document(user.getUid()))
                .get().addOnSuccessListener(documentSnapshot -> {
                    documentSnapshot.getDocuments().forEach(eventUser -> {

                        eventUser.getDocumentReference("id_event").get().addOnSuccessListener(event -> {
                            if (event.getDocumentReference("status").getId().equals("3") || event.getDocumentReference("status").getId().equals("4")) {

                                View cardview = LayoutInflater.from(getContext()).inflate(R.layout.events_cardview, eventsContainer, false);
                                cardview.findViewById(R.id.btnJoinEvent).setVisibility(View.GONE);

                                DocumentReference organizerDoc = event.getDocumentReference("organizer");
                                DocumentReference typeDoc = event.getDocumentReference("type");

                                TextView name = cardview.findViewById(R.id.eventName);
                                TextView date = cardview.findViewById(R.id.tvEventDate);
                                TextView endDate = cardview.findViewById(R.id.tvEventEndDate);
                                TextView organizer = cardview.findViewById(R.id.tvEventOrganizer);
                                TextView type = cardview.findViewById(R.id.tvEventType);
                                Button eventDetails = cardview.findViewById(R.id.btnEventDetails);

                                name.setText(event.getString("event_name"));
                                date.setText(sdf.format(event.getDate("date")));
                                endDate.setText(sdf.format(event.getDate("end_date")));
                                organizerDoc.get().addOnSuccessListener(doc -> {
                                    organizer.setText(doc.getString("name"));
                                });
                                typeDoc.get().addOnSuccessListener(doc -> {
                                    type.setText(doc.getString("type"));
                                });

                                eventDetails.setOnClickListener(v -> {
                                    Intent intent = new Intent(getContext(), Event_Details.class);
                                    intent.putExtra("id_event", event.getId());
                                    intent.putExtra("fromFragment", "History");
                                    startActivity(intent);
                                });

                                eventsContainer.addView(cardview);
                            }
                        });
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