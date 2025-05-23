package com.example.firelogin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

public class StaticFunctions {
    static public void print(String message){
        Log.d("_DEBUG_",message);
    }
    static public void print(String message,String title){
        Log.d(title,message);
    }
    static public void showToastAlert(Context context, String message){
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
        print("in_:"+String.valueOf(context)+":"+message);
    }
    static public void showAlert(Context context,String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

        print(mensaje);
        // Configura el titulo.
        alertDialogBuilder.setTitle(titulo);

        // Configura el mensaje.
        alertDialogBuilder
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Cerrar",null)
                .create().show();
    }
    static public void showHome(){}
//    static public void showHome(Context context){
//        print("showHome in "+context);
//        Intent intent = new  Intent (context,Home.class);
//        startActivity(context,intent,null);
//    }
}
