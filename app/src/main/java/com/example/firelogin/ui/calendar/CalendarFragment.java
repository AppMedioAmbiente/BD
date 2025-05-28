package com.example.firelogin.ui.calendar;

import static com.example.firelogin.StaticFunctions.print;
import static com.example.firelogin.StaticFunctions.showAlert;
import static com.example.firelogin.StaticFunctions.showToastAlert;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentCalendarBinding;
import com.example.firelogin.ui.calendar.CalendarViewModel;
import com.example.firelogin.ui.events.EventsFragment;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CalendarFragment extends Fragment {
    private FragmentCalendarBinding binding;
    MaterialCalendarView calendarView;
    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    List<EventDecorator> dates;
    Map<String,EventDecorator> datesMap;
    EventsFragment eFragment = new EventsFragment();
    LinearLayout eContainer;
    private int lastDateIndex=0;
    int currentMonth=-1;
    Boolean eventsInCM=false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        CalendarViewModel calendarViewModel =
                new ViewModelProvider(this).get(CalendarViewModel.class);

        binding = FragmentCalendarBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textCalendar;
        calendarViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        calendarView = root.findViewById(R.id.eventsCalendar);
//        dates=new ArrayList<>();
        FirebaseHandler fb= new FirebaseHandler(2);
        datesMap=new HashMap<>();
        eContainer=root.findViewById(R.id.eventsContainer);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            currentMonth=LocalDate.now().getMonthValue();
        }
        String userId = fb.getUser().getUid();
        fb.db.collection("event_has_usuarios")
            .whereEqualTo("id_usuario",
                fb.db.collection("usuarios")
            .document(userId))
                .get().addOnCompleteListener(eventUser->{
                if(!eventUser.isSuccessful()) {
                    showToastAlert(requireContext(), "NO ESTAS REGISTRADO EN NINGUN EVENTO");
                    return;
                }
                for(DocumentSnapshot event : eventUser.getResult()){
                    event.getDocumentReference("id_event").get().addOnSuccessListener(item->{
                        if(item.exists()) {
                            String date = sdf.format(item.getDate("date"));
                            setEventDate(date, item.getId());
                        }else{
                            showToastAlert(requireContext(),"ESTA MMDA ESTÁ VACIA");
                        }
                    });
                }

        });

        calendarView.setOnDateChangedListener((widget,selectedDate,selected)->{
            try {
                String date = (sdf.format(selectedDate.getDate()));
                EventDecorator decorator = datesMap.get(reverseDate(date));
                if(decorator!=null) {
                    eContainer.removeAllViews();
                    List<String> eventos=decorator.getEventsId();

                    fb.db.collection("eventos")
                        .whereIn(FieldPath.documentId(),eventos) //maximo 10 eventos
                        .get().addOnSuccessListener(task->{
                            showToastAlert(requireContext(),"Se completó la consulta:"+(task.isEmpty()?"void":"lleno"));
                            task.getDocuments().forEach(doc->{
//                                    showToastAlert(requireContext(),"documento"+doc.toString());
                                eFragment.createCard(requireContext(),eContainer,doc,fb.db,null);
                            });
                    });
                }
            }catch (Exception e){
                showToastAlert(requireContext(),"ERROR:"+e.getMessage());
            }
        });
        return root;
    }
    private Boolean isARegisteredDate(String formatedDate,String eventId){
        EventDecorator decorator = datesMap.get(formatedDate);
        if(datesMap.size()!=0 && decorator!=null){
            //fecha ya habia aparecido antes
            decorator.addEvent(eventId);
            return true;
        }
//        if (!dates.isEmpty()) {
//            for (int index = lastDateIndex; index < dates.size(); index++) {
//                EventDecorator dateDecorator = dates.get(index);
//                int dateReference = dateDecorator.isFromDate(formatedDate);
//                if (dateReference == 1) {
//                    dateDecorator.addEvent(eventId);
//                    lastDateIndex = index;
//                    return true;
//                }
//            }
//        }
        return false;
    }
    private Calendar createCalendarObject(int[] splitedDate,int[] time){
        if(time==null){
            time=new int[]{0,0,0};
        }
        Calendar calendar = Calendar.getInstance();
        calendar.set( splitedDate[2], splitedDate[1]-1, splitedDate[0],
                time[0],time[1],time[2]);
        return calendar;
    }
    private void setEventDate(String formatedDate,String eventId){
        try {
            print("dateF="+formatedDate);
            if(isARegisteredDate(formatedDate,eventId)) return;
            //no existe la fecha, es decir, es nueva para el calendario

            int[] splitedDate = splitDate(formatedDate);// DD-MM-YYYY
            Calendar calendar=createCalendarObject(splitedDate,null);
//            Calendar calendar = Calendar.getInstance();
//            calendar.set( splitedDate[2], splitedDate[1]-1, splitedDate[0]);
            CalendarDay date = CalendarDay.from(calendar);
            EventDecorator decorator = new EventDecorator(
                    requireContext(),
                    Color.BLUE,
                    Collections.singleton(date),
                    formatedDate, eventId);
//            dates.add(decorator);
            datesMap.put(reverseDate(formatedDate),decorator);
            calendarView.addDecorator(decorator);
            lastDateIndex = dates.size() - 1;

            if(currentMonth!=-1 && !eventsInCM && currentMonth==splitedDate[1]){
                // se pudo obtener el mes
                // hay un evento en el mes actual
                // antes, eventsInCM era falso, es decir, es el primer evento registrado del mes
                eventsInCM=true;
            }
        }catch(Exception ex){
            print("ERROR:"+ex.getMessage());
        }
    }
    private String reverseDate(String date){
        String reversedDate="";
        for(int index=0;index<date.length();index++){
            char ch=date.charAt(index);
            if(ch!='-' && ch!='/') {
                reversedDate += ch;
            }
        }
        return reversedDate;
    }
    public Boolean isBeforeDate(String date,String dateString){
        int[] subDate = splitDate(date);
        int[] subMyDate = splitDate(dateString);
        if(subDate[2] < subMyDate[2]){
            // pregunton es de antes de mi año
            return true;
        }
        if(subDate[2]>subMyDate[2]){
            // pregunton despues de mi año
            return false;
        }
        //mismo año
        if(subDate[1]<subMyDate[1]){
            // pregunton es de antes de mi mes
            return true;
        }if(subDate[1]>subMyDate[1]){
            // pregunton es despues de mi mes
            return false;
        }
        //mismo mes
        if(subDate[0]<=subMyDate[0]){
            // pregunton es de antes de mi mes
            return true;
        }
        return false;
    }
    private int[] splitDate(String date){
        String[] subDate = date.split("/");
        return new int[]{
                Integer.parseInt(subDate[0]),
                Integer.parseInt(subDate[1]),
                Integer.parseInt(subDate[2])
        };
    }
//    private int parseInt(String value){
//        return Integer.parseInt(value);
//    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}