package com.creatio.fixer.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.creatio.fixer.Calendario;
import com.creatio.fixer.Objects.OCalendarAll;
import com.creatio.fixer.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Layge on 04/07/2017.
 */

public class ADCalendar extends BaseAdapter {
    Context context;
    ArrayList<OCalendarAll> list;
    LinearLayout linear_calendar;
    CircleImageView image_profile;
    TextView txtNombre;
    Calendario fragment;
    String todaySend;

    public ADCalendar(Context context, ArrayList<OCalendarAll> list, Calendario fragment, String todaySend) {
        this.context = context;
        this.list = list;
        this.fragment = fragment;
        this.todaySend = todaySend;
    }

    @Override
    public int getCount() {
        return 2;
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
        final View itemView = inflater.inflate(R.layout.grid_calendar, parent, false);
        linear_calendar = (LinearLayout) itemView.findViewById(R.id.linear_calendar);
        image_profile = (CircleImageView) itemView.findViewById(R.id.image_profile);
        txtNombre = (TextView) itemView.findViewById(R.id.txtNombre);
        if (position == 0) {
            image_profile.setVisibility(View.INVISIBLE);
            txtNombre.setText("Hora");
            GenerateText(0);
        } else {
            txtNombre.setText("Fecha seleccionada");

            Glide.with(context)
                    .load("https://www.shareicon.net/data/2015/08/29/92889_calendar_2133x2133.png")
                    .into(image_profile);
            GenerateButton(list.get(position - 1).getStatus());
        }

        return itemView;
    }
    int c = 0;
    public void GenerateButton(final String status) {

        for (int i = 0; i < list.size(); i++) {
            c = i;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(5, 5, 5, 5);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
            params.height = height;
            params.gravity = Gravity.CENTER;

            final Button btn = new Button(context);
            String statusc = list.get(c).getStatus();
            btn.setTextColor(Color.WHITE);
            if (statusc.equalsIgnoreCase("0")) {
                btn.setText("Libre");
                btn.setId(i);
                btn.setBackgroundResource(R.drawable.bg_green);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment.Seleccionar(list.get(c).getId_specialis(), todaySend, btn.getId());
                    }
                });
            } else {
                btn.setText("Ocupado");
                btn.setBackgroundResource(R.drawable.bg_red);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context, "El técnico esta ocupado.", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            if (i < 8 || i > 20) {
                btn.setVisibility(View.GONE);
            }
            btn.setTextSize(10);

            linear_calendar.addView(btn, params);
        }

    }

    public void GenerateText(int status) {

        for (int i = 0; i <= 24; i++) {

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            params.setMargins(5, 5, 5, 5);
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 40, context.getResources().getDisplayMetrics());
            params.height = height;
            params.gravity = Gravity.CENTER;

            TextView txt = new TextView(context);
            txt.setGravity(Gravity.CENTER);
            txt.setTextColor(context.getResources().getColor(R.color.dark));
            if (i < 8 || i > 20) {
                txt.setVisibility(View.GONE);
            }
            txt.setText("" + (0 + i) + ":00");

            txt.setTextSize(15);

            linear_calendar.addView(txt, params);

        }
    }
}
