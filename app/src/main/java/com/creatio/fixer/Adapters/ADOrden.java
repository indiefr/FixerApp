package com.creatio.fixer.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.Image;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.creatio.fixer.CardForm;
import com.creatio.fixer.Helper;
import com.creatio.fixer.Objects.OServices;
import com.creatio.fixer.R;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADOrden extends BaseAdapter {
    Context context;
    ArrayList<OServices> arrServices;
    String type;

    public ADOrden(Context context, ArrayList<OServices> arrServices, String type) {
        this.context = context;
        this.arrServices = arrServices;
        this.type = type;
    }

    @Override
    public int getCount() {
        return arrServices.size() + 1;
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
        final View itemView = inflater.inflate(R.layout.list_orden, parent, false);
        TextView txtTitle, txtDesc;
        Button btnPrice;
        ImageButton btnDelete;
        ImageView imgConcepto;
        txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
        imgConcepto = (ImageView) itemView.findViewById(R.id.imgConcepto);
        btnDelete = (ImageButton) itemView.findViewById(R.id.deleteItem);

        if (position < arrServices.size()) {

            txtTitle.setText(arrServices.get(position).getTitle());

            double total = 0;
            if (arrServices.get(position).getType().equalsIgnoreCase("0")) {
                //Nuevo
                total = Double.parseDouble(arrServices.get(position).getTime_new()) * 1.59;
                txtDesc.setText(arrServices.get(position).getDesc() + "\nInstalación nueva");
            } else {
                //Reinstalación
                total = Double.parseDouble(arrServices.get(position).getTime_pre()) * 1.59;
                txtDesc.setText(arrServices.get(position).getDesc() + "\nReinstalación");
            }

            btnPrice.setText(Helper.formatDecimal(total));
            btnDelete.setEnabled(true);
        } else {
            btnDelete.setBackgroundResource(R.drawable.ic_place);
            btnDelete.setEnabled(false);
            txtTitle.setText("Tarifa de trayecto");
            imgConcepto.setImageResource(R.drawable.ruta);
            txtDesc.setText("Cargos por trayecto");
            btnPrice.setText("$ 35,00");
        }
        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setMessage("¿Estás seguro de eliminar este servicio?");
                alert.setTitle("Atención");
                alert.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String id_service = arrServices.get(position).getId_service();
                        Log.e("service", id_service);
                        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor edit = pref.edit();
                        final Map<String, ?> allEntries = pref.getAll();
                        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                            if (entry.getKey().contains("id_service")) {
                                if (entry.getKey().contains(id_service)){
                                    edit.remove(entry.getKey());
                                    int items = pref.getInt("badge", 0);
                                    edit.putInt("badge", items - 1);
                                    edit.apply();
                                    int totalbadge = pref.getInt("badge", 0);
                                    arrServices.remove(position);
                                    notifyDataSetChanged();
                                    if (totalbadge == 0){
                                        Toast.makeText(context, "No hay más elementos", Toast.LENGTH_SHORT).show();

                                    }
                                }
                            }
                        }
                    }
                });
                alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                alert.show();
            }
        });
        return itemView;
    }
}
