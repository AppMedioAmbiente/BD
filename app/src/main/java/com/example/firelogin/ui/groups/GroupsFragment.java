package com.example.firelogin.ui.groups;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentGroupsBinding;

public class GroupsFragment extends Fragment {

    private FragmentGroupsBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentGroupsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        binding.btnJoinGroup.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), Groups.class);
            startActivity(intent);
        });


        root.findViewById(R.id.btnAddGroup).setOnClickListener(view->{
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.GroupCreator);
            // startActivity();
        });
        FirebaseHandler fb = new FirebaseHandler(2);
//        fb.abrirColeccion("");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
