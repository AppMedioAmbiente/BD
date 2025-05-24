package com.example.firelogin.cards;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

public class MyButtonsContainer extends LinearLayout {
    String firebaseId;

    public MyButtonsContainer(Context context) {
        super(context);
    }
    public MyButtonsContainer(Context context, AttributeSet attrs) {super(context, attrs);}
    public MyButtonsContainer(Context context, AttributeSet attrs, int defStyleAttr) {super(context, attrs, defStyleAttr);}
    public void setFirebaseId(String id) {
        firebaseId = id;
    }

    public String getFirebaseId() {
        return firebaseId;
    }
}
