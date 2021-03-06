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


public class ADMySpe extends BaseAdapter {
    Context context;
    ArrayList<OMySpecialist> list;

    public ADMySpe(Context context, ArrayList<OMySpecialist> list) {
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
        TextView txtPass, txtEmail, txtName;
        txtName = itemView.findViewById(R.id.txtName);
        txtPass = itemView.findViewById(R.id.txtPass);
        txtEmail = itemView.findViewById(R.id.txtEmail);

        txtName.setText(list.get(position).getName() + " " + list.get(position).getLast_name());
        txtEmail.setText("Correo: " + list.get(position).getEmail());
        txtPass.setText("Pass: " + list.get(position).getPassword());

        return itemView;
    }
}
