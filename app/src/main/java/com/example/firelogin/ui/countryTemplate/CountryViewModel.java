package com.example.firelogin.ui.countryTemplate;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CountryViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public CountryViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is events fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}