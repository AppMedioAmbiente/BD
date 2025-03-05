package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

enum ProviderType{
    BASIC,
    FACEBOOK,
    GOOGLE
}
public class Usuario extends AppCompatActivity {

    TextView tvNames, tvSurnames, tvBirthdate, tvContact;
    Button logOut;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario);

        tvNames = (TextView) findViewById(R.id.names);
        tvSurnames = (TextView) findViewById(R.id.surnames);
        tvBirthdate = (TextView) findViewById(R.id.birthdate);
        tvContact = (TextView) findViewById(R.id.contact);

        Intent intent = getIntent();

        String names = getExtraString(intent.getStringExtra("name"));
        String surnames = getExtraString(intent.getStringExtra("surname"));
        String birthdate = getExtraString(intent.getStringExtra("birthdate"));
        //Log.d("datos",intent.getDataString());

        String contact = getExtraString(intent.getStringExtra("contact"));

        tvNames.setText(tvNames.getText()+names);
        tvSurnames.setText(tvSurnames.getText()+surnames);
        tvBirthdate.setText(tvBirthdate.getText()+birthdate);
        tvContact.setText(tvContact.getText()+contact);

        logOut= findViewById(R.id.btnLogOut);
        logOut.setOnClickListener(l->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Usuario.this,Login.class));
        });
    }
    private String getExtraString(String value){
        if(value==null){
            return "__Campo Vacio__";
        }
        return value;
    }

    private void showAlert(String titulo,String mensaje){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

// Configura el titulo.
        alertDialogBuilder.setTitle(titulo);

// Configura el mensaje.
        alertDialogBuilder
                .setMessage(mensaje)
                .setCancelable(false)
                .setPositiveButton("Cerrar",null)
                .create().show();
    }
}

