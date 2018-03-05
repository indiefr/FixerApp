package com.creatio.fixer.Adapters;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creatio.fixer.CardForm;
import com.creatio.fixer.Helper;
import com.creatio.fixer.Objects.OOrders;
import com.creatio.fixer.Ordenes;
import com.creatio.fixer.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADListOrden extends BaseAdapter {
    Context context;
    ArrayList<OOrders> list;
    String type, iscancel;

    public ADListOrden(Context context, ArrayList<OOrders> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_order_list, parent, false);
        Button btnPrice, btnPagar, btnCancelar;
        ImageView imgType = (ImageView) itemView.findViewById(R.id.imgType);
        TextView txtNumber, txtFecha, txtEspecialist, txtStatus, txtReference, txtRate;
        final RatingBar rtBar;
        LinearLayout lyOptions = (LinearLayout) itemView.findViewById(R.id.ly_options);
        Button btnCupon = (Button) itemView.findViewById(R.id.btnCupon);
        LinearLayout lyRate = (LinearLayout) itemView.findViewById(R.id.lyRate);
        btnPagar = (Button) itemView.findViewById(R.id.btnPagar);
        btnCancelar = (Button) itemView.findViewById(R.id.btnCancelar);
        btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
        txtNumber = (TextView) itemView.findViewById(R.id.txtNumber);
        txtReference = (TextView) itemView.findViewById(R.id.txtReference);
        txtRate = (TextView) itemView.findViewById(R.id.txtRate);
        txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
        txtEspecialist = (TextView) itemView.findViewById(R.id.txtEspecialist);
        txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
        rtBar = (RatingBar) itemView.findViewById(R.id.rtBar);
        rtBar.setEnabled(true);
        lyOptions.setVisibility(View.GONE);
        lyRate.setVisibility(View.GONE);
        btnCupon.setVisibility(View.GONE);
        Log.e("Rate", list.get(position).getRate());
        if (!list.get(position).getRate().equalsIgnoreCase("") && !list.get(position).getRate().equalsIgnoreCase("0")) {
            rtBar.setRating(Float.parseFloat(list.get(position).getRate()));
            txtRate.setText("" + roundTwoDecimals(Double.parseDouble(list.get(position).getRate()) * 2));
            rtBar.setEnabled(false);
        }
        if (!list.get(position).getReference().equalsIgnoreCase("0")) {
            imgType.setImageResource(R.drawable.ic_oxxo_pay_grande);
            imgType.setVisibility(View.VISIBLE);
            txtReference.setVisibility(View.VISIBLE);
            StringBuilder s;
            //txtReference.setText(list.get(position).getReference());
            s = new StringBuilder(list.get(position).getReference());

            for (int i = 4; i < s.length(); i += 5) {
                s.insert(i, " ");
            }
            txtReference.setText("Referencia oxxo: " + s.toString());

        } else {
//            imgType.setVisibility(View.GONE);
            imgType.setImageResource(R.drawable.ic_1600);
            txtReference.setVisibility(View.GONE);
        }

        String[] date = list.get(position).getInit_date().split(" ");
        txtNumber.setText("Número de orden: " + list.get(position).getId_order());
        txtFecha.setText("Fecha programada: " + date[0] + " a las " + list.get(position).getHour_date() + " hrs.");

        if (list.get(position).getService_date().contains("2017")) {
            String[] dateser = list.get(position).getService_date().split(" ");
            txtFecha.append("\n" + "Con fecha de reparación: " + dateser[0] + " a las " + list.get(position).getHour_date_service());
        }
        txtEspecialist.setText("Especialista: " + list.get(position).getName());
        String total = list.get(position).getTotal();
        btnPrice.setText(Helper.formatDecimal(Double.parseDouble(total)));

        if (list.get(position).getName().equalsIgnoreCase(null)) {
            txtEspecialist.setText("Especialista: En espera");
        }
        btnCupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder alerta = new AlertDialog.Builder(context);
                alerta.setTitle("Nombre de cupón");
                LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                View dialogViewc = li.inflate(R.layout.dialog_email, null);
                final EditText edtNamec = (EditText) dialogViewc.findViewById(R.id.edtNombre);
                edtNamec.setVisibility(View.GONE);
                final EditText edtTelefonoc = (EditText) dialogViewc.findViewById(R.id.edtTelefono);
                edtTelefonoc.setVisibility(View.GONE);
                final EditText edtMsjc = (EditText) dialogViewc.findViewById(R.id.edtMsj);

                edtMsjc.setHint("Cupón");
                edtMsjc.setInputType(InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                alerta.setView(dialogViewc);
                alerta.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alerta.setPositiveButton("Aplicar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);

                        String url = "http://api.fixerplomeria.com/v1/";
                        if (Helper.debug) {
                            url = "http://apitest.fixerplomeria.com/v1/";
                        }
                        AndroidNetworking.post(url + "ApplyCupon")
                                .addBodyParameter("id_user", pref.getString("id_user", "0"))
                                .addBodyParameter("id_order", list.get(position).getId_order())
                                .addBodyParameter("name", edtMsjc.getText().toString())

                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                if (response.contains("Aplicado")) {
                                    Helper.ShowAlert(context, "Felicidades", "Se ha aplicado el cupón: " + edtMsjc.getText().toString(), 0);
                                    ((Ordenes) context).ReadOrders();
                                } else {
                                    Helper.ShowAlert(context, "Oh Oh", "Cupón no válido. Ya ingresaste el cupón anteriormente o no éxiste.", 0);
                                }

                            }

                            @Override
                            public void onError(ANError anError) {

                            }
                        });
                    }
                });
                alerta.show();
            }
        });
        if (!list.get(position).getHascupon().equalsIgnoreCase("0")) {
            txtEspecialist.setText("Especialista: " + list.get(position).getName() + "\n" + "Se aplicó cupón");
        }
        switch (list.get(position).getStatus_so()) {
            case "3":
                //Solicitar autorizacion
                lyOptions.setVisibility(View.VISIBLE);
                if (!list.get(position).getHascupon().equalsIgnoreCase("0")) {
                    btnCupon.setVisibility(View.GONE);
                } else {
                    btnCupon.setVisibility(View.VISIBLE);
                }
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case "1":
                //Iniciada
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.blue));
                break;
            case "6":
                //Orden canclada
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.black));
                txtFecha.append("\n" + "Cancelada por el usuario");
                break;
            case "5":
                //Orden creada
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.fondo_card));
                break;
            case "2":
                //Finalizada
                lyRate.setVisibility(View.VISIBLE);
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.black));
                break;
            case "4":
                //Autorizada
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "0":
                //Agendada
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            default:
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
        }
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iscancel = "no";
                //PAGAR
                if (list.get(position).getReference().equalsIgnoreCase("0")) {
                    //tarjeta

                    ConektaOrder(list.get(position).getId_order(), list.get(position).getId_user(), position, "4");
                } else {
                    //oxxo
                    // ConektaOrderOxxo(list.get(position).getId_order(), list.get(position).getId_user());
                }


            }
        });
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iscancel = "si";
                //PAGAR
                if (list.get(position).getReference().equalsIgnoreCase("0")) {
                    //tarjeta
                    ConektaOrder(list.get(position).getId_order(), list.get(position).getId_user(), position, "6");
                } else {
                    //oxxo
                    //ConektaOrderOxxo(list.get(position).getId_order(), list.get(position).getId_user());
                }

            }
        });
        rtBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                //Action to rate...
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Comentario del servicio");

                final EditText input = new EditText(context);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String m_Text = input.getText().toString();
                        String url = "http://api.fixerplomeria.com/v1/";
                        if (Helper.debug) {
                            url = "http://apitest.fixerplomeria.com/v1/";
                        }
                        AndroidNetworking.post(url + "RateSpecialist")
                                .addBodyParameter("id_specialist", list.get(position).getId_specialist())
                                .addBodyParameter("id_order", list.get(position).getId_order())
                                .addBodyParameter("rate", String.valueOf(rating))
                                .addBodyParameter("commits", m_Text)
                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("TAG RATE", response);
                                list.get(position).setRate(String.valueOf(rating));
                                notifyDataSetChanged();
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
        return itemView;
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public void ConektaOrder(final String id_sale, String id_user, final int position, final String status) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("Generando cargo");
        dialog.setCancelable(false);
        dialog.show();
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "ConektaOrder")
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("id_sale", id_sale)
                .addBodyParameter("cancel", iscancel)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Order desc", response);
                dialog.dismiss();
                GetEstusPago(id_sale, position, status);
            }

            @Override
            public void onError(ANError anError) {
                Log.e("Order desc", anError.toString());
                dialog.dismiss();
            }
        });
    }

    public void ConektaOrderOxxo(String id_sale, String id_user) {

        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "ConektaOrderOxxo")
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("id_sale", id_sale)
                .setPriority(Priority.IMMEDIATE)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("Order reference", response);


            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    public void GetEstusPago(final String id_sale, final int position, final String status) {
        final ProgressDialog dialogs = new ProgressDialog(context);
        dialogs.setMessage("Cargando información");
        dialogs.setCancelable(false);
        dialogs.show();
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "GetEstatusPago")
                .addBodyParameter("id_sale", id_sale)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("e pago", response);
                dialogs.dismiss();
                if (response.contains("paid")) {
                    Helper.WriteLog(context, "Pago de la orden: " + list.get(position).getId_order());
                    Helper.InitOrder(list.get(position).getId_order(), status);
                    if (status.equalsIgnoreCase("6")) {
                        Helper.SendNotification(list.get(position).getId_specialist(), "Orden: " + id_sale + " cancelada", "Solicitud de servicio cancelada por el usuario", "0");
                    } else {
                        Helper.SendNotification(list.get(position).getId_specialist(), "Orden: " + id_sale + " confirmada", "Solicitud de servicio confirmada", "0");
                    }
                    list.get(position).setStatus_so(status);
                    notifyDataSetChanged();
                } else {
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.alert_fixer);
                    // set the custom dialog components - text, image and button
                    TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                    TextView txtMsj = (TextView) dialog.findViewById(R.id.txtMsj);
                    txtTitle.setText("Error");
                    txtMsj.setText("Lamentamos esto, tu tarjeta actual no tiene fondos suficientes. Intenta agregando una nueva tarjeta o bien intenta de nuevo.");

                    Button btnAceptar = (Button) dialog.findViewById(R.id.btnAceptar);
                    btnAceptar.setText("Agregar nueva tarjeta");
                    Button btnCancelar = (Button) dialog.findViewById(R.id.btnCancelar);
                    btnCancelar.setText("Intentar de nuevo");

                    // if button is clicked, close the custom dialog
                    btnAceptar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Nueva tarjeta");
                            alert.setMessage("¿Realmente quieres agregar un nuevo metodo de pago?");
                            alert.setPositiveButton("Si, agregar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent intent = new Intent(context, CardForm.class);
                                    intent.putExtra("oxxo", false);
                                    intent.putExtra("updateCard", true);
                                    context.startActivity(intent);
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
                    btnCancelar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });
                    dialog.show();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

}
