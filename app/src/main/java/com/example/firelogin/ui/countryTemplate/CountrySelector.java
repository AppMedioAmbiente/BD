package com.example.firelogin.ui.countryTemplate;


import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.example.firelogin.R;
import com.example.firelogin.urlRequest.CountryTemplate;
import com.example.firelogin.urlRequest.CountryTemplateFragment;
public class CountrySelector extends CountryTemplateFragment {
    ColorStateList color;
    String country,state;
    public static interface createEv{
        void OnCreate(View root);
    }
    createEv postCreate;
    public CountrySelector(String color,String country,String state){
        getColor(color);
        this.country=country;
        this.state=state;
    }
    public CountrySelector(String color){
        getColor(color);
    }
    public CountrySelector(){}
    private void getColor(String color){
        if(color==null){
            return;
        }
        try{
            int colorValue=Integer.parseInt(color);
            this.color=ColorStateList.valueOf(colorValue);
        }catch(Exception ex){
            this.color= ColorStateList.valueOf(Color.parseColor(color));
        }
    }

    public void setAfterCreateEvent(createEv ev){
        this.postCreate=ev;
    }
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_country_selector, container, false);
        initCountryViews(R.id.select_country, R.id.select_state,
                country,state, root);
        cambiarColor();

        if(postCreate!=null){
            postCreate.OnCreate(root);
        }
        return root;
    }
    public void cambiarColor() {
        if (color != null) {
            countrySelector.setBackgroundTintList(color);
            stateSelector.setBackgroundTintList(color);
        }
    }

    public String[] getSelection(){
        getStateSelected();
        getCountrySelected();
        return new String[] {countrySelcted, stateSelected};
    }
}