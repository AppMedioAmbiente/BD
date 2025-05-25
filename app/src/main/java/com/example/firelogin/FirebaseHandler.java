package com.example.firelogin;

import static com.example.firelogin.StaticFunctions.*;
import static com.example.firelogin.StaticFunctions.showToastAlert;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;


public class FirebaseHandler{
    public FirebaseAuth firebase;
    protected FirebaseUser user;
    public FirebaseFirestore db;
    public static interface ResultadoFb{
        void cuandoTermine(boolean exito,DocumentReference document,Exception ex);
    }
    public static interface ResultadoColeccion{
        void cuandoTermine(boolean exito, @Nullable DocumentSnapshot document);
    }
    public static interface ResultadoQuery{
        void cuandoTermine(boolean exito, @Nullable QuerySnapshot document);
    }
    public static interface ResultadoTask{
        void cuandoTermine( Boolean exito,Task task);
    }
    public static interface ResultadoVoid{
        void cuandoTermine( Boolean exito);
    }
    public FirebaseHandler(int nivel){
        firebase=FirebaseAuth.getInstance();
        user=firebase.getCurrentUser();
        if(nivel==2) {
            db = FirebaseFirestore.getInstance();
        }
//        if(user!=null){
//            verificarValidezCuenta(true);
//        }
    }
    public Map crearMap(Object[][] valores){
//        new Object[][] {
//                {"nombre","paco"}
//        }
        Map map= new HashMap();
        for(Object[] atributo : valores ){
            map.put(atributo[0],atributo[1]);
        }
        return map;
    }
    public void abrirDocumento(String coleccion,String docId,ResultadoColeccion callback) {
        db.collection(coleccion)
                .document(docId)
                .get()
                .addOnSuccessListener(doc->{
                    if(doc.exists()){
                        callback.cuandoTermine(true,doc);
                    }else{
                        callback.cuandoTermine(false,null);
                    }
                })
                .addOnFailureListener(doc->callback.cuandoTermine(false,null));
    }
    public void abrirColeccionBuscando(String coleccion,String campoId,String valorBuscando,ResultadoQuery callback) {
        db.collection(coleccion)
                .whereEqualTo(campoId, valorBuscando)
                .get()
                .addOnSuccessListener(doc->{
                        if(doc.isEmpty()){
                            //no se encontraron coincidencias
                            callback.cuandoTermine(true,null);
                        }
                        callback.cuandoTermine(true,doc);
                })
                .addOnFailureListener(doc->callback.cuandoTermine(false,null));
    }
    public void abrirColeccion(String coleccion, ResultadoQuery callback) {
        db.collection(coleccion)
                .get()
                .addOnSuccessListener(doc->{
                    if(doc.isEmpty()){
                        //no se encontraron coincidencias
                        callback.cuandoTermine(true,null);
                    }
                    callback.cuandoTermine(true,doc);
                })
                .addOnFailureListener(doc->callback.cuandoTermine(false,null));
    }
    public void insertarValores(String colleccion, String idDocument, Map<String, Object> valores, ResultadoFb callback) {
        if (idDocument == null) {
            print("El id es nulo");
            db.collection(colleccion).add(valores)
                    .addOnSuccessListener(aVoid -> callback.cuandoTermine(true,aVoid,null)).
                    addOnFailureListener(aVoid -> callback.cuandoTermine(false,null,null));;
//            event.getResult().getId();
        } else {
            print("El id tiene un valor");
            db.collection(colleccion).document(idDocument).set(valores)
                    .addOnSuccessListener(aVoid -> callback.cuandoTermine(true,null,null)).
                    addOnFailureListener(aVoid -> callback.cuandoTermine(false,null,aVoid));
        }
    }
    public void deleteUser(String password,ResultadoTask callback){
        reAutenticar(password,(exito,authTask)->{
            if(exito) {
                user.delete()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                callback.cuandoTermine(true, task);
                            } else {
                                callback.cuandoTermine(false, task);
                            }
                        });
                return;
            }
            callback.cuandoTermine(false,authTask);
        });

    }
    public FirebaseUser getUser(){
        if(user==null){
            user=firebase.getCurrentUser();
        }
        return user;
    }
    public void updateUser(ResultadoTask callback){
        user=firebase.getCurrentUser();
        if(user!=null) {
            user.reload().addOnCompleteListener(task->{
                if(task.isSuccessful()){
                    callback.cuandoTermine(true,task);
                    return;
                }
                callback.cuandoTermine(false,task);
            });
        }
    }
    public Address getLocation(Context context, double latitud, double longitud){
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> adreses = geocoder.getFromLocation(latitud,longitud,1);
            if(adreses !=null && !adreses.isEmpty()) {
                Address adress=adreses.get(0);

                String pais = adress.getCountryName(); //pais
                String estado = adress.getAdminArea(); //estado
                String ciudad = adress.getLocality(); // ciudad
                print("Geocoder","pais:"+pais+";estado:"+estado+";ciudad:"+ciudad);
                return adress;
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
    protected void cerrarSesion(){
        print("se cerró la sesion");
        firebase.signOut();
    }
    // Whiss.p3r
    protected Boolean reAutenticar(String password, ResultadoTask task){
        AuthCredential credencial=null;
        if(password!=null && user!=null){
            credencial = EmailAuthProvider.getCredential(user.getEmail(), password);
        }

        if(credencial==null){
            return false;
        }
        user.reauthenticate(credencial).addOnCompleteListener(AuthTask->{
            if(!AuthTask.isSuccessful()){
                print("No se pudo reautenticar");
                task.cuandoTermine(false,AuthTask);
                return;
            }
            task.cuandoTermine(true,AuthTask);
        });
        return true;
    }
}

//
//    protected void verificarValidezCuenta(){
//        verificarValidezCuenta(null);
//    }
//    protected void verificarValidezCuenta(@Nullable Boolean redireccionar) {
////        startActivity(new Intent(getApplicationContext(),MainMenuActivity.class));
////        finish();
////        return ;
//        abrirDocumento("usuarios",user.getUid(),(exito,documento)->{
//            Boolean activo=false;
//            if(!exito){
//                Log.d("_viendo error_","sin exito");
//                Map<String, Object> datos=new HashMap<>();
//                datos.put("activo",true);
//
//                //se ingresan valores a la Firebase Store
//                insertarValores("usuarios",user.getUid(),datos,(exito2,doc)->{
//                    Log.d("_viendo error_", "segunda insercion "+String.valueOf(exito2));
//                });
//                activo=true;
//            }else{
//                Log.d("_viendo error_","exito");
//                activo= documento.getBoolean("activo");
//            }
//
//            if(activo!=null && !activo) {
//                Log.d("_viendo error_", "el estado de cuenta es false");
//                //Esta suspendida la cuenta
//                cerrarSesion();
//                alerta("Tu cuenta fué suspendida");
//                /*if (redireccionar!=null) {
//                    // startActivity(new Intent(getApplicationContext(),Login_activity.class)); remplazar
//                    finish();
//                }*/
//            }
//            /*else{
//                Log.d("_viendo error_","el estado de cuenta es true");
//                // startActivity(new Intent(getApplicationContext(),MainMenuActivity.class)); remplazar
//                finish();
//            }*/
//        });
//    }