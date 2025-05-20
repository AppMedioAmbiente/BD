package com.example.firelogin.ui.groups;

import android.app.appsearch.observer.SchemaChangeInfo;
import android.content.Intent;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.Home;
import com.example.firelogin.Manifest;
import com.example.firelogin.R;
import com.example.firelogin.databinding.FragmentGroupsCreatorBinding;
import com.example.firelogin.settings.Settings_PD;
import com.example.firelogin.ui.countryTemplate.CountrySelector;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Groups_Creator extends Fragment {
    private FragmentGroupsCreatorBinding binding;
    List<String> optionsSelected = new ArrayList<>();
    View countryS;
    FirebaseHandler fb;
    CountrySelector cs ;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        GroupsViewModel groupsViewModel =
                new ViewModelProvider(this).get(GroupsViewModel.class);

        binding = FragmentGroupsCreatorBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        LinearLayout catContainer = root.findViewById(R.id.categoryContainer);
        fb = new FirebaseHandler(2);
    

        fb.abrirDocumento("usuarios",fb.getUser().getUid(),(exito,doc)->{
            String color= String.valueOf(ContextCompat.getColor(requireContext(),R.color.purple_200));
            if(exito && doc.exists()){
                cs=new CountrySelector(color,
                        doc.get("country").toString(),
                        doc.get("state").toString());
            }else{
                cs=new CountrySelector(color);
            }
            getChildFragmentManager()
                    .beginTransaction()
                    .replace(R.id.countryContainer,cs)
                    .commit();
        });
        fb.abrirColeccion("event_type",(exito,query)->{
            if(exito){
                fb.print("Consulta exitosa");

                for(DocumentSnapshot doc:query.getDocuments()){
                    fb.print(doc.getData().toString());
                    CheckBox cb = new CheckBox(requireContext());
                    cb.setText(doc.get("type").toString());
                    cb.setId(Integer.parseInt(doc.get("id_type").toString()));

                    cb.setOnCheckedChangeListener((buttonView,isChecked)->{
                        if(isChecked){
                            optionsSelected.add(String.valueOf(buttonView.getId()));
                        }else{
                            optionsSelected.remove(optionsSelected.indexOf(String.valueOf(buttonView.getId())));
                        }
                    });

                    TextView desc= new TextView(requireContext());
                    desc.setText(doc.get("examples").toString());
                    desc.setPadding(
                            changePxToDp(40),
                            changePxToDp(2),
                            changePxToDp(2),
                            changePxToDp(2));
                    catContainer.addView(cb);
                    catContainer.addView(desc);
                }
            }
        });
        Button createGroup=root.findViewById(R.id.btnCreateGroup);
        if(createGroup!=null) {
            createGroup.setOnClickListener(view -> {
                EditText name = root.findViewById(R.id.name);
                EditText description = root.findViewById(R.id.description);
                String[] selection = cs.getSelection();
                print("nombre:" + name.getText().toString());
                print("nombre:" + description.getText().toString());
//            print("selecteds:"+String.join(",",optionsSelected.toArray(new String[0])));
                print("country:" + selection[0]);
                print("state:" + selection[1]);
//            alerta(String.join(",",optionsSelected.toArray(new String[0])));
                createGroup(name.getText().toString(), description.getText().toString(),
                        selection[0], selection[1]);
            });
            // Button home= root.findViewById(R.id.goHome);
            // home.setOnClickListener(view->{
            //     requireParentFragment().getActivity()
            //             .startActivityFromFragment(Groups_Creator.this,
            //                     new Intent(requireContext(), Home.class),100);
            // });
        }
        return root;
    }
    private Boolean validarDatos(String name,String description){
        if(name==null || name.isEmpty()){
            alerta("Debes dar asignar un nombre al grupo");
            return false;
        }
        if(description==null || description.isEmpty()){
            alerta("El grupo debe tener una descripcion");
            return false;
        }
        if(optionsSelected.size()==0){
            alerta("Debes seleccionar al menos una categoria");
            return false;
        }
        return true;
    }
    public int changePxToDp(int px){
        float scale= requireContext().getResources().getDisplayMetrics().density;
        return (int) (px*scale +0.5f);
    }
    private Boolean createGroup(String name,String description, String Country, String State){
//        String selectedCad=optionsSelected.toArray().toString();
//        String categories=String.join(",",optionsSelected.toArray(new String[0]);
        if(!validarDatos(name,description)){
            return false;
        }
        Timestamp date = new Timestamp(new Date());
        fb.abrirColeccionBuscando("groups","name",name,(exists,doc_existente)->{
            if(exists){
                alerta("ya existe el grupo");
                return;
            }
            /*
* Los grupos sirven para avisar a sus miemebros sobre eventos relacionados a el,asi como brindar un chat
* ¿Los miembros deben compartir pais? estado?
    yo creo que al menos deberian compartir pais, y preferentemente estado
    o lo dejo libre, pero ahora si se restrinje al enlistarse en un evento
    En caso sea libre...:¿Debería marcar diferencia entre los que comparten pais y los que no?
        seh, apareceran de otro color, para que si se enlista en un evento sepan que tal vez no asista
        por privacidad se manejaran solo 2 estilas, y no uno por pais
* Son necesarios los gruops?
    Tener grupos:
        ventajas:
            permite un sentimiento de comunidad
            chateas con gente de tu misma zona(preferentemente)
        desventajas:
            teniendo la opcion de notificaciones por zona y clasif una de las funcions de los grupos pierde sentido
            chat ocuparia mucho spacio en firebase, a menos que eso lo guardemos en otro lado
    Dejarlo en puras notifs:
        ventajas:
            * mas simple
            * ahorra espacio
        desventajas:
            a
* ¿Es automatica la compartida?
    si.... o no????
    Opciones:
        Auto
            ventajas:
                
                
            desventajas:
        Manual
            ventajas:
            desventajas:
* ¿Como?
    Cuando uno de sus miembros cree un evento que comparta una de las clasificaciones del grupo
    y se encuentre en la misma localizacion
* ¿ Avisa exclusivamente sobre los eventos de sus miembros ?
    no necesariamente, avisa de cualquier evento que comparta ubicacion y categoria(s)
    Agregar una opcion para solo compartir eventos de miembros
* ¿Como aumenta el porcentaje de asistencia?
    cuando un integrante asiste a un evento y ahi confirma su asistencia genera puntos positivos
    si no asiste genera puntos negativos
* ¿Como evitamos sabotaje de gente que venga a joder la asistencia?
    Cuenta unicamente a los miembros de mas de 7 dias
    ¿Permite expulsiones por votacion popular a los que tengan mucha inasistencia?
    ¿Eso lo dejamos a manos del admin del grupo?
        No creo, pues corrupcion
    De base,eventos deberían tener penalizaciones por inasistencia
* ¿Peermite desactivar notificaciones?
    No, pues esa es la finalidad
            * */
            fb.insertarValores("groups",null,fb.crearMap(new Object[][]{
                    {"name",name},
                    {"no_members",1},
                    {"description",description},
                    {"country",Country},
                    {"state",State},
                    {"categories",optionsSelected},
                    {"attendance_percentaje","--"},
                    {"date",date},
                    {"id_creator",fb.getUser().getUid()}
            }),(valuesInserted,docInserted,ex)->{
                if(valuesInserted){
                    alerta("se ingreso el grupo "+name);
//                    fb.insertarValores("group_has_users",null,fb.crearMap(new Object[][]{
//                            {"id_group",docInserted.getId()},{"id_user",fb.getUser().getUid()},{"date",date}
//                    }),(userInserted,docUser,userEx)->{
//                        if(!userInserted){
//                            alerta("no se pudo ingresar el miembro");
//                        }
//                    });
                }else{
                    alerta("no se pudo insertar el grupo");
                }
            });
        });


        return false;
    }
    private void clasifsBasic(){
        int index=2;
        for(String[] campo: new String[][]{
                {"Mantenimiento",
                        "Limpieza de espacios públicos , reparación de infraestructura, jornadas de reciclaje"},
                {"Restauración",
                        "Reforestación y plantación arboles, recuperación de ecosistemas"},
                {"Activismo",
                        "Marchas y manifestaciones, plantones y vigilias"},
                {"Apoyo comunitario",
                        "Entrega de alimentos o ropa, comedores sociales, apoyo a personas en situación vulnerable"},
                {"Educación",
                        "Talleres o charlas informativas"},
                {"Cultura Comunitaria",
                        "ferias y encuentros culturales"},
                {"Salud",
                        "jornadas médicas"},
                {"Capacitación",
                        "Entrenamiento para brigadas, cursos prácticos orientados al servicio comunitario"}}){
            fb.insertarValores("event_type",String.valueOf(index),fb.crearMap(new Object[][] {
                    {"id_type",index},{"type",campo[0]},{"examples",campo[1]} }),(exito,doc,ex)->{
                if(exito){
                    print("se insertó "+campo[0]);
                }
            });
            index+=1;
        }
    }
    protected void alerta(String texto){
        Toast.makeText(requireContext(),texto,Toast.LENGTH_SHORT).show();
        print(texto);
    }
    public void print(String text){
        Log.d("__my_sistema",text);
    }
};