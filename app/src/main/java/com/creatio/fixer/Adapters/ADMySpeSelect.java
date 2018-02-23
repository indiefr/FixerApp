package com.creatio.fixer.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.creatio.fixer.Objects.OMySpecialist;
import com.creatio.fixer.R;

import java.util.ArrayList;

/**
 * Created by Layge on 03/07/2017.
 */


public class ADMySpeSelect extends BaseAdapter {
    Context context;
    ArrayList<OMySpecialist> list;

    public ADMySpeSelect(Context context, ArrayList<OMySpecialist> list) {
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
        final View itemView = inflater.inflate(R.layout.list_myspe, parent, false);
        TextView txtPass,txtEmail,txtName,txtOrders;
        txtName = itemView.findViewById(R.id.txtName);
        txtPass = itemView.findViewById(R.id.txtPass);
        txtEmail = itemView.findViewById(R.id.txtEmail);
        txtOrders = itemView.findViewById(R.id.txtOrders);

        txtName.setText(list.get(position).getName() + " " + list.get(position).getLast_name());
        txtOrders.setVisibility(View.GONE);
        txtEmail.setText(list.get(position).getEmail());
        txtPass.setVisibility(View.GONE);

        return itemView;
    }
}
