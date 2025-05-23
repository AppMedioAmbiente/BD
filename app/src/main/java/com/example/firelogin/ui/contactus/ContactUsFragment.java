package com.example.firelogin.ui.contactus;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.databinding.FragmentContactUsBinding;
import com.example.firelogin.ui.contactus.ContactUsViewModel;

public class ContactUsFragment extends Fragment {
    private FragmentContactUsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ContactUsViewModel contactUsViewModel =
                new ViewModelProvider(this).get(ContactUsViewModel.class);

        binding = FragmentContactUsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textContactUs;
        //contactUsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}