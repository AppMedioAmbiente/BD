package com.example.firelogin.ui.events;

import static com.example.firelogin.StaticFunctions.showToastAlert;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.Create_Event;
import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.R;
import com.example.firelogin.cards.MyButtonsContainer;
import com.example.firelogin.databinding.FragmentEventsBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;

public class EventsFragment extends Fragment {
    private FragmentEventsBinding binding;
    FloatingActionButton btnAddEvent;
    private FirebaseHandler fb;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EventsViewModel eventsViewModel =
                new ViewModelProvider(this).get(EventsViewModel.class);

        binding = FragmentEventsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnAddEvent = root.findViewById(R.id.addEvent);
        btnAddEvent.setOnClickListener(view -> {
            Intent intent = new Intent (getContext(), Create_Event.class);
            startActivity(intent);
        });
        fb= new FirebaseHandler(2);
        ViewGroup cardContainer=root.findViewById(R.id.EventContainer);

        showToastAlert(requireContext(),"Obteniendo datos...");

        fb.abrirColeccion("eventos",(exito,values)->{
            if(!exito){
                showToastAlert(requireContext(),"No se pudieron obtener los eventos");
                return;
            }
            for(DocumentSnapshot document : values.getDocuments()){
                View card = inflater.inflate(R.layout.event_card, cardContainer, false);

                TextView name=card.findViewById(R.id.eventName);
                TextView place=card.findViewById(R.id.tvEventPlace);
                TextView date=card.findViewById(R.id.tvEventDate);
                TextView organizer=card.findViewById(R.id.tvEventOrganizer);

                changeTextViewValue(name,getFirebaseField(document.get("event_name")));
                changeTextViewValue(date,getFirebaseField(document.getDate("date")));

                MyButtonsContainer btnContainer = card.findViewById(R.id.actionContainer);
                Button details=btnContainer.findViewById(R.id.btnEventDetails);
                btnContainer.setFirebaseId(document.getId());
//                Button details=card.findViewById(R.id.btnEventDetails);
                details.setOnClickListener(view->{
                    MyButtonsContainer b = (MyButtonsContainer) view.getParent();
                    showToastAlert(requireContext(),"El id es"+b.getFirebaseId());
//                    showToastAlert(requireContext(),"El id es "+b.getTag(3));
                });

                GeoPoint geopint = document.getGeoPoint("location");
                Address adress = fb.getLocation(requireContext(), geopint.getLatitude(),geopint.getLongitude());
                changeTextViewValue(place,(adress!=null)?
                        adress.getAddressLine(0) : geopint.toString());


                DocumentReference org = document.getDocumentReference("organizer");
                if(org!=null) {
                    org.get().addOnSuccessListener(snapshot->{
                        if(snapshot.exists()) {
                            changeTextViewValue(organizer, getFirebaseField(snapshot.getString("nickname")));
                        }

                        cardContainer.addView(card);
                    });

                }
            }
        });


        //final TextView textView = binding.textEvents;
        //eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }
    private String getFirebaseField(Object valueFromSnapshot){
        if(valueFromSnapshot==null) return "";
        return valueFromSnapshot.toString();
    }
    private void changeTextViewValue(TextView view, String newValue){
        view.setText(view.getText()+":"+newValue);
    }
}

