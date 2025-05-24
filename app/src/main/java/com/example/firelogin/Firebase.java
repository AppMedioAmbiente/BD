package com.example.firelogin;

import static com.example.firelogin.StaticFunctions.print;

import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;


public class Firebase extends Fragment {
    protected FirebaseAuth firebase;
    protected FirebaseUser user;
    protected FirebaseFirestore db;
    public static interface ResultadoFb{
        void cuandoTermine(boolean exito,DocumentReference document);
    }
    public static interface ResultadoColeccion{
        void cuandoTermine(boolean exito, @Nullable DocumentSnapshot document);
    }
    public static interface ResultadoQuery{
        void cuandoTermine(boolean exito, @Nullable QuerySnapshot document);
    }
    public static interface ResultadoTask{
        void cuandoTermine( Task task);
    }
    protected void iniciarFb(int nivel){
        firebase=FirebaseAuth.getInstance();
        user=firebase.getCurrentUser();
        if(nivel==2) {
            db = FirebaseFirestore.getInstance();
        }
//        if(user!=null){
//            verificarValidezCuenta(true);
//        }
    }
    protected Map crearMap(Object[][] valores){
//        new Object[][] {
//                {"nombre","paco"}
//        }
        Map map= new HashMap();
        for(Object[] atributo : valores ){
            map.put(atributo[0],atributo[1]);
        }
        return map;
    }
    protected void verificarValidezCuenta(){
        verificarValidezCuenta(null);
    }
    protected void verificarValidezCuenta(@Nullable Boolean redireccionar) {
//        startActivity(new Intent(getApplicationContext(),MainMenuActivity.class));
//        finish();
//        return ;
        abrirDocumento("usuarios",user.getUid(),(exito,documento)->{
            Boolean activo=false;
            if(!exito){
                print("no existe documento");
                Map<String, Object> datos=new HashMap<>();
                datos.put("activo",true);

                //se ingresan valores a la Firebase Store
                insertarValores("usuarios",user.getUid(),datos,(exito2,doc)->{
                    print("segunda insercion "+String.valueOf(exito2));
                });
                activo=true;
            }else{
                print("exito");
                activo= documento.getBoolean("activo");
            }

            if(activo!=null && !activo) {
                print("el estado de cuenta es false");
                //Esta suspendida la cuenta
                cerrarSesion();
                alerta("Tu cuenta fué suspendida");
                /*if (redireccionar!=null) {
                    // startActivity(new Intent(getApplicationContext(),Login_activity.class)); remplazar
                    finish();
                }*/
            }
            /*else{
                Log.d("_viendo error_","el estado de cuenta es true");
                // startActivity(new Intent(getApplicationContext(),MainMenuActivity.class)); remplazar
                finish();
            }*/
        });
    }
    protected void abrirDocumento(String coleccion,String docId,ResultadoColeccion callback) {
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
    protected void abrirColeccionBuscando(String coleccion,String campoId,String valorBuscando,ResultadoQuery callback) {
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
    protected void abrirColeccion(String coleccion,ResultadoQuery callback) {
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
    protected void insertarValores(String colleccion,String idDocument,Map<String,Object> valores, ResultadoFb callback) {
        if (idDocument == null) {
            db.collection(colleccion).add(valores)
                    .addOnSuccessListener(aVoid -> callback.cuandoTermine(true,aVoid)).
                    addOnFailureListener(aVoid -> callback.cuandoTermine(false,null));;
//            event.getResult().getId();
        } else {
            db.collection(colleccion).document(idDocument).set(valores)
                    .addOnSuccessListener(aVoid -> callback.cuandoTermine(true,null)).
                    addOnFailureListener(aVoid -> callback.cuandoTermine(false,null));
        }
    }
    protected void alerta(String texto){
        Toast.makeText(requireContext(),texto,Toast.LENGTH_SHORT).show();
        print(texto);
    }
    protected void cerrarSesion(){
        Log.d("_viendo error_","se cerró la sesion");
        firebase.signOut();
    }
    protected void reAutenticar(AuthCredential credencial, ResultadoTask task){
        if(credencial==null){
            return;
        }
        user.reauthenticate(credencial).addOnCompleteListener(AuthTask->{
            if(!AuthTask.isSuccessful()){
                alerta("No se pudo reautenticar");
                return;
            }
            task.cuandoTermine(AuthTask);
        });
    }
}
