package com.example.firelogin.ui.map;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MapViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public MapViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is map gjhgjfghjgh");
    }

    public LiveData<String> getText() {
        return mText;
    }
}