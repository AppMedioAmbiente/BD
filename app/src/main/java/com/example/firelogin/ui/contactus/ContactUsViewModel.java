package com.example.firelogin.ui.contactus;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ContactUsViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public ContactUsViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is contact us gjhgjfghjgh");
    }

    public LiveData<String> getText() {
        return mText;
    }
}