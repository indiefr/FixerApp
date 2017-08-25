package com.creatio.fixer.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.creatio.fixer.Helper;
import com.creatio.fixer.MainActivity;
import com.creatio.fixer.Objects.OServices;
import com.creatio.fixer.R;

import java.util.ArrayList;

/**
 * Created by Layge on 03/07/2017.
 */


public class ADServices extends BaseAdapter {
    Context context;
    ArrayList<OServices> arrServices;

    public ADServices(Context context, ArrayList<OServices> arrServices) {
        this.context = context;
        this.arrServices = arrServices;
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
        final View itemView = inflater.inflate(R.layout.list_services, parent, false);
        TextView txtTitle = (TextView)itemView.findViewById(R.id.txtTitle);
        TextView txtDesc = (TextView)itemView.findViewById(R.id.txtDescription);
        txtDesc.setText(arrServices.get(position).getDesc());
        txtTitle.setText(arrServices.get(position).getTitle());
        Button btnReparar = (Button)itemView.findViewById(R.id.btnReparar);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String service = pref.getString("id_service"+arrServices.get(position).getId_service(), "0");
        btnReparar.setVisibility(View.VISIBLE);
        if (!service.equalsIgnoreCase("0")){
            btnReparar.setVisibility(View.INVISIBLE);
        }

        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)context).Detalles(arrServices.get(position).getId_service(),arrServices.get(position).getTitle());
            }
        });
        return itemView;
    }
}
