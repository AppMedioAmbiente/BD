package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
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
        /*
        String names = intent.getStringExtra("names");
        String surnames = intent.getStringExtra("surnames");
        String birthdate = intent.getStringExtra("provider");
        */
        String contact = intent.getStringExtra("contact");
        /*
        tvNames.setText(names);
        tvSurnames.setText(surnames);
        tvBirthdate.setText(birthdate);
         */
        tvContact.setText(contact);
        logOut= findViewById(R.id.btnLogOut);

        logOut.setOnClickListener(l->{
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Usuario.this,Login.class));
        });
    }

}
