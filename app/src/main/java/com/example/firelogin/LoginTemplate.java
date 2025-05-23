package com.example.firelogin;

import static com.example.firelogin.StaticFunctions.*;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;

public class LoginTemplate extends AppCompatActivity {

    public FirebaseUser user;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseHandler fb;
    protected Map<String, Object> data;
    
    protected String getUserId(){
        return (user != null) ? user.getUid() : null;
    }
    protected void showHome(Context context) {
        print("show home in "+context);
        return ;
//        Intent home=new Intent(this,Home.class);
//        startActivity(home);
    }
    protected Object chageDataType(Object value, String type){
        if(type==null){
            return value;
        }
        if(type.equals("int")){
            return Integer.parseInt((String) value);
        }
        return value;
    }
    protected Map readTemporalyData(){
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        String values = prefs.getString("Data", null);
        String[] valuesSplited;
        if(values==null){
            return null;
        }
        valuesSplited=values.split(",");

        Map<String, Object> data = new HashMap<>();
        for (int index=0;index<valuesSplited.length;index++){
            //ej values[0]="name:{name}"
            // name,surname,birthdate,country,state
            String[] splitValues=valuesSplited[index].split(":");
            // print(valuesSplited[index]+"---->"+splitValues.toString()+"-->"+splitValues.length);
            data.put(splitValues[0],chageDataType(splitValues[1], (splitValues.length==3)?splitValues[2]:null));
        }
        return data;
    }
    protected void deleteTemporalyData(){
        SharedPreferences prefs = getSharedPreferences("UserData", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.remove("Data");
        editor.apply(); // o editor.commit();
    }
    public void insertValues(String userId){
        // Crea un objeto que deseas insertar (puede ser cualquier tipo de objeto o mapa)
        // Map<String, Object> data = readTemporalyData();
        if(data==null){
            showToastAlert(this,"no se guardaron los datos");
            return;
        }
        // Inserta los datos usando el ID del usuario como el documento
        fb.insertarValores("usuarios",userId,data,(exito,doc,ex)->{
            if(!exito){
                failedRegister(ex.getMessage());
                return;
            }
            print("se insertaron los valores al ID"+userId);
            deleteTemporalyData();
//            getUserData();
            showHome(this);
        });
    }
    protected void failedRegister(String exception){
        showAlert(this,"Registro","El registro ha fallado:"+exception);
    }

    public void onAuthStateChanged (){
        print("AUTH STATE CHANGED");
        fb.updateUser((exito,task)->{
            user=fb.getUser();
            if(user == null){
                print("user=null");
                return;
            }
            data=readTemporalyData();
            if(data==null){
                print("DATA=null");
                getUserData();
                return;
            }
            if (user.isEmailVerified()) {
                // Usuario verificado, permitir acceso
                showToastAlert(this,"Usuario verificado");
                insertValues(getUserId());

            } else {
                // Usuario no verificado, mostrar advertencia o cerrar sesión
                showToastAlert(this,"Debes verificar tu correo electrónico");
//            fb.firebase.signOut();
            }
        });
    }
    protected void getUserData(){
        Log.d("referencia","getUserData");

        if(user==null){
            print("El user es nulo");
            return ;
        }
        fb.abrirDocumento("usuarios",getUserId(),(exito,doc)->{
            print("Existe el doc?"+String.valueOf(doc.exists()));
            if(exito){
                showHome(this);
            }else{
                print("El documento fallo");
            }
        });
    }
    protected void setMAuthListener(){
        mAuthListener = firebaseAuth -> {
            onAuthStateChanged();
        };
    }
    @Override
    protected void onStart() {
        super.onStart();
        print("ON START");
        if(mAuthListener!=null && fb!=null && fb.firebase!=null) {
            fb.firebase.addAuthStateListener(mAuthListener);
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        print("ON STOP");
        if (mAuthListener != null && fb!=null && fb.firebase!=null) {
            fb.firebase.removeAuthStateListener(mAuthListener);
        }
    }
}
