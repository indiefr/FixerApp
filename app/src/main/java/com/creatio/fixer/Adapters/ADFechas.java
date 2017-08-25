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


public class ADFechas extends BaseAdapter {
    Context context;
    ArrayList<OCalendar> list;

    public ADFechas(Context context, ArrayList<OCalendar> list) {
        this.context = context;
        this.list = list;
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
        final View itemView = inflater.inflate(R.layout.list_fechas, parent, false);
        TextView txtHora = (TextView) itemView.findViewById(R.id.txtHora);
        txtHora.setText(list.get(position).getName());
        return itemView;
    }
}
