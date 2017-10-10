package com.creatio.fixer.Adapters;

import android.content.Context;
import android.content.DialogInterface;
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
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creatio.fixer.Helper;
import com.creatio.fixer.Objects.OOrders;
import com.creatio.fixer.R;

import java.util.ArrayList;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADListOrden extends BaseAdapter {
    Context context;
    ArrayList<OOrders> list;
    String type;

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
        Button btnPrice, btnPagar;
        ImageView imgType = (ImageView) itemView.findViewById(R.id.imgType);
        TextView txtNumber, txtFecha, txtEspecialist, txtStatus, txtReference;
        final RatingBar rtBar;
        LinearLayout lyOptions = (LinearLayout) itemView.findViewById(R.id.ly_options);
        LinearLayout lyRate = (LinearLayout) itemView.findViewById(R.id.lyRate);
        btnPagar = (Button) itemView.findViewById(R.id.btnPagar);
        btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
        txtNumber = (TextView) itemView.findViewById(R.id.txtNumber);
        txtReference = (TextView) itemView.findViewById(R.id.txtReference);
        txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
        txtEspecialist = (TextView) itemView.findViewById(R.id.txtEspecialist);
        txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);
        rtBar = (RatingBar) itemView.findViewById(R.id.rtBar);
        rtBar.setEnabled(true);
        if (!list.get(position).getRate().equalsIgnoreCase("")) {
            rtBar.setRating(Float.parseFloat(list.get(position).getRate()));
            rtBar.setEnabled(false);
        }
        if (!list.get(position).getReference().equalsIgnoreCase("0")) {
            imgType.setImageResource(R.drawable.ic_oxxo_pay_grande);
            imgType.setVisibility(View.VISIBLE);
            txtReference.setVisibility(View.VISIBLE);
            StringBuilder s;
            //txtReference.setText(list.get(position).getReference());
            s = new StringBuilder(list.get(position).getReference());

            for(int i = 4; i < s.length(); i += 5){
                s.insert(i, " ");
            }
            txtReference.setText("Referencia oxxo: " + s.toString());

        }else{
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
        switch (list.get(position).getStatus_so()) {
            case "3":
                lyOptions.setVisibility(View.VISIBLE);
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.red));
                break;
            case "1":
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.blue));
                break;
            case "5":
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.fondo_card));
                break;
            case "2":
                lyRate.setVisibility(View.VISIBLE);
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.black));
                break;
            case "4":
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
            case "0":
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
                break;
            default:
                txtStatus.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
                break;
        }
        btnPagar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helper.InitOrder(list.get(position).getId_order(), "0");
                Helper.SendNotification(list.get(position).getId_specialist(), "Orden confirmada", "Solicitud de servicio confirmada", "0");
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
                        AndroidNetworking.post("http://api.fixerplomeria.com/v1/RateSpecialist")
                                .addBodyParameter("id_specialist", list.get(position).getId_specialist())
                                .addBodyParameter("id_order", list.get(position).getId_order())
                                .addBodyParameter("rate", String.valueOf(rating))
                                .addBodyParameter("commits", m_Text)
                                .build().getAsString(new StringRequestListener() {
                            @Override
                            public void onResponse(String response) {
                                Log.e("TAG RATE", response);
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
}
