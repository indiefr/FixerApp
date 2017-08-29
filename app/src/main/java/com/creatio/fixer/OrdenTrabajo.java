package com.creatio.fixer;

import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.creatio.fixer.Adapters.ADFechas;
import com.creatio.fixer.Adapters.ADHoras;
import com.creatio.fixer.Adapters.ADOrdenTrabajo;
import com.creatio.fixer.Objects.OCalendar;
import com.creatio.fixer.Objects.OServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;

public class OrdenTrabajo extends AppCompatActivity {
    public ListView list_orden;
    Bundle extras;
    ArrayList<OServices> arrServices;
    ADOrdenTrabajo adapterList;
    String type;
    String id_sale;
    TextView txtTitle, txtDate, txtNombre, txtOrden, txtSum;
    SharedPreferences sharedPref;
    Button btnComo;
    Button btnIniciar;
    CircularProgressButton btnAutorizar;
    String status_gral;
    String id_user;
    LinearLayout ly_espera;
    Spinner spFecha, spHora;
    ArrayList<OCalendar> listFechas = new ArrayList<>();
    ArrayList<OCalendar> listCalendar = new ArrayList<>();
    ViewGroup myFooter;
    String hour_date_service, service_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orden_trabajo);
        extras = getIntent().getExtras();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(OrdenTrabajo.this);
        //-------------------[ELEMENTS]-------------------------
        LayoutInflater myinflater = OrdenTrabajo.this.getLayoutInflater();
        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.header_orden_trabajo, list_orden, false);
        myFooter = (ViewGroup) myinflater.inflate(R.layout.footer_orden_trabajo, list_orden, false);

        LinearLayout btnAgregar = (LinearLayout) myFooter.findViewById(R.id.lyAgregar);
        btnAutorizar = (CircularProgressButton) myFooter.findViewById(R.id.btnAutorizar);
        txtNombre = (TextView) myHeader.findViewById(R.id.txtNombre);
        txtOrden = (TextView) myHeader.findViewById(R.id.txtOrden);
        txtSum = (TextView) myHeader.findViewById(R.id.txtSum);
        ly_espera = (LinearLayout) findViewById(R.id.ly_espera);
        spFecha = (Spinner) myFooter.findViewById(R.id.spFecha);
        spHora = (Spinner) myFooter.findViewById(R.id.spHora);
        LinearLayout ly_footer = (LinearLayout) findViewById(R.id.ly_footer);
        list_orden = (ListView) findViewById(R.id.list_orden);
        ImageButton btnClose = (ImageButton) findViewById(R.id.btnClose);
        btnComo = (Button) findViewById(R.id.btnComo);
        btnIniciar = (Button) findViewById(R.id.btnIniciar);
        type = extras.getString("type");
        id_sale = extras.getString("id_sale");
        txtDate = (TextView) findViewById(R.id.txtDate);
        txtTitle = (TextView) findViewById(R.id.txtTitle);
        //-----------------------[Actions]----------------------
        final List<String> fechasarr = new ArrayList<String>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDateandTime = sdf.format(new Date());
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(currentDateandTime));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        service_date = currentDateandTime;
        for (int i = 0; i < 10; i++) {
            c.add(Calendar.DATE, i);  // number of days to add, can also use Calendar.DAY_OF_MONTH in place of Calendar.DATE
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String output = sdf1.format(c.getTime());
            listFechas.add(new OCalendar("", output, "", "", "", "", "", "", null));

        }
        final ADFechas dataAdapter = new ADFechas(OrdenTrabajo.this, listFechas);

        spFecha.setAdapter(dataAdapter);
        spFecha.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                GetCalendar(listFechas.get(position).getName());
                service_date = listFechas.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        txtTitle.setText("Orden: " + id_sale);
        txtOrden.setText(id_sale);
        txtDate.setText("");

        txtNombre.setText(extras.getString("name_user", "No user") + " " + extras.getString("last_name_user", "No user"));
        btnAutorizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnAutorizar.startAnimation();

                new CountDownTimer(2000, 1000) {

                    public void onTick(long millisUntilFinished) {

                    }

                    public void onFinish() {


                        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                                R.drawable.ic_check);
                        btnAutorizar.doneLoadingAnimation(R.color.colorPrimaryDark, icon);
                        btnAutorizar.revertAnimation();
                        btnAutorizar.setVisibility(View.GONE);
                        LeerServicios();

                    }
                }.start();
                Helper.SendNotification(id_user, "Autorización", "Pido autorización para iniciar la orden: "
                        + id_sale + "\nCon fehca de reparación: "
                        + service_date
                        + " a las " + hour_date_service, "1");
                Helper.InitOrder(id_sale, "3");
                Helper.UpdateDateService(id_sale, hour_date_service, service_date);

            }
        });

        btnAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirAlert("0");
            }
        });
        ly_footer.setVisibility(View.GONE);
        if (type.equalsIgnoreCase("1")) {
            list_orden.addFooterView(myFooter, null, false);
            ly_footer.setVisibility(View.VISIBLE);
        }

        list_orden.addHeaderView(myHeader, null, false);

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnIniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnIniciar.setBackgroundResource(R.drawable.flat_danger);
                btnIniciar.setText("Finalizar");
                LeerServicios();
                if (status_gral.equalsIgnoreCase("1")) {
                    NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    nMgr.cancelAll();
                    Helper.InitOrder(id_sale, "2");
                    Helper.SendNotification(id_user, "Orden finalizada", "El técnico acaba de finalziar la orden " + id_sale, "1");
                } else {
                    NotificationCompat.Builder mBuilder =
                            new NotificationCompat.Builder(OrdenTrabajo.this)
                                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_notification_build))
                                    .setSmallIcon(R.drawable.ic_notification_build)
                                    .setContentTitle("Fixer")
                                    .setContentText("Tienes una orden activa");
                    Intent resultIntent = new Intent(OrdenTrabajo.this, OrdenTrabajo.class);
                    resultIntent.putExtra("type", "1");
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(OrdenTrabajo.this);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(resultIntent);
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    mBuilder.setSound(alarmSound);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_IMMUTABLE
                            );
                    mBuilder.setOngoing(true);
                    mBuilder.setContentIntent(resultPendingIntent);
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(101, mBuilder.build());
                    Helper.InitOrder(id_sale, "1");
                    Helper.SendNotification(id_user, "Orden iniciada", "El técnico acaba de iniciar la orden " + id_sale, "1");
                }
            }
        });
        LeerServicios();
    }

    public void GetCalendar(final String todaySend) {
        listCalendar = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/GetCalendar")
                .addBodyParameter("date", todaySend)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject object = response.getJSONObject(i);

                        String id_specialist = object.optString("id_specialist");
                        String init_date = object.optString("init_date");
                        String finish_date = object.optString("finish_date");
                        String commits = object.optString("commits");
                        String status = object.optString("status");
                        listCalendar.add(new OCalendar(id_specialist, "", "", "", "Orden: ", "", status, "", null));


                    }
                    ADHoras adapter = new ADHoras(OrdenTrabajo.this, listCalendar);
                    spHora.setAdapter(adapter);
                    spHora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            hour_date_service = String.valueOf(position + 1);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                } catch (JSONException e) {
                    Log.e("Login error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });

    }

    String id_service, type_new;

    public void AbrirAlert(String t) {
        final ArrayList<OServices> list = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/LoadCatServ")
                .addBodyParameter("type", t)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_service = object.optString("id_service");
                        String name = object.optString("name");
                        String description = object.optString("description");
                        String image = object.optString("image");
                        String time_pre = object.optString("time_pre");
                        String time_new = object.optString("time_new");
                        list.add(new OServices(id_service, image, name, description, time_pre, time_new, "", ""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Dialog dialog = new Dialog(OrdenTrabajo.this);
                dialog.setContentView(R.layout.alert_edit);

                final TextView txtServicio = (TextView) dialog.findViewById(R.id.txtServicio);
                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                final AutoCompleteTextView actv;
                final AutoCompleteTextView actv2;

                actv = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView);
                actv2 = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView2);


                final ArrayAdapter<OServices> adapter = new ArrayAdapter<OServices>
                        (OrdenTrabajo.this, android.R.layout.simple_list_item_1, list);
                actv.setAdapter(adapter);
                actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        OServices services = adapter.getItem(position);
                        actv2.setVisibility(View.VISIBLE);
                        txtServicio.setVisibility(View.VISIBLE);
                        actv2.requestFocus();
                        final ArrayList<OServices> list2 = new ArrayList<>();
                        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ChildrenServices")
                                .setPriority(Priority.MEDIUM)
                                .addBodyParameter("type", "1")
                                .addBodyParameter("id_service", services.getId_service())
                                .build().getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.e("Responce array", response.toString());
                                try {
                                    for (int i = 0; i < response.length(); i++) {
                                        JSONObject object = response.getJSONObject(i);
                                        String id_service = object.optString("id_service");
                                        String name = object.optString("name") +
                                                " Cat: " + object.optString("categoria");
                                        String description = object.optString("description");
                                        String image = object.optString("image");
                                        String time_pre = object.optString("time_pre");
                                        String time_new = object.optString("time_new");
                                        list2.add(new OServices(id_service, image, name, description, time_pre, time_new, "", ""));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final ArrayAdapter<OServices> adapter2 = new ArrayAdapter<OServices>
                                        (OrdenTrabajo.this, android.R.layout.simple_list_item_1, list2);
                                actv2.setAdapter(adapter2);
                                actv2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        OServices services = adapter2.getItem(position);
                                        id_service = services.getId_service();

                                    }
                                });
                                actv2.setThreshold(1);
                            }

                            @Override
                            public void onError(ANError anError) {
                                Log.e("Responce error array", anError.toString());
                            }
                        });
                    }
                });
                actv.setThreshold(1);


                // if button is clicked, close the custom dialog
                btnAceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
