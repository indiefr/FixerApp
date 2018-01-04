package com.creatio.fixer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MyAccount extends AppCompatActivity {
    private TextView lblName, lblLast, lblTel, lblEmail, lblCard;
    private LinearLayout lyCards;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        lblName = (TextView) findViewById(R.id.lblName);
        lblLast = (TextView) findViewById(R.id.lblLast);
        lblTel = (TextView) findViewById(R.id.lblTel);
        lblEmail = (TextView) findViewById(R.id.lblEmail);
        lyCards = (LinearLayout) findViewById(R.id.lyCards);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        ChangeData(pref);
        lblName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyAccount.this);
                builder.setTitle("Cambiar nombre de registro");
                builder.setMessage("Nombre Actual: " + pref.getString("name", "Sin registro"));

                final EditText input = new EditText(MyAccount.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_WORDS);

                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String m_Text = input.getText().toString();
                        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdateName")
                                .addBodyParameter("id_user", pref.getString("id_user", ""))
                                .addBodyParameter("name", m_Text)
                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("TAG RATE", response);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("name", m_Text);

                                editor.apply();
                                ChangeData(pref);

                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                builder.show();
            }
        });

        lblEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MyAccount.this, "El correo no puede ser editado.", Toast.LENGTH_SHORT).show();
            }
        });

        lblTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MyAccount.this);
                builder.setTitle("Asignación de número teléfonico");
                builder.setMessage("teléfono Actual: " + pref.getString("phone", "Sin registro"));

                final EditText input = new EditText(MyAccount.this);
                input.setInputType(InputType.TYPE_CLASS_PHONE);
                builder.setView(input);

                builder.setPositiveButton("Guardar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final String m_Text = input.getText().toString();
                        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdatePhone")
                                .addBodyParameter("id_user", pref.getString("id_user", ""))
                                .addBodyParameter("phone", m_Text)
                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("TAG RATE", response);
                                SharedPreferences.Editor editor = pref.edit();
                                editor.putString("phone", m_Text);

                                editor.apply();
                                ChangeData(pref);
                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });

                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();

                    }
                });

                builder.show();
            }
        });
        if (pref.getBoolean("conekta", false)) {
//            Toast.makeText(this, "Cuenta ligada correctamente al metodo de pago", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Sin metodo de pago registrado", Toast.LENGTH_SHORT).show();

        }

        setTitle("Mi cuenta");
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void ChangeData(SharedPreferences pref) {
        lblName.setText(pref.getString("name", "Empty") + " " + pref.getString("last_name", "Empty"));
        lblLast.setText(pref.getString("last_name", "Empty"));
        lblTel.setText(pref.getString("phone", "Sin teléfono registrado"));
        lblEmail.setText(pref.getString("email", "Empty"));
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/GetCards")
                .addBodyParameter("id_user", pref.getString("id_user", "0"))
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("desc",""+ response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject obj = response.getJSONObject(i);
                        String last4 = obj.optString("last4");
                        Log.e("Datos", last4);
                        String defaul = obj.optString("default");
                        View child = getLayoutInflater().inflate(R.layout.child, null);
                        lblCard = (TextView) child.findViewById(R.id.lblCard);
                        TextView lblDefault = (TextView) child.findViewById(R.id.lblDefault);
                        if (defaul.equalsIgnoreCase("true")){
                            lblDefault.setVisibility(View.VISIBLE);
                        }else{
                            lblDefault.setVisibility(View.INVISIBLE);
                        }
                        lblCard.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(MyAccount.this);
                                alert.setTitle("Nueva tarjeta");
                                alert.setMessage("¿Realmente quieres agregar un nuevo metodo de pago?");
                                alert.setPositiveButton("Si, agregar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent intent = new Intent(MyAccount.this, CardForm.class);
                                        intent.putExtra("oxxo", false);
                                        intent.putExtra("updateCard", true);
                                        startActivity(intent);
                                    }
                                });
                                alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {

                                    }
                                });
                                alert.show();
                            }
                        });
                        lblCard.setText("**** - " + last4);
                        lyCards.addView(child);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
        Toast.makeText(this, "Datos actualizados", Toast.LENGTH_SHORT).show();
    }
}
