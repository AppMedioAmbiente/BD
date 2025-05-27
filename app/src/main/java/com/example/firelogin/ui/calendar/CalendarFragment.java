package com.example.firelogin.ui.calendar;

import static com.example.firelogin.StaticFunctions.showToastAlert;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentCalendarBinding;
import com.example.firelogin.ui.calendar.CalendarViewModel;
import com.google.firebase.firestore.Query;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    MaterialCalendarView calendarView;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
    List<EventDecorator> dates;
    private int lastDateIndex=0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCalendar;
        calendarViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        calendarView = root.findViewById(R.id.eventsCalendar);
        dates=new ArrayList<>();
        FirebaseHandler fb= new FirebaseHandler(2);
        fb.db.collection("eventos").orderBy("date", Query.Direction.DESCENDING).get()
                .addOnSuccessListener(task->{
                    task.getDocuments().forEach(item->{
                        String date = sdf.format(item.getDate("date"));
                        setEventDate(date,item.getId());
                    });
                });
        calendarView.setOnDateChangedListener((widget,selectedDate,selected)->{
            showToastAlert(requireContext(),"DATE SELECTED:"+selectedDate.toString());
        });
        return root;
    }
    private void setEventDate(String formatedDate,String eventId){
        if(!dates.isEmpty()) {
            for (int index = lastDateIndex; index < dates.size(); index++) {
                EventDecorator dateDecorator = dates.get(index);
                int dateReference = dateDecorator.isFromDate(formatedDate);
                if (dateReference == 1) {
                    dateDecorator.addEvent(eventId);
                    lastDateIndex = index;
                    return;
                }
            }
        }
        //no existe la fecha, es decir, es nueva para el calendario

        String[] splitedDate=formatedDate.split("/");// DD-MM-YYYY
        Calendar calendar=Calendar.getInstance();
        calendar.set(
                Integer.parseInt(splitedDate[2]),
                Integer.parseInt(splitedDate[1]),
                Integer.parseInt(splitedDate[0]));
        CalendarDay date = CalendarDay.from(calendar);
        EventDecorator decorator = new EventDecorator(Color.GREEN,
                Collections.singleton(date),
                formatedDate,eventId);
        dates.add(decorator);
        calendarView.addDecorator(decorator);
        lastDateIndex=dates.size()-1;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}