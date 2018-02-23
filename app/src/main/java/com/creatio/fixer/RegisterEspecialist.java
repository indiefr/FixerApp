package com.creatio.fixer;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;

public class RegisterEspecialist extends AppCompatActivity {
    private EditText edtName, edtLast, edtEmail, edtEdad, edtPhone, edtDesc;
    private Button btnRegister;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_especialist);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        edtName = findViewById(R.id.edtName);
        edtLast = findViewById(R.id.edtLast);
        edtEmail = findViewById(R.id.edtEmail);
        edtEdad = findViewById(R.id.edtEdad);
        edtPhone = findViewById(R.id.edtPhone);
        edtDesc = findViewById(R.id.edtDesc);
        btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtName.setError(null);
                edtLast.setError(null);
                edtEmail.setError(null);
                edtEdad.setError(null);
                edtPhone.setError(null);
                edtDesc.setError(null);
                boolean error = false;
                if (edtName.getText().toString().trim().equalsIgnoreCase("")) {
                    edtName.setError("Campo necesario");
                    error = true;
                }
                if (edtLast.getText().toString().trim().equalsIgnoreCase("")) {
                    edtLast.setError("Campo necesario");
                    error = true;
                }
                if (edtEmail.getText().toString().trim().equalsIgnoreCase("")) {
                    edtEmail.setError("Campo necesario");
                    error = true;
                }
                if (edtPhone.getText().toString().trim().equalsIgnoreCase("")) {
                    edtPhone.setError("Campo necesario");
                    error = true;
                }
                if (error) {
                    Helper.ShowAlert(RegisterEspecialist.this, "Tienes campos vacios", "Los campos de nombre, apellido, correo y teléfono son obligatorios", 0);

                } else {
                    CreateEspecialist();
                }
            }
        });
    }

    public void CreateEspecialist() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(RegisterEspecialist.this);
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/CreateSpecialistContratist")
                .addBodyParameter("update", "0")
                .addBodyParameter("name", edtName.getText().toString())
                .addBodyParameter("last_name", edtLast.getText().toString())
                .addBodyParameter("age", edtEdad.getText().toString())
                .addBodyParameter("email", edtEmail.getText().toString())
                .addBodyParameter("phone", edtPhone.getText().toString())
                .addBodyParameter("description", edtDesc.getText().toString())
                .addBodyParameter("contratist_relation", pref.getString("id_user","0"))
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("response",response);
                        if (response.contains("Actualizado")) {
                            Helper.ShowAlert(RegisterEspecialist.this, "Bien hecho", "El especialista ha sido creado, para ver la contraseña ve a mis especialistas en el menú.", 0);
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
    }

}
