package com.creatio.fixer.Adapters;

import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.bumptech.glide.Glide;
import com.creatio.fixer.Helper;
import com.creatio.fixer.MainActivityPlo;
import com.creatio.fixer.Objects.OMySpecialist;
import com.creatio.fixer.Objects.OOrders;
import com.creatio.fixer.OrdenTrabajo;
import com.creatio.fixer.R;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Layge on 03/07/2017.
 */


public class ADNew extends BaseSwipeAdapter {
    Context context;
    ArrayList<OOrders> list;
    String specialist = "";

    public ADNew(Context context, ArrayList<OOrders> list) {
        this.context = context;
        this.list = list;
    }


    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.item;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return getCount();
    }

    @Override
    public View generateView(final int position, final ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_new, parent, false);
        final LinearLayout ly_services = (LinearLayout) itemView.findViewById(R.id.ly_services);
        Button btnDeclinar = (Button) itemView.findViewById(R.id.btnDeclinar);
        Button btnProgramar = (Button) itemView.findViewById(R.id.btnProgramar);
        Button btnOrden = (Button) itemView.findViewById(R.id.btnOrden);
        TextView txtService, txtName, txtFecha, txtFechaDet;
        txtService = (TextView) itemView.findViewById(R.id.txtService);
        txtName = (TextView) itemView.findViewById(R.id.txtName);
        txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
        txtFechaDet = (TextView) itemView.findViewById(R.id.txtFechaDet);

        txtService.setText("Orden: " + list.get(position).getId_order());
        txtName.setText("Cliente: " + list.get(position).getName());
        final SwipeLayout swipeLayout = (SwipeLayout) itemView.findViewById(R.id.item);
        swipeLayout.setShowMode(SwipeLayout.ShowMode.LayDown);
        //swipeLayout.addDrag(SwipeLayout.DragEdge.Right, itemView.findViewById(R.id.bottom_wrapper));
        //swipeLayout.addDrag(SwipeLayout.DragEdge.Left, itemView.findViewById(R.id.bottom_wrapper2));
        final LinearLayout row = (LinearLayout) itemView.findViewById(R.id.row);
        swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {

            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        final ExpandableLayout expandable = (ExpandableLayout) itemView.findViewById(R.id.expandable_layout);
        expandable.setDuration(500);
        ImageButton btnExpand = (ImageButton) itemView.findViewById(R.id.btnExpand);
        ObjectAnimator.ofFloat(btnExpand, "rotation", 0, 180).start();
        if (list.get(position).getStatus_sc().equalsIgnoreCase("1")) {
            btnExpand.setVisibility(View.GONE);
        }
        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandable.isExpanded()) {
                    expandable.collapse();
                    ObjectAnimator.ofFloat(v, "rotation", 0, 180).start();
                } else {
                    LeerServicios(ly_services, list.get(position).getId_order());
                    expandable.expand();
                    ObjectAnimator.ofFloat(v, "rotation", 0, 0).start();
                    String getMapURL = "https://maps.googleapis.com/maps/api/staticmap?center=" + list.get(position).getLat_lng() + "&zoom=13&size=600x300&maptype=roadmap\n" +
                            "&markers=size:mid%7Ccolor:red%7C" + list.get(position).getLat_lng() +
                            "&key=AIzaSyCmCTuVwOWD6tgfwnSmdLTN74IAtpMjGEU";
                    Log.e("Map", getMapURL);
                    ImageView ivUrl = (ImageView) itemView.findViewById(R.id.img_mapa);

                    Glide.with(context)
                            .load(getMapURL)
                            .error(R.drawable.ic_fixer_logo)
                            .placeholder(R.drawable.ic_fixer_logo)
                            .into(ivUrl);
                }

            }
        });
        btnDeclinar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateCalendary(list.get(position).getId_calendary(), "2", list.get(position).getId_user());
                //Pasar a otro usuario...
                Snackbar.make(v, "Declinaste una reparación de tu agenda", Snackbar.LENGTH_LONG)
                        //.setActionTextColor(Color.CYAN)
                        .setActionTextColor(context.getResources().getColor(R.color.colorPrimary))
                        .setAction("Deshacer", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                UpdateCalendary(list.get(position).getId_calendary(), "0", list.get(position).getId_user());
                            }
                        })
                        .show();
            }
        });
        btnProgramar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                if (pref.getString("is_contratist", "0").equalsIgnoreCase("0")) {
                    Helper.InitOrder(list.get(position).getId_order(), "0");
                    UpdateCalendary(list.get(position).getId_calendary(), "1", list.get(position).getId_user());
                    Snackbar.make(v, "Añadiste una reparación a tu agenda", Snackbar.LENGTH_LONG)
                            //.setActionTextColor(Color.CYAN)
                            .setActionTextColor(context.getResources().getColor(R.color.colorPrimary))
                            .setAction("Deshacer", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    UpdateCalendary(list.get(position).getId_calendary(), "0", list.get(position).getId_user());
                                }
                            })
                            .show();
                    ((MainActivityPlo) context).LeerOrdenes();
                } else {
                    final ArrayList<OMySpecialist> listspe = new ArrayList<>();
                    listspe.clear();

                    String url = "http://api.fixerplomeria.com/v1/";
                    if (Helper.debug) {
                        url = "http://apitest.fixerplomeria.com/v1/";
                    }
                    AndroidNetworking.post(url + "LoadMySpecialists")
                            .addBodyParameter("id_user", pref.getString("id_user", "0"))
                            .setPriority(Priority.MEDIUM)
                            .build().getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.e("response ", response.toString());
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject object = response.getJSONObject(i);
                                    String id_specialist = object.optString("id_specialist");
                                    String name = object.optString("name");
                                    String last_name = object.optString("last_name");
                                    String email = object.optString("email");

                                    listspe.add(new OMySpecialist(id_specialist, name, last_name, name + " " + last_name, "", email, ""));
                                }
                            } catch (JSONException e) {
                                Log.e("error ", e.toString());
                            }

                            //Asignar a un especialista
                            final Dialog dialog = new Dialog(context);
                            dialog.setContentView(R.layout.alert_asing);

                            Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                            Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                            final AutoCompleteTextView actv;


                            Spinner spinner = (Spinner) dialog.findViewById(R.id.spinner);
                            ADMySpeSelect adapter = new ADMySpeSelect(context, listspe);
                            spinner.setAdapter(adapter);
                            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                    specialist = listspe.get(i).getId_specialis();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> adapterView) {
                                    specialist = "nada";
                                }
                            });
                            btnAceptar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    AlertDialog.Builder alert = new AlertDialog.Builder(context);
                                    alert.setTitle("¿Quieres continuar?");
                                    alert.setMessage("Se asignará con estatus de programado al especialista que eligiste");
                                    alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int j) {

                                            if (specialist.equalsIgnoreCase("nada")) {
                                                Helper.ShowAlert(context, "Atención", "Debes de seleccionar un especialista", 0);
                                                return;
                                            }
                                            final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                                            String url = "http://api.fixerplomeria.com/v1/";
                                            if (Helper.debug) {
                                                url = "http://apitest.fixerplomeria.com/v1/";
                                            }
                                            AndroidNetworking.post(url + "AsingToSpecialist")
                                                    .addBodyParameter("id_specialist", specialist)
                                                    .addBodyParameter("id_calendary", list.get(position).getId_calendary())
                                                    .setPriority(Priority.MEDIUM)
                                                    .build().getAsString(new StringRequestListener() {
                                                @Override
                                                public void onResponse(String response) {
                                                    Helper.InitOrder(list.get(position).getId_order(), "0");
                                                    UpdateCalendary(list.get(position).getId_calendary(), "1", list.get(position).getId_user());

                                                }

                                                @Override
                                                public void onError(ANError error) {
                                                    Log.e("Error WriteLog", error.toString());
                                                }
                                            });

                                        }
                                    });
                                    alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {

                                        }
                                    });
                                    alert.show();
                                    dialog.dismiss();
                                }
                            });

                            btnCancelar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
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
            }

        });
        btnOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrdenTrabajo.class);
                i.putExtra("type", "0");
                i.putExtra("id_sale", list.get(position).getId_order());
                i.putExtra("name_user", list.get(position).getName_user());
                i.putExtra("last_name_user", list.get(position).getLast_name_user());
                context.startActivity(i);
            }
        });
        String date = list.get(position).getInit_date();
        String[] fechas = date.split(" ");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = null;
        try {
            testDate = sdf.parse(fechas[0]);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        String newFormat = formatter.format(testDate);
        txtFecha.setText(newFormat + " - " + list.get(position).getHour_date() + " hrs.");
        SimpleDateFormat formatter2 = new SimpleDateFormat("EEEE, d MMMM");
        String newFormat2 = formatter2.format(testDate);
        txtFechaDet.setText(newFormat2 + " - " + list.get(position).getHour_date() + " hrs.");
        return itemView;
    }

    @Override
    public void fillValues(int position, View convertView) {

    }


    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private void LeerServicios(final LinearLayout ly_services, String id_order) {
        ly_services.removeAllViews();

        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "GetServicesOrder")
                .addBodyParameter("id_sale", id_order)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONObject(new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String syncresponse = response.getString("data");
                    JSONArray object2 = new JSONArray(syncresponse);
                    for (int i = 0; i < object2.length(); i++) {
                        JSONObject object = object2.getJSONObject(i);
                        String json = object.getString("json");
                        JSONArray jsonarray = new JSONArray(json);
                        for (int j = 0; i < jsonarray.length(); j++) {
                            JSONObject objectjson = jsonarray.getJSONObject(j);
                            String id_service = objectjson.optString("id_service");
                            String name = objectjson.optString("name");
                            String description = objectjson.optString("description");
                            String time_new = objectjson.optString("time_new");
                            String time_pre = objectjson.optString("time_pre");
                            String image = objectjson.optString("image");
                            LayoutInflater inflater = LayoutInflater.from(context);
                            LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.services_layout, null, false);
                            TextView txtService = (TextView) layout.findViewById(R.id.txtService);
                            TextView txtTime = (TextView) layout.findViewById(R.id.txtTime);
                            txtService.setText(name);
                            txtTime.setText(time_new + " MINS");
                            ly_services.addView(layout);
                        }
                    }
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

    private void UpdateCalendary(final String id_calendary, final String status, final String id_user) {
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "UpdateCalendary")
                .addBodyParameter("id_calendary", id_calendary)
                .addBodyParameter("status", status)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Data", response);
                if (status.contains("1")) {
                    Helper.WriteLog(context, "Especialista actualizó  la solicitud a estatus: " + status);
                    Helper.ShowAlert(context, "¡Gracias por usar nuestro servicio!", "Haz actualizado la solicitud", 0);
                    Helper.SendNotification(id_user, "Solicitud aceptada por ", "El técnico ha recibido tu solicitud y ha sido aceptada", "1");
                }

                ((MainActivityPlo) context).LeerOrdenes();


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("UpdateCalendary error", error.toString());
            }
        });
    }
}
