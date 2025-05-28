package com.example.firelogin.ui.calendar;

import static com.example.firelogin.StaticFunctions.print;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.example.firelogin.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class EventDecorator implements DayViewDecorator {
    private final int color;
    private final HashSet<CalendarDay> dates;
    private final Drawable backgroundDrawable;
    List<String> eventsId;
    String dateString;
    public EventDecorator(Context ctx, int color, Collection<CalendarDay> dates, String dateString, String eventId){
        this.color=color;
        this.dates=new HashSet<>(dates);
        eventsId=new ArrayList<>();
        addEvent(eventId);
        this.dateString=dateString;
        backgroundDrawable = DrawableCompat.wrap(ContextCompat.getDrawable(ctx, R.drawable.bg_personalizado)).mutate();
        try {
//        backgroundDrawable
            backgroundDrawable.setTint(color);
//        backgroundDrawable.setColor(color);
        }catch(Exception e){
            print("ERROR EN BG color:"+e.getMessage());
        }
    }
    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }
    @Override
    public void decorate(DayViewFacade view) {
        try {
//            view.addSpan(new DotSpan(10, color));
            view.addSpan(new ForegroundColorSpan(Color.WHITE));
            view.setBackgroundDrawable(backgroundDrawable);
        }catch(Exception e){
            print("ERROR EN DEORATE"+e.getMessage());
        }
    }
    public void addEvent(String eventId){
        eventsId.add(eventId);
    }
    public List<String> getEventsId(){
        return eventsId;
    }
//    public Boolean containsEvent(String eventId){
//        return eventsId.contains(eventId);
//    }
//    public int isFromDate(String date){
//        if(dateString.equals(date)){
//            return 1;
//        }
//        return (isBeforeMyDate(date))?-1:-2;
//    }
//    public Boolean isBeforeMyDate(String date){
//        int[] subDate = splitDate(date);
//        int[] subMyDate = splitDate(dateString);
//        if(subDate[2] < subMyDate[2]){
//            // pregunton es de antes de mi año
//            return true;
//        }
//        if(subDate[2]>subMyDate[2]){
//            // pregunton despues de mi año
//            return false;
//        }
//        //mismo año
//        if(subDate[1]<subMyDate[1]){
//            // pregunton es de antes de mi mes
//            return true;
//        }if(subDate[1]>subMyDate[1]){
//            // pregunton es despues de mi mes
//            return false;
//        }
//        //mismo mes
//        if(subDate[0]<=subMyDate[0]){
//            // pregunton es de antes de mi mes
//            return true;
//        }
//        return false;
//    }
//    private int[] splitDate(String date){
//        String[] subDate = date.split("/");
//        return new int[]{
//                parseInt(subDate[0]),
//                parseInt(subDate[1]),
//                parseInt(subDate[2])
//        };
//    }
//    private int parseInt(String value){
//        return Integer.parseInt(value);
//    }
}
