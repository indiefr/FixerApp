package com.creatio.fixer.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
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
    boolean flagAfter = false;

    public ADServices(Context context, ArrayList<OServices> arrServices, boolean flagAfter) {
        this.context = context;
        this.arrServices = arrServices;
        this.flagAfter = flagAfter;
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
        TextView txtTitle = (TextView) itemView.findViewById(R.id.txtTitle);
        TextView txtDesc = (TextView) itemView.findViewById(R.id.txtDescription);
        final ImageView image_profile = (ImageView) itemView.findViewById(R.id.image_profile);
        txtDesc.setText(arrServices.get(position).getDesc());
        txtTitle.setText(arrServices.get(position).getTitle());
        Button btnReparar = (Button) itemView.findViewById(R.id.btnReparar);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        String service = pref.getString("id_service" + arrServices.get(position).getId_service(), "0");
        btnReparar.setVisibility(View.VISIBLE);
        if (!service.equalsIgnoreCase("0")) {
            btnReparar.setVisibility(View.INVISIBLE);
        }

        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) context).Detalles(arrServices.get(position).getId_service(), arrServices.get(position).getTitle(), arrServices.get(position).getDesc(), arrServices.get(position).getImage());
            }
        });
        Glide.with(context)
                .load(arrServices.get(position).getImage())
                .error(R.drawable.tuberia_dummy)
                .into(image_profile);
        if (flagAfter) {
            Animation animation = AnimationUtils
                    .loadAnimation(context, R.anim.right_left);
            itemView.startAnimation(animation);
        } else {
            Animation animation = AnimationUtils
                    .loadAnimation(context, R.anim.left_right);
            itemView.startAnimation(animation);
        }
        return itemView;
    }
}
