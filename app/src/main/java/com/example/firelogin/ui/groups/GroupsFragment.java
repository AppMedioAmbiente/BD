package com.example.firelogin.ui.groups;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.Firebase;
import com.example.firelogin.databinding.FragmentGroupsBinding;
import com.example.firelogin.ui.groups.GroupsViewModel;

public class GroupsFragment extends Firebase {
    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentGroupsBinding.inflate(inflater, container, false);

        //final TextView textView = binding.textGroups;
        //groupsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        View root= binding.getRoot();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    //private Boolean createGroup(){

    //}
}