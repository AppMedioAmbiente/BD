package com.example.firelogin;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

interface ResultadoFb{
    void cuandoTermine(boolean exito);
}
interface ResultadoColeccion{
    void cuandoTermine(boolean exito, @Nullable DocumentSnapshot document);
}
interface ResultadoTask{
    void cuandoTermine( Task task);
}
public class Firebase extends Fragment {
    protected FirebaseAuth firebase;
    protected FirebaseUser user;
    protected FirebaseFirestore db;

    protected void iniciarFb(int nivel){
        firebase=FirebaseAuth.getInstance();
        user=firebase.getCurrentUser();
        if(nivel==2){
            db=FirebaseFirestore.getInstance();
        }

//        if(user!=null){
//            verificarValidezCuenta(true);
//        }
    }
    protected void verificarValidezCuenta(){
        verificarValidezCuenta(null);
    }
    protected void verificarValidezCuenta(@Nullable Boolean redireccionar) {
//        startActivity(new Intent(getApplicationContext(),MainMenuActivity.class));
//        finish();
//        return ;
        abrirColeccion("usuarios",(exito,documento)->{
            Boolean activo=false;
            if(!exito){
                Log.d("_viendo error_","sin exito");
                Map<String, Object> datos=new HashMap<>();
                datos.put("activo",true);

                //se ingresan valores a la Firebase Store
                insertarValores("usuarios",1,datos,(exito2)->{
                    Log.d("_viendo error_", "segunda insercion "+String.valueOf(exito2));
                });
                activo=true;
            }else{
                Log.d("_viendo error_","exito");
                activo= documento.getBoolean("activo");
            }

            if(activo!=null && !activo) {
                Log.d("_viendo error_", "el estado de cuenta es false");
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
    protected void abrirColeccion(String coleccion, ResultadoColeccion callback) {
        db.collection(coleccion).document(user.getUid()).get()
                .addOnSuccessListener(doc->{
                    if(doc.exists()){
                        callback.cuandoTermine(true,doc);
                    }else{
                        callback.cuandoTermine(false,null);
                    }
                })
                .addOnFailureListener(doc->callback.cuandoTermine(false,null));
    }
    protected void insertarValores(String colleccion,int modo,Map<String,Object> valores, ResultadoFb callback){
        db.collection(colleccion).document(user.getUid()).set(valores)
                .addOnSuccessListener(aVoid->callback.cuandoTermine(true)).
                addOnFailureListener(aVoid->callback.cuandoTermine(false));
    }
    protected void alerta(String texto){
        Toast.makeText(requireContext(),texto,Toast.LENGTH_SHORT).show();
        Log.d("alerta",texto);
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
