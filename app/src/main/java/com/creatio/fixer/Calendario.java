package com.creatio.fixer;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.GridView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.fixer.Adapters.ADCalendar;
import com.creatio.fixer.Objects.OCalendarAll;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Calendario extends Fragment {
    public GridView grid_calendar;
    public Button btnCalendar, btnL, btnR;
    public TextView txtAbrirOrden;
    java.util.Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    View v;
    ArrayList<OCalendarAll> list = new ArrayList<>();
    String todaySend, today;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_calendar, null);
        grid_calendar = (GridView) v.findViewById(R.id.grid_calendar);

        btnCalendar = (Button) v.findViewById(R.id.btnCalendar);
        btnL = (Button) v.findViewById(R.id.btnL);
        btnR = (Button) v.findViewById(R.id.btnR);
        txtAbrirOrden = (TextView) v.findViewById(R.id.txtAbrirOrden);


        //Actions
        SimpleDateFormat sdf = new SimpleDateFormat("EEE dd MMM", Locale.ROOT);
        SimpleDateFormat sdfSend = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
        today = sdf.format(new Date());
        todaySend = sdfSend.format(new Date());
        btnCalendar.setText(today);
        txtAbrirOrden.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).AbrirOrden();
            }
        });
        myCalendar = java.util.Calendar.getInstance(Locale.US);
        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(java.util.Calendar.YEAR, year);
                myCalendar.set(java.util.Calendar.MONTH, month);
                myCalendar.set(java.util.Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "EEE dd MMM"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ROOT);
                btnCalendar.setText(sdf.format(myCalendar.getTime()));
                SimpleDateFormat sdfSend = new SimpleDateFormat("yyyy-MM-dd", Locale.ROOT);
                todaySend = sdfSend.format(myCalendar.getTime());
                GetCalendar(todaySend);

            }
        };
        btnCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(getActivity(), date, myCalendar
                        .get(java.util.Calendar.YEAR), myCalendar.get(java.util.Calendar.MONTH),
                        myCalendar.get(java.util.Calendar.DAY_OF_MONTH)).show();


            }
        });
        btnR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdf.parse(todaySend));
                    c.add(Calendar.DATE, 1);  // number of days to add
                    todaySend = sdf.format(c.getTime());
                    String myFormat = "EEE dd MMM"; //In which you need put here
                    SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat, Locale.ROOT);
                    btnCalendar.setText(sdf2.format(c.getTime()));
                    GetCalendar(todaySend);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        btnL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.setTime(sdf.parse(todaySend));
                    c.add(Calendar.DATE, -1);  // number of days to add
                    todaySend = sdf.format(c.getTime());
                    String myFormat = "EEE dd MMM"; //In which you need put here
                    SimpleDateFormat sdf2 = new SimpleDateFormat(myFormat, Locale.ROOT);
                    btnCalendar.setText(sdf2.format(c.getTime()));
                    GetCalendar(todaySend);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        GetCalendar(todaySend);
        return v;
    }

    public void Seleccionar(final String id_specialist, final String init_date, final int hour_date) {


        ArrayList<String> list = new ArrayList<String>();
        list.add(id_specialist);
        list.add(init_date);
        list.add(String.valueOf(hour_date));
        Helper.OpenAlertBottom(getActivity(), "0", "Has seleccionado correctamente el horario.", list);

    }

    public void GetCalendar(final String todaySend) {
        list = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/GetCalendar")
                .addBodyParameter("date", todaySend)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {

                        JSONObject object = response.getJSONObject(i);

                        String id_specialist = object.optString("id_specialist");
                        String init_date = object.optString("init_date");
                        String finish_date = object.optString("finish_date");
                        String commits = object.optString("commits");
                        String status = object.optString("status");


                        list.add(new OCalendarAll(id_specialist, init_date, finish_date, commits, status));

                    }
                    ADCalendar adapter = new ADCalendar(getActivity(), list, Calendario.this, todaySend);
                    grid_calendar.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("GetCalendar error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("GetCalendar error", error.toString());
            }
        });

    }
}
