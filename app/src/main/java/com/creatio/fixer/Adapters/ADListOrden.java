package com.creatio.fixer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creatio.fixer.CardForm;
import com.creatio.fixer.Helper;
import com.creatio.fixer.Objects.OOrders;
import com.creatio.fixer.Objects.OServices;
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
        Button btnPrice,btnPagar;
        TextView txtNumber, txtFecha, txtEspecialist, txtStatus;
        LinearLayout lyOptions = (LinearLayout) itemView.findViewById(R.id.ly_options);
        btnPagar = (Button) itemView.findViewById(R.id.btnPagar);
        btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
        txtNumber = (TextView) itemView.findViewById(R.id.txtNumber);
        txtFecha = (TextView) itemView.findViewById(R.id.txtFecha);
        txtEspecialist = (TextView) itemView.findViewById(R.id.txtEspecialist);
        txtStatus = (TextView) itemView.findViewById(R.id.txtStatus);

        String[] date = list.get(position).getInit_date().split(" ");
        txtNumber.setText("Número de orden: " + list.get(position).getId_order());
        txtFecha.setText("Fecha programada: " + date[0] + " a las " + list.get(position).getHour_date() + " hrs.");

        if (list.get(position).getService_date().contains("2017")){
            String[] dateser = list.get(position).getService_date().split(" ");
            txtFecha.append("\n" + "Con fecha de reparación: " + dateser[0] + " a las " + list.get(position).getHour_date_service());
        }
        txtEspecialist.setText("Especialista: " + list.get(position).getName());
        String total = list.get(position).getTotal();
        btnPrice.setText(Helper.formatDecimal(Double.parseDouble(total)));

        switch (list.get(position).getStatus_so()){
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
                Helper.InitOrder(list.get(position).getId_order(),"0");
                Helper.SendNotification(list.get(position).getId_specialist(),"Orden confirmada","Solicitud de servicio confirmada","0");
            }
        });
        return itemView;
    }
}
