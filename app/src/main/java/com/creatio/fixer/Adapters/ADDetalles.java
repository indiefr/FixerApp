package com.creatio.fixer.Adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.creatio.fixer.Detalles;
import com.creatio.fixer.MainActivity;
import com.creatio.fixer.Objects.OServices;
import com.creatio.fixer.R;

import java.util.ArrayList;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADDetalles extends BaseAdapter {
    Context context;
    ArrayList<OServices> arrServices;
    Detalles fragment;
    String type = "0";

    public ADDetalles(Context context, ArrayList<OServices> arrServices, Detalles fragment) {
        this.context = context;
        this.arrServices = arrServices;
        this.fragment = fragment;
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
        final View itemView = inflater.inflate(R.layout.list_detalles, parent, false);
        final Button btnReparar = (Button) itemView.findViewById(R.id.btnReparar);
        final TextView txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        final TextView txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String service = pref.getString("id_service" + arrServices.get(position).getId_service(), "0");
        btnReparar.setVisibility(View.VISIBLE);
        if (!service.equalsIgnoreCase("0")) {
            btnReparar.setVisibility(View.INVISIBLE);
        }

        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        type = "0";
                        fragment.Reparar();
                        ((MainActivity) context).ChangeBadge(arrServices.get(position).getId_service(), type);
                    }
                });
                btnCancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        type = "1";
                        fragment.Reparar();
                        ((MainActivity) context).ChangeBadge(arrServices.get(position).getId_service(), type);
                    }
                });
                dialog.show();
                v.setVisibility(View.INVISIBLE);


            }
        });
        txtDesc.setText(arrServices.get(position).getDesc());
        txtTitle.setText(arrServices.get(position).getTitle());
        return itemView;
    }

}
