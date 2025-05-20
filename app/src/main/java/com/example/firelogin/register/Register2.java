package com.example.firelogin.register;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.firelogin.FirebaseHandler;
import com.example.firelogin.LoginTemplate;
import com.example.firelogin.R;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register2 extends LoginTemplate implements View.OnClickListener {

    EditText etEmail, etPhone, etPassword, etRepeatPassword,etNickName;
    Button btnRegister;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String email, phone, password, repPass, name, surname, birthdate,nickName;
    TextView tvEmailMsg, tvPhoneMsg, tvPassMsg, tvRepPassMsg, tvContactMsg,tvNickNameMsg;
    FirebaseHandler fb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register2);

        etEmail = findViewById(R.id.email);
        etPhone = findViewById(R.id.phone);
        etPassword = findViewById(R.id.password);
        etRepeatPassword = findViewById(R.id.repeat_password);
        btnRegister = findViewById(R.id.send);
        tvEmailMsg = findViewById(R.id.emailMsg);
        tvPhoneMsg = findViewById(R.id.phoneMsg);
        tvPassMsg = findViewById(R.id.passwordMsg);
        tvRepPassMsg = findViewById(R.id.repPassMsg);
        etNickName = findViewById(R.id.nickname);
        tvNickNameMsg = findViewById(R.id.nicknameMsg);
        //tvContactMsg = findViewById(R.id.contactMsg);

        btnRegister.setOnClickListener(this);

        fb = new FirebaseHandler(2);
    }
    private void showErrorMessages(HashMap msgs){
        if (msgs.containsKey("nickname")) {
            tvNickNameMsg.setText((CharSequence) msgs.get("nickname"));
        } else tvNickNameMsg.setText("");

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
    }
    @Override
    public void onClick(View view) {

        email = etEmail.getText().toString().replace(" ",""); // elimino espacios en el correo
        phone = etPhone.getText().toString().trim();
        password = etPassword.getText().toString();
        repPass = etRepeatPassword.getText().toString();
        nickName = etNickName.getText().toString();

        HashMap msgs = checkFields(email, phone, password, repPass);
        if (!msgs.isEmpty()) {
            //if (msgs.containsKey("contact")) {
            //    tvContactMsg.setText((CharSequence) msgs.get("contact"));
            //} else tvContactMsg.setText("");
            showErrorMessages(msgs);

        } else {
            fb.firebase.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(listener -> {
                        if (!listener.isSuccessful()) {
                            failedRegister(listener.getException().toString());
                            return;
                        }
                        user = fb.firebase.getCurrentUser();
                        user.sendEmailVerification()
                                .addOnCompleteListener(verificationTask -> {
                                    if (verificationTask.isSuccessful()) {
                                        showToastAlert("Correo de verificación enviado");
                                    } else {
                                        showToastAlert("Error al enviar correo de verificación");
                                    }
                                });

                    });
        }
    }
    public void onAuthStateChanged (){

        if (user != null && user.isEmailVerified()) {
            // Usuario verificado, permitir acceso
            showToastAlert("USUario verificado");
            insertValues(getUserId());
        } else {
            // Usuario no verificado, mostrar advertencia o cerrar sesión
            showToastAlert("Debes verificar tu correo electrónico");
            fb.firebase.signOut();
        }

    }
    public void insertValues(String userId){
        Intent intent = getIntent();

        // Crea un objeto que deseas insertar (puede ser cualquier tipo de objeto o mapa)
        Map<String, Object> data = new HashMap<>();
        String[] values=intent.getStringExtra("values").split(",");
        for (int index=0;index<values.length;index++){
            //ej values[0]="name:{name}"
            // name, surname,birthdate,country,state
            String[] splitValues=values[index].split(":");
            data.put(splitValues[0],splitValues[1]);
        }
        data.put("phone",phone);
        data.put("nickname",nickName);
        data.put("accountStatus","active");//active, suspended,blocked,inactive,pending
        data.put("trust_rate","--");
        print("El id user es "+userId);
        // Inserta los datos usando el ID del usuario como el documento
        fb.insertarValores("usuarios",userId,data,(exito,doc,ex)->{
            if(!exito){
                failedRegister(ex.getMessage());
                return;
            }
            print("se insertaron los valores al ID"+userId);
            showHome();

        });
    }
    private void failedRegister(String exception){
        showAlert("Registro","El registro ha fallado:"+exception);
    }
    public boolean validateWithRegex(String field, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(field);

        return matcher.matches();
    }
    public HashMap checkFields(String email, String phone, String password, String repPass) {
        HashMap<String, String> msgs = new HashMap<>();
        String nicknameRegex = "[a-zA-Z0-9_]";
        String emailRegex = "^(?!.*\\.\\.)[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$";
        String phoneRegex = "\\d{10,13}";
        String passRegex = "^(?!.*[\\/=\\\\?@\\[\\\\\\]^<>;:])(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!\\-._])[\\w!\\-._]{6,}$";
        /*
        if (nickName.isEmpty() || !validateWithRegex(nickName, nicknameRegex) || nickName.length() < 8|| nickName.length() > 25) {
            msgs.put("nickname", "El nombre solo puede contener letras. numeros y guion bajo. Minimo 8 caracteres y maximo 25");
        }*/

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
}

