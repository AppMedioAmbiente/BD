package com.example.firelogin.ui.groups;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GroupsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GroupsViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is groups gjhgjfghjgh");
    }

    public LiveData<String> getText() {
        return mText;
    }
}