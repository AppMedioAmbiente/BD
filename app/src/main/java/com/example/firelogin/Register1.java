package com.example.firelogin;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register1 extends LoginTemplate implements View.OnClickListener {

    EditText etEmail, etPhone, etPassword, etRepeatPassword;
    Button btnRegister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email, phone, password, repPass, name, surname, birthdate;
    TextView tvEmailMsg, tvPhoneMsg, tvPassMsg, tvRepPassMsg, tvContactMsg;

    public void insertValues(String userId){
        Intent intent = getIntent();

        // Crea un objeto que deseas insertar (puede ser cualquier tipo de objeto o mapa)
        Map<String, Object> data = new HashMap<>();

        data.put("name", intent.getStringExtra("name"));
        data.put("surname", intent.getStringExtra("surname"));
        data.put("birthdate", intent.getStringExtra("birthdate"));
        // Inserta los datos usando el ID del usuario como el documento

        db.collection("usuarios")
                .document(userId)  // Usa el ID del usuario como clave del documento
                .set(data)
                .addOnSuccessListener(aVoid -> {
                    // Si la inserción es exitosa
                    showHome(ProviderType.BASIC,
                            email,name,surname,birthdate);
                    //showAlert("Firestore", "DocumentSnapshot added with ID: " + userId);
                })
                .addOnFailureListener(e -> {
                    // Si hay un error al insertar
                    failedRegister(e.toString());
                });
    }

    public boolean validateWithRegex(String field, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(field);

        return matcher.matches();
    }

    public HashMap checkFields(String email, String phone, String password, String repPass) {
        HashMap<String, String> msgs = new HashMap<>();
        String emailRegex = "^(?!.*\\.\\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String phoneRegex = "\\d{10,13}";
        String passRegex = "^(?!.*[\\/=\\\\?@\\[\\\\\\]^<>;:])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!\\-._])[\\w!\\-._]{6,}$";

        //if (!(email.isEmpty() && phone.isEmpty())) {
        if (email.isEmpty() || !validateWithRegex(email, emailRegex) || email.length() < 10|| email.length() > 150 ) {
            msgs.put("email", "Correo de entre 10 y 150 caracteres");
        }

        if (!phone.isEmpty() && !validateWithRegex(phone, phoneRegex)) {
            msgs.put("phone", "Número de teléfono de entre 10 y 13 números");
        }

        //} else msgs.put("contact", "Debe llenar correo o teléfono");

        if (password.isEmpty() || !validateWithRegex(password, passRegex) || password.trim().length() < 6 || password.trim().length() > 30) {
            msgs.put("password", "Mínimo 6 caracteres, máximo 30, con letra mayúscula, minúscula, número y caracter especial");
        }

        if (!password.equals(repPass)) {
            msgs.put("repPass", "Las contraseñas deben coincidir");
        }

        return msgs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register1);

        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);
        etRepeatPassword = findViewById(R.id.repeat_password);
        btnRegister = findViewById(R.id.send);
        tvEmailMsg = findViewById(R.id.emailMsg);
        tvPhoneMsg = findViewById(R.id.phoneMsg);
        tvPassMsg = findViewById(R.id.passwordMsg);
        tvRepPassMsg = findViewById(R.id.repPassMsg);
        //tvContactMsg = findViewById(R.id.contactMsg);

        btnRegister.setOnClickListener(this);
    }


    @Override
    public void onClick(View view) {
        /*if(!etRepeatPassword.getText().toString()
                .equals(etPassword.getText().toString())){
            showAlert("Error de Contraseña","Ambas Contraseñas deben coincidir");
            return ;
        }*/
        name = getIntent().getStringExtra("name");
        surname = getIntent().getStringExtra("surname");
        //String birthdateString = getIntent().getStringExtra("birthdate");

        birthdate=getIntent().getStringExtra("birthdate");
        /*
        birthdate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            birthdate = dateFormat.parse(birthdateString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        email = etEmail.getText().toString().replace(" ",""); // elimino espacios en el correo
        phone = etPhone.getText().toString().trim();
        password = etPassword.getText().toString();
        repPass = etRepeatPassword.getText().toString();

        HashMap msgs = checkFields(email, phone, password, repPass);
        if (!msgs.isEmpty()) {
            //if (msgs.containsKey("contact")) {
            //    tvContactMsg.setText((CharSequence) msgs.get("contact"));
            //} else tvContactMsg.setText("");

            if (msgs.containsKey("email")) {
                tvEmailMsg.setText((CharSequence) msgs.get("email"));
            } else tvEmailMsg.setText("");

            if (msgs.containsKey("phone")) {
                tvPhoneMsg.setText((CharSequence) msgs.get("phone"));
            } else tvPhoneMsg.setText("");

            if (msgs.containsKey("password")) {
                tvPassMsg.setText((CharSequence) msgs.get("password"));
            } else tvPassMsg.setText("");

            if (msgs.containsKey("repPass")) {
                tvRepPassMsg.setText((CharSequence) msgs.get("repPass"));
            } else tvRepPassMsg.setText("");

        } else {

            FirebaseAuth firebase = FirebaseAuth.getInstance();

            firebase.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(listener -> {
                        if (!listener.isSuccessful()) {
                            failedRegister(listener.getException().toString());
                            return;
                        }
                        insertValues(getUserId(firebase));
                    });
        }
    }
    private void failedRegister(String exception){
        showAlert("Registro","El registro ha fallado:"+exception);
    }
}

