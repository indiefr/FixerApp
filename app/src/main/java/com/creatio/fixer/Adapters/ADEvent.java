package com.creatio.fixer.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.creatio.fixer.Objects.OCalendar;
import com.creatio.fixer.OrdenTrabajo;
import com.creatio.fixer.R;

import java.util.ArrayList;

/**
 * Created by Layge on 03/07/2017.
 */


public class ADEvent extends BaseAdapter {
    Context context;
    ArrayList<OCalendar> list;

    public ADEvent(Context context, ArrayList<OCalendar> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return 24;
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
        final View itemView = inflater.inflate(R.layout.list_event, parent, false);
        TextView txtReparacion = (TextView) itemView.findViewById(R.id.txtReparacion);
        TextView txtClient = (TextView) itemView.findViewById(R.id.txtClient);
        LinearLayout ly_gral = (LinearLayout) itemView.findViewById(R.id.ly_gral);
        ly_gral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, OrdenTrabajo.class);
                i.putExtra("type", "1");
                i.putExtra("id_sale", list.get(position).getExtra());
                context.startActivity(i);
            }
        });
        String status = list.get(position).getStatus();
        if (status.equalsIgnoreCase("1")) {
            ly_gral.setVisibility(View.VISIBLE);
        } else {
            ly_gral.setVisibility(View.INVISIBLE);
        }
        txtReparacion.setText(list.get(position).getDescription());

        txtClient.setText("Cliente: " + list.get(position).getName());
        TextView txtHora = (TextView) itemView.findViewById(R.id.txtHora);

        if (position < 12) {
            txtHora.setText((position) + " AM");
        } else {
            txtHora.setText((position) + " PM");
        }
        if (list.get(position).getLast_name().equalsIgnoreCase("Fecha de servicio")) {
            ly_gral.setBackgroundResource(R.color.colorPrimary);
            txtReparacion.append(" Reparación");
        } else {
            if (list.get(position).getStatus().equalsIgnoreCase("1")) {
                ly_gral.setBackgroundResource(R.color.blue);
                txtReparacion.append(" Programada");
            } else if (list.get(position).getStatus().equalsIgnoreCase("2")) {
                ly_gral.setBackgroundResource(R.color.red);
                txtReparacion.append(" Finalizada");
            } else {
                ly_gral.setBackgroundResource(R.color.colorAccent);
                txtReparacion.append(" Primera visita");
            }
        }

        return itemView;
    }
}
