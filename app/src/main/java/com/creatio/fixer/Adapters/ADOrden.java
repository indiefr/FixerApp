package com.creatio.fixer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.creatio.fixer.Helper;
import com.creatio.fixer.Objects.OServices;
import com.creatio.fixer.R;

import java.util.ArrayList;

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
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.list_orden, parent, false);
        TextView txtTitle, txtDesc;
        Button btnPrice;
        ImageView imgConcepto;
        txtDesc = (TextView) itemView.findViewById(R.id.txtDesc);
        txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        btnPrice = (Button) itemView.findViewById(R.id.btnPrice);
        imgConcepto = (ImageView)itemView.findViewById(R.id.imgConcepto);

        if (position < arrServices.size()) {

            txtTitle.setText(arrServices.get(position).getTitle());

            double total = 0;
            if (arrServices.get(position).getType().equalsIgnoreCase("0")){
                //Nuevo
                total = Double.parseDouble(arrServices.get(position).getTime_new())* 1.59;
                txtDesc.setText(arrServices.get(position).getDesc() + "\nInstalación nueva");
            }else{
                //Reinstalación
                total = Double.parseDouble(arrServices.get(position).getTime_pre())* 1.59;
                txtDesc.setText(arrServices.get(position).getDesc() + "\nReinstalación");
            }

            btnPrice.setText(Helper.formatDecimal(total));

        }else{
            txtTitle.setText("Tarifa de trayecto");
            imgConcepto.setImageResource(R.drawable.ruta);
            txtDesc.setText("Cargos por trayecto");
            btnPrice.setText("$ 35,00");
        }
        return itemView;
    }
}
