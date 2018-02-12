package com.creatio.fixer;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.fixer.Adapters.ADListOrden;
import com.creatio.fixer.Objects.OOrders;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Ordenes extends AppCompatActivity {
    private ListView list_orders;
    private SwipeRefreshLayout swipe;
    private FloatingActionButton floatingActionButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ordenes);
        list_orders = (ListView) findViewById(R.id.list_orders);
        swipe = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + "+526145460168"));
                startActivity(intent);
            }
        });
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        ReadOrders();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ReadOrders();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        ReadOrders();
        super.onResume();
    }

    public void ReadOrders() {
        final ArrayList<OOrders> list = new ArrayList<>();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ReadOrders")
                .addBodyParameter("id_user", id_user)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                swipe.setRefreshing(false);
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_order = object.optString("id_sale");
                        String create_on = object.optString("create_on");
                        String total = object.optString("total");
                        String subtotal = object.optString("subtotal");
                        String lat_lng = object.optString("lat_lng");
                        String init_date = object.optString("init_date");
                        String id_specialist = object.optString("id_spe");
                        String name = object.optString("name");
                        String name_user = object.optString("name_user");
                        String last_name = object.optString("last_name");
                        String last_name_user = object.optString("last_name_user");
                        String id_calendary = object.optString("id_calendary");
                        String status_sc = object.optString("status_sc");
                        String status_so = object.optString("status_so");
                        String id_user = object.optString("id_user");
                        String hour_date = object.optString("hour_date");
                        String hour_date_service = object.optString("hour_date_service");
                        String service_date = object.optString("service_date");
                        String rate = object.optString("ratef");
                        String reference = object.optString("reference");
                        list.add(new OOrders(id_order, create_on, total, subtotal, lat_lng, init_date, id_specialist, name + " " + last_name, id_calendary, name_user, last_name_user, status_sc, status_so, id_user, hour_date, hour_date_service, service_date, rate, reference));

                    }
                    ADListOrden adapter = new ADListOrden(Ordenes.this, list, "1");
                    list_orders.setAdapter(adapter);
                    list_orders.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String[] date = list.get(position).getInit_date().split(" ");

                            Intent i = new Intent(Ordenes.this, OrdenTrabajo.class);
                            i.putExtra("type", "0");
                            i.putExtra("id_sale", list.get(position).getId_order());
                            i.putExtra("latlng", list.get(position).getLat_lng());
                            i.putExtra("date", date[0] + " a las " + list.get(position).getHour_date() + " hrs.");
                            i.putExtra("hour", list.get(position).getHour_date_service());
                            startActivity(i);
                        }
                    });
                } catch (JSONException e) {
                    Log.e("Login error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                swipe.setRefreshing(false);
                Log.e("Login error", error.toString());
            }
        });
    }
}
