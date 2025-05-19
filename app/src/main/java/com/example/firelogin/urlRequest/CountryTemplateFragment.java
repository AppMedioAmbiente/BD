package com.example.firelogin.urlRequest;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.firelogin.ui.countryTemplate.CountrySelector;
import com.mukesh.countrypicker.CountryPicker;
//rajas,1 rojo,  2 queso
import java.util.ArrayList;
import java.util.List;
public class CountryTemplateFragment extends Fragment  implements baseTemplates{
    protected TextView countrySelector;
    protected Spinner stateSelector;
    protected String countrySelcted,stateSelected;
    CountryTemplateFragment activity;
    Boolean onlyViewMode=true;
    protected void initCountryViews(
            int id_country_selector, int id_state_selector,
            String countryText, String stateText, View root
    ){
        this.activity=this;
        countrySelcted= (countryText !=null) ? countryText:"";
        stateSelected=(stateText !=null)?stateText:"";
        try{
            countrySelector = root.findViewById(id_country_selector);
            stateSelector = root.findViewById(id_state_selector);
//        (R.id.select_country,R.id.select_state)
            countrySelector.setOnClickListener(this::onClick);

            stateSelector.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (onlyViewMode) {
                        CountryHandler<CountryTemplateFragment> ch=new CountryHandler<>(activity, countrySelcted);
                        ch.getStates();
                        onlyViewMode = false;
                    }
                    return false;
                }
            });
            stateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(stateSelector.getSelectedItem()!=null) {
                        stateSelected = stateSelector.getSelectedItem().toString();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }catch(Exception ex) {
            Log.d("selctores",ex.getMessage().toString());
            Toast.makeText(requireContext(), "Error, no se encontraron los selectores", Toast.LENGTH_SHORT).show();
            return;
        }
        countrySelector.setText(countryText);

        List<String> defaultValue =new ArrayList<>() ;
        defaultValue.add(stateText);

        updateSpinner(defaultValue);
        stateSelector.setSelection(0);
    }
    private void onClick(View view) {
        CountryPicker picker = new CountryPicker.Builder().with(requireContext())
                .listener(country -> {
                    countrySelcted=country.getName();
                    countrySelector.setText(countrySelcted);
                    //spiner
                    Log.d("creacion","voy a crear en handler");
                    CountryHandler<CountryTemplateFragment> ch=new CountryHandler<>(activity, countrySelcted);
                    ch.getStates();
                    onlyViewMode=false;
                }).build();
        picker.showDialog(requireActivity());
    }
    public String getCountrySelected(){
        countrySelcted= (String) countrySelector.getText();
        return countrySelcted;
    }
    public String getStateSelected(){
        stateSelected=stateSelector.getSelectedItem().toString();
//        Toast.makeText(activity, "Estado:"+stateSelected, Toast.LENGTH_SHORT).show();
        return stateSelected;
    }
    public void updateSpinner(List<String> states){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                states
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSelector.setAdapter(adapter);
    }
    public void updateSpinner() {
        Toast.makeText(requireContext(), "Error al obtener los estados", Toast.LENGTH_SHORT).show();
    }
    public Boolean wasCountrySelected(){
        return (!countrySelcted.equals("Seleccionar un Pais"));
    }
    public void showToastAlert(String mensaje){
        Toast.makeText(requireContext(),mensaje,Toast.LENGTH_SHORT).show();
        Log.d("nuestro sistema",mensaje);
    }
}
