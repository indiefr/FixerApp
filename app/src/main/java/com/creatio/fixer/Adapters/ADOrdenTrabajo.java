package com.creatio.fixer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
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
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.creatio.fixer.Helper;
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
    String services, id_service_old, type_old, statusGral;

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
        ImageView imgConcepto = (ImageView) itemView.findViewById(R.id.imgConcepto);
        if (type.equalsIgnoreCase("1")) {
            btnEdit.setVisibility(View.VISIBLE);
        }
        TextView txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        TextView txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        txtTitle.setText(arrServices.get(position).getTitle());
        int t = Integer.parseInt(arrServices.get(position).getTime_new());
        String time = formatHoursAndMinutes(t);
        if (arrServices.get(position).getType().equalsIgnoreCase("0")) {
            //Nuevo
            txtDesc.setText(arrServices.get(position).getDesc() + "\nInstalación nueva");
            btnPrice.setText(time);
        } else {
            //Reinstalación
            txtDesc.setText(arrServices.get(position).getDesc() + "\nReinstalación");
            btnPrice.setText(time);
        }
        Glide.with(context)
                .load(arrServices.get(position).getImage())
                .error(R.drawable.tuberia_dummy)
                .into(imgConcepto);
        txtTitle.setText(arrServices.get(position).getTitle());
        if (statusGral.equalsIgnoreCase("0")){
            btnEdit.setVisibility(View.VISIBLE);
        }else{
            btnEdit.setVisibility(View.GONE);
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
                                if (arrServices.size() == 1) {
                                    Helper.ShowAlert(context, "No puedes dejar vacia la orden", "No se permite eliminar todos los servicios de la orden.", 0);
                                } else {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(context);

                                    alert.setTitle("¿Estás seguro de eliminar?");
                                    alert.setMessage("Se eliminará el servicio: " + arrServices.get(position).getTitle() + " id: " + id_service_old);
                                    alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Toast.makeText(context, "Se ha eliminado el servicio" + " : " + arrServices.get(position).getTitle(), Toast.LENGTH_LONG).show();
                                            arrServices.remove(position);
                                            UpdateServices("delete", id_service_old);
                                            notifyDataSetChanged();
                                        }
                                    });
                                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    alert.show();
                                }
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
                String price = object.optString("code");
                txtPieces.append(name + "\n" + price + "\n");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return itemView;
    }

    public static String formatHoursAndMinutes(int totalMinutes) {
        String minutes = Integer.toString(totalMinutes % 60);
        minutes = minutes.length() == 1 ? "0" + minutes : minutes;
        return (totalMinutes / 60) + ":" + minutes;
    }

    String id_service;
    String type_new = "";

    public void LoadCatServ(String t) {
        final ArrayList<OServices> list = new ArrayList<>();
        list.clear();
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "LoadCatServ")
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
                        list2.clear();
                        String url = "http://api.fixerplomeria.com/v1/";
                        if (Helper.debug) {
                            url = "http://apitest.fixerplomeria.com/v1/";
                        }
                        AndroidNetworking.post(url + "ChildrenServices")
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
                        btnCancelar.setText("Reinstalación");
                        // if button is clicked, close the custom dialog
                        btnAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                type_new = "0";
                                UpdateServices("edit", "");
                                ((OrdenTrabajo) context).LeerServicios();
                            }
                        });
                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                type_new = "1";
                                UpdateServices("edit", "");
                                ((OrdenTrabajo) context).LeerServicios();
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

    public void UpdateServices(String type, String id_service) {
        if (type.equalsIgnoreCase("delete")) {
            services = services.replace(id_service_old + "|" + type_old , "");
        } else {
            services = services.replace(id_service_old + "|" + type_old, id_service + "|" + type_new);
        }


        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "DeletePiecesFromService")
                .setPriority(Priority.MEDIUM)
                .addBodyParameter("id_service", id_service)
                .addBodyParameter("id_sale", id_sale)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                String url = "http://api.fixerplomeria.com/v1/";
                if (Helper.debug) {
                    url = "http://apitest.fixerplomeria.com/v1/";
                }
                AndroidNetworking.post(url + "UpdateServices")
                        .setPriority(Priority.MEDIUM)
                        .addBodyParameter("services", services)
                        .addBodyParameter("id_sale", id_sale)
                        .build().getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {
                        ((OrdenTrabajo) context).LeerServicios();
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
}
