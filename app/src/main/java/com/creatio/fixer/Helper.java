package com.creatio.fixer;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Layge on 13/07/2017.
 */

public class Helper {
    public static void ShowAlert(Context context, String title, String msj, int type) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_fixer);
        // set the custom dialog components - text, image and button
        TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
        txtTitle.setText(title);
        txtMsj.setText(msj);


        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
        if (type == 0) {
            btnCancelar.setVisibility(View.INVISIBLE);
        }
        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

    public static void ShowAlertServices(Context context) {
        // custom dialog
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.alert_edit);

        final TextView txtServicio = (TextView) dialog.findViewById(R.id.txtServicio);
        Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
        Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
        AutoCompleteTextView actv;
        final AutoCompleteTextView actv2;
        String[] services = {"Baño ", "Cocina", "Patio", "Medidor", "Tuberias 1", "Tuberias 2"};
        String[] services2 = {"Tuberias 1", "Tuberias 2", "Tuberias 3", "Tuberias 4", "Tuberias 5", "Tuberias 6"};
        actv = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView);
        actv2 = (AutoCompleteTextView) dialog.findViewById(R.id.autoCompleteTextView2);


        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (context, android.R.layout.simple_list_item_1, services);
        actv.setAdapter(adapter);
        actv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actv2.setVisibility(View.VISIBLE);
                txtServicio.setVisibility(View.VISIBLE);
                actv2.requestFocus();
            }
        });
        actv.setThreshold(1);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>
                (context, android.R.layout.simple_list_item_1, services2);
        actv2.setAdapter(adapter2);
        actv2.setThreshold(1);
        // if button is clicked, close the custom dialog
        btnAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
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

    public static void WriteLog(final Context context, String msj) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String id_user = pref.getString("id_user", "0");
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/WriteLog")
                .addBodyParameter("msj", msj)
                .addBodyParameter("id_user", id_user)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("respuesta WriteLog", response);

            }

            @Override
            public void onError(ANError error) {
                Log.e("Error WriteLog", error.toString());
            }
        });
    }

    public static String formatDecimal(double number) {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('\'');
        symbols.setDecimalSeparator(',');

        DecimalFormat decimalFormat = new DecimalFormat("$ #,###.00", symbols);
        String prezzo = decimalFormat.format(number);
        return prezzo;
    }

    public static void SendNotification(String id_user, String title, String msj, String type) {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/SendAndroid")
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("msj", msj)
                .addBodyParameter("title", title)
                .addBodyParameter("type", type)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {


                Log.e("SendNotification succes", response);


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("SendNotification error", error.toString());
            }
        });
    }

    public void Network() {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/Services")
                .addBodyParameter("email", "")
                .addBodyParameter("pass", "")
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_user = object.optString("id_user");


                    }
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

    // validating email id
    public static boolean isValidEmail(String email) {
        String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

        Pattern pattern = Pattern.compile(EMAIL_PATTERN);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static boolean isValidName(String name) {

        return true;
    }

    public static boolean isValidPhone(String phone) {
        if (phone != null && phone.length() >= 10) {
            return true;
        }
        return false;
    }

    // validating password with retype password
    public static boolean isValidPassword(String pass) {
        if (pass != null && pass.length() >= 6) {
            return true;
        }
        return false;
    }

    public static void InitOrder(String id_sale, String status) {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/InitOrder")
                .addBodyParameter("id_sale", id_sale)
                .addBodyParameter("status", status)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });
    }

    public static void UpdateDateService(String id_sale,String hour_date_service, String service_date) {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdateDateService")
                .addBodyParameter("id_sale", id_sale)
                .addBodyParameter("service_date", service_date)
                .addBodyParameter("hour_date_service", hour_date_service)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });
    }

    public static void OpenAlertBottom(final Context context, final String type, final String msj, final ArrayList<String> list) {
        final View contentView = View.inflate(context, R.layout.snack_repair, null);
        final BottomSheetDialog dialog = new BottomSheetDialog(context);
        dialog.setContentView(contentView);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) contentView.getParent())
                .getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        ((View) contentView.getParent()).setBackgroundColor(context.getResources().getColor(android.R.color.transparent));
        Button btnReparalo = (Button) contentView.findViewById(R.id.btnReparalo);
        TextView txtMsj = (TextView) contentView.findViewById(R.id.txtMsj);
        txtMsj.setText(msj);
        if (type.equalsIgnoreCase("0")) {
            btnReparalo.setText("Seleccionar ubicación");
        }

        btnReparalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (type.equalsIgnoreCase("1")) {
                    ((MainActivity) context).AbrirOrden();
                } else {
                    ((MainActivity) context).OpenPlace(list.get(0), list.get(1), Integer.parseInt(list.get(2)));
                }
            }
        });
        dialog.show();
        new CountDownTimer(2000, 1000) {

            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                if (type.equalsIgnoreCase("1")) {
                    dialog.dismiss();
                }

            }
        }.start();
    }

    public static void UpdateOrder(String id_sale, String id_services) {
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdateOrder")
                .addBodyParameter("id_sale", id_sale)
                .addBodyParameter("id_services", id_services)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });
    }
}
