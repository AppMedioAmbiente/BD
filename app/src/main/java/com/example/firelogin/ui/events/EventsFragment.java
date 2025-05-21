package com.example.firelogin.ui.events;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.Create_Event;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentEventsBinding;
import com.example.firelogin.ui.events.EventsFragment;
import com.example.firelogin.ui.events.EventsViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventsFragment extends Fragment {
    private FragmentEventsBinding binding;
    FloatingActionButton btnAddEvent;

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

        //final TextView textView = binding.textEvents;
        //eventsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}