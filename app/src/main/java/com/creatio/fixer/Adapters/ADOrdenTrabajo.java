package com.creatio.fixer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.fixer.MainActivity;
import com.creatio.fixer.Objects.OServices;
import com.creatio.fixer.OrdenTrabajo;
import com.creatio.fixer.Pieces;
import com.creatio.fixer.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADOrdenTrabajo extends BaseAdapter {
    Context context;
    ArrayList<OServices> arrServices;
    String type;
    String id_sale;
    TextView txtPieces;
    String services,id_service_old, type_old, statusGral;

    public ADOrdenTrabajo(Context context, ArrayList<OServices> arrServices, String type, String id_sale, String services, String statusGral) {
        this.context = context;
        this.arrServices = arrServices;
        this.type = type;
        this.id_sale = id_sale;
        this.services = services;
        this.statusGral = statusGral;
    }

    @Override
    public int getCount() {
        return arrServices.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_orden_trabajo, parent, false);
        Button btnEdit = (Button) itemView.findViewById(R.id.btnEdit);
        Button btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
        txtPieces = (TextView) itemView.findViewById(R.id.txtPiece);
        btnEdit.setVisibility(View.GONE);

        if (type.equalsIgnoreCase("1") && statusGral.equalsIgnoreCase("5")){
            btnEdit.setVisibility(View.VISIBLE);
        }
        TextView txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        TextView txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        txtTitle.setText(arrServices.get(position).getTitle());
        if (arrServices.get(position).getType().equalsIgnoreCase("0")) {
            //Nuevo
            txtDesc.setText(arrServices.get(position).getDesc() + "\nInstalación nueva");
            btnPrice.setText(arrServices.get(position).getTime_new() + " MINS");
        } else {
            //Preinstalación
            txtDesc.setText(arrServices.get(position).getDesc() + "\nPreinstalación");
            btnPrice.setText(arrServices.get(position).getTime_pre() + " MINS");
        }

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                v.setId(position);
                id_service_old = arrServices.get(v.getId()).getId_service();
                type_old = arrServices.get(v.getId()).getType();
                final PopupMenu popup = new PopupMenu(context, v);
                popup.getMenuInflater().inflate(R.menu.popup,
                        popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        switch (item.getItemId()) {
                            case R.id.edit: {

                                LoadCatServ("0");

                                break;
                            }
                            case R.id.delete: {
                                Toast.makeText(context, "Se ha eliminado el servicio" + " : " + arrServices.get(v.getId()).getTitle(), Toast.LENGTH_LONG).show();
                                arrServices.remove((int) v.getTag());
                                notifyDataSetChanged();
                                break;
                            }
                            case R.id.pieces: {
                                Intent intent = new Intent(context, Pieces.class);
                                intent.putExtra("id_service", arrServices.get(v.getId()).getId_service());
                                intent.putExtra("id_sale", id_sale);
                                intent.putExtra("name_service", arrServices.get(v.getId()).getTitle());
                                context.startActivity(intent);
                                break;
                            }
                            default:
                                break;
                        }

                        return true;
                    }
                });
            }
        });
        try {
            JSONArray object2 = new JSONArray(arrServices.get(position).getPieces());
            for (int i = 0; i < object2.length(); i++) {
                JSONObject object = object2.getJSONObject(i);
                String name = object.optString("name");
                String price = object.optString("price");
                txtPieces.append(name + "\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemView;
    }


    String id_service;
    String type_new = "";
    public void LoadCatServ(String t) {
        final ArrayList<OServices> list = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/LoadCatServ")
                .addBodyParameter("type", t)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++){
                        JSONObject object = response.getJSONObject(i);
                        String id_service = object.optString("id_service");
                        String name = object.optString("name");
                        String description = object.optString("description");
                        String image = object.optString("image");
                        String time_pre = object.optString("time_pre");
                        String time_new = object.optString("time_new");
                        list.add(new OServices(id_service, image, name, description, time_pre, time_new,"",""));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.alert_edit);

                final TextView txtServicio = (TextView) dialog.findViewById(R.id.txtServicio);
                Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                final AutoCompleteTextView actv;
                final AutoCompleteTextView actv2;

                actv = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView);
                actv2 = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView2);


                final ArrayAdapter<OServices> adapter = new ArrayAdapter<OServices>
                        (context, android.R.layout.simple_list_item_1, list);
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
                                .addBodyParameter("type","1")
                                .addBodyParameter("id_service", services.getId_service())
                                .build().getAsJSONArray(new JSONArrayRequestListener() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.e("Responce array", response.toString());
                                try {
                                    for (int i = 0; i < response.length(); i++){
                                        JSONObject object = response.getJSONObject(i);
                                        String id_service = object.optString("id_service");
                                        String name = object.optString("name") +
                                                " Cat: " + object.optString("categoria");
                                        String description = object.optString("description");
                                        String image = object.optString("image");
                                        String time_pre = object.optString("time_pre");
                                        String time_new = object.optString("time_new");
                                        list2.add(new OServices(id_service, image, name, description, time_pre, time_new,"",""));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final ArrayAdapter<OServices> adapter2 = new ArrayAdapter<OServices>
                                        (context, android.R.layout.simple_list_item_1, list2);
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
//                        list.get(position).setDesc(actv2.getText().toString());
//                        list.get(position).setTitle(actv2.getText().toString());
//
//                        notifyDataSetChanged();
                        final Dialog dialog = new Dialog(context);
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
                                ((OrdenTrabajo)context).LeerServicios();
                            }
                        });
                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                type_new = "1";
                                UpdateServices();
                                ((OrdenTrabajo)context).LeerServicios();
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
    public void UpdateServices() {
        services = services.replace(id_service_old + "|" + type_old, id_service + "|"+ type_new);
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdateServices")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("services",services)
                .addBodyParameter("id_sale",id_sale)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                ((OrdenTrabajo)context).LeerServicios();
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
}
