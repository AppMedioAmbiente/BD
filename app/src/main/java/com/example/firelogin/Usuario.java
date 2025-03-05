package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

enum ProviderType{
    BASIC,
    FACEBOOK,
    GOOGLE
}
public class Usuario extends LoginTemplate {

    TextView tvNames, tvSurnames, tvBirthdate, tvContact;
    Button logOut;
    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuario);

        tvNames = findViewById(R.id.names);
        tvSurnames =findViewById(R.id.surnames);
        tvBirthdate = findViewById(R.id.birthdate);
        tvContact = findViewById(R.id.contact);

        Intent intent = getIntent();

        String names = getExtraString(intent.getStringExtra("name"));
        String surnames = getExtraString(intent.getStringExtra("surname"));
        String birthdate = getExtraString(intent.getStringExtra("birthdate"));
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
}

