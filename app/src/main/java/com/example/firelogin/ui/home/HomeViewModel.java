package com.example.firelogin.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import org.imaginativeworld.whynotimagecarousel.model.CarouselItem;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<List<CarouselItem>> carouselItems = new MutableLiveData<>();

    public LiveData<List<CarouselItem>> getCarouselItems() {
        return carouselItems;
    }

    public void setCarouselItems(List<CarouselItem> items) {
        carouselItems.setValue(items);
    }

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        //mText.setValue("This is home gjhgjfghjgh");
    }

    public LiveData<String> getText() {
        return mText;
    }
}