//                        arrServices.get(position).setDesc(actv2.getText().toString());
//                        arrServices.get(position).setTitle(actv2.getText().toString());
//
//                        notifyDataSetChanged();
                        final Dialog dialog = new Dialog(OrdenTrabajo.this);
                        dialog.setContentView(R.layout.alert_fixer);
                        // set the custom dialog components - text, image and button
                        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                        txtTitle.setText("Tipo de reparación");
                        txtMsj.setText("Selecciona el tipo de reparación que el especialista va a realizar.");

                        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                        btnAceptar.setText("Nueva");
                        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                        btnCancelar.setText("Preinstalación");
                        // if button is clicked, close the custom dialog
                        btnAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                type_new = "0";
                                UpdateServices();
                            }
                        });
                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                type_new = "1";
                                UpdateServices();

                            }
                        });
                        dialog.show();

                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
            }

            @Override
            public void onError(ANError anError) {
                Log.e("Responce array error", anError.toString());
            }
        });
    }

    String services;

    public void UpdateServices() {
        services = services + "," + id_service + "|" + type_new;
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdateServices")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("services", services)
                .addBodyParameter("id_sale", id_sale)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                LeerServicios();
            }

            @Override
            public void onError(ANError anError) {
                LeerServicios();
            }
        });
    }

    public void LeerServicios() {
        arrServices = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/GetServicesOrder")
                .addBodyParameter("id_sale", id_sale)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {
                Log.e("TAG0", response.toString());
                try {
                    String syncresponse = response.getString("data");
                    String status = response.getString("status");
                    services = response.getString("services");
                    id_user = response.getString("id_user");
                    String name_user = response.getString("user");
                    final String latlng = response.getString("latlng");
                    int sum = 0;
                    txtNombre.setText(name_user);
                    JSONArray object2 = new JSONArray(syncresponse);
                    for (int i = 0; i < object2.length(); i++) {
                        JSONObject object = object2.getJSONObject(i);
                        String json = object.getString("json");
                        String pieces = object.getString("pieces");
                        JSONArray arrjson = new JSONArray(json);
                        for (int j = 0; j < arrjson.length(); j++) {
                            JSONObject objectjson = arrjson.getJSONObject(j);
                            String id_service = objectjson.optString("id_service");
                            String name = objectjson.optString("name");
                            String description = objectjson.optString("description");
                            String time_new = objectjson.optString("time_new");
                            String time_pre = objectjson.optString("time_pre");
                            String type = objectjson.optString("type");
                            if (type.equalsIgnoreCase("0")) {
                                sum = sum + Integer.parseInt(time_new);
                            } else {
                                sum = sum + Integer.parseInt(time_pre);
                            }

                            String image = objectjson.optString("image");
                            arrServices.add(new OServices(id_service, image, name, description, time_pre, time_new, type, pieces));
                        }
                    }
                    txtSum.setText(sum + " MINS");
                    btnComo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                    Uri.parse("google.navigation:q=" + latlng));
                            startActivity(intent);
                        }
                    });
                    status_gral = status;
                    if (!status_gral.equalsIgnoreCase("5")) {
                        btnAutorizar.setVisibility(View.GONE);
                    }

                    if (status.equalsIgnoreCase("1")) {
                        btnIniciar.setBackgroundResource(R.drawable.flat_danger);
                        btnIniciar.setText("Finalizar");
                        myFooter.setVisibility(View.GONE);

                    }
                    if (status.equalsIgnoreCase("2")) {
                        btnIniciar.setBackgroundResource(R.drawable.flat_primary);
                        btnIniciar.setText("Finalizada");
                        btnIniciar.setEnabled(false);
                        myFooter.setVisibility(View.GONE);
                    }
                    if (status.equalsIgnoreCase("3")) {
                        btnIniciar.setBackgroundResource(R.drawable.flat_black);
                        btnIniciar.setText("Iniciar");
                        btnIniciar.setEnabled(false);
                        ly_espera.setVisibility(View.VISIBLE);
                        myFooter.setVisibility(View.GONE);
                    }
                    if (status.equalsIgnoreCase("5")) {
                        btnIniciar.setBackgroundResource(R.drawable.flat_black);
                        btnIniciar.setText("Iniciar");
                        btnIniciar.setEnabled(false);
                    }
                    if (status.equalsIgnoreCase("0")){
                        myFooter.setVisibility(View.GONE);
                    }
                    adapterList = new ADOrdenTrabajo(OrdenTrabajo.this, arrServices, type, id_sale, services, status_gral);
                    list_orden.setAdapter(adapterList);
                } catch (JSONException e) {
                    Log.e("Orden de trabajo error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Orden de trabajo error", error.toString());
            }
        });


    }


}
