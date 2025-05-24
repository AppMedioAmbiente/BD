package com.example.firelogin.ui.groups;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.firelogin.Firebase;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentGroupsBinding;

public class GroupsFragment extends Fragment {
    private FragmentGroupsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentGroupsBinding.inflate(inflater, container, false);

        //final TextView textView = binding.textGroups;
        //groupsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        View root= binding.getRoot();

        root.findViewById(R.id.btnAddGroup).setOnClickListener(view->{
            NavController navController = Navigation.findNavController(requireView());
            navController.navigate(R.id.GroupCreator);
            // startActivity();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
   /* private Boolean createGroup(){

    }*/
}