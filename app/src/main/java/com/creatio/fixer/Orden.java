package com.creatio.fixer;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.fixer.Adapters.ADOrden;
import com.creatio.fixer.Objects.OServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

public class Orden extends Fragment {
    public ListView list_orden;
    public TextView txtAlgoMas, txtTotal, txtNombre, txtTotalTime;
    ArrayList<OServices> arrServices = new ArrayList<>();
    ProgressDialog dialog;
    String ids = "", types = "";
    double total;
    int tiempo = 0;
    SharedPreferences sharedPref;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_orden, null);
        LayoutInflater myinflater = getActivity().getLayoutInflater();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //Elementos
        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.header_orden, list_orden, false);
        ViewGroup myFooter = (ViewGroup) myinflater.inflate(R.layout.footer_orden, list_orden, false);
        txtAlgoMas = (TextView) v.findViewById(R.id.txtAlgoMas);
        list_orden = (ListView) v.findViewById(R.id.list_orden);
        txtTotal = (TextView) myHeader.findViewById(R.id.txtTotal);
        txtTotalTime = (TextView) myHeader.findViewById(R.id.txtTotalTime);
        txtNombre = (TextView) myHeader.findViewById(R.id.txtNombre);
        //-----------------
        txtNombre.setText(sharedPref.getString("name", "No user") + " " + sharedPref.getString("last_name", "No user"));
        Button btnContinuar = (Button) myFooter.findViewById(R.id.btnContinuar);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).AbrirCalendar(String.valueOf(total));
            }
        });
        list_orden.addHeaderView(myHeader, null, false);
        list_orden.addFooterView(myFooter, null, false);
        txtAlgoMas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).Cerrar();
            }
        });
        v.setFocusableInTouchMode(true);
        v.requestFocus();

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        ((MainActivity) getActivity()).Cerrar();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        dialog = ProgressDialog.show(getActivity(), null, "Cargando orden");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                GetOrden();
            }
        }, 1500);
        super.onStart();
    }

    public void GetOrden() {
        ids = "";
        Map<String, ?> allEntries = sharedPref.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            if (entry.getKey().contains("id_service")) {
                ids += entry.getValue().toString() + ",";

            }
        }
        ids = ids.replaceFirst(".$", "");
        GetDataService(ids);

    }

    public void GetDataService(String id_service) {
        arrServices = new ArrayList<>();
        arrServices.clear();
        tiempo = 0;
        total = 0;//Se le suma la tarifa de trayecto
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "GetDataService")
                .addBodyParameter("id_service", id_service)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_service = object.optString("id_service");
                        String name = object.optString("name");
                        String image = object.optString("image");
                        String description = object.optString("description");
                        String time_pre = object.optString("time_pre");
                        String time_new = object.optString("time_new");
                        String type = object.optString("type");

                        double price = 0.0;
                        if (type.equalsIgnoreCase("0")) {
                            price = Float.parseFloat(time_new) * 2.23;
                            total += price;
                            tiempo += Integer.parseInt(time_new);
                        } else {
                            price = Float.parseFloat(time_pre) * 2.23;
                            total += price;
                            tiempo += Integer.parseInt(time_pre);
                        }

                        arrServices.add(new OServices(id_service, image, name, description, time_pre, time_new, type, ""));

                    }
                    dialog.dismiss();

                    if (total < 100) {
                        total = total + 40.0;
                    }
                    total = total * 1.16;
                    Log.e("total", total + "");
                    String totali = Helper.formatDecimal(total);
                    txtTotal.setText(totali);
                    txtTotalTime.setText("" + formatHoursAndMinutes(tiempo));
                    ADOrden adapter = new ADOrden(getActivity(), arrServices, "0", Orden.this, total);
                    list_orden.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("Orden error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Orden error", error.toString());
            }
        });

    }

    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }
}
