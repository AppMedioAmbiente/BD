package com.example.firelogin;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Context;
import android.content.Intent;
import android.nfc.Tag;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

public class StaticFunctions {
    static public void print(String message){
        Log.e("_DEBUG_",message);
    }
    static public void print(String message,String title){
        Log.d("_DEBUG_"+title,message);
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
    public static int updateSpinner(Context ctx, Object[][] values,Spinner spinner){
        if(ctx ==null || spinner==null || values==null){
            return -1;
        }
        try {
            List<SpinnerOption> types = new ArrayList<>();
            types.add(new SpinnerOption(-1, "Seleccionar"));

            for (Object[] value : values) {
                if (value.length != 2) {
                    return -2;
                }
                print(String.valueOf(values[1]) );
                types.add(new SpinnerOption(value[0], String.valueOf(value[1]) ));
            }
            ArrayAdapter<SpinnerOption> adapter = new ArrayAdapter<>(ctx, android.R.layout.simple_spinner_item, types);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            return 1;
        }catch(Exception ex){
            print("EXCEPCION",ex.getMessage());
            return -3;
        }
    }
//    static public void showHome(Context context){
//        print("showHome in "+context);
//        Intent intent = new  Intent (context,Home.class);
//        startActivity(context,intent,null);
//    }
}
