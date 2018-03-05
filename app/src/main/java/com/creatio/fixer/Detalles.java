package com.creatio.fixer;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.creatio.fixer.Adapters.ADDetalles;
import com.creatio.fixer.Objects.OServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class Detalles extends Fragment {
    public ListView list_detalle;
    public TextView txtTitle;
    public TextView txtDesc;
    private CircleImageView img;
    ProgressDialog dialog;
    String title_service;
    String desc_service;
    String image;
    String id_service;
    private EditText editText;
    ADDetalles adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_detalles, container, false);
        title_service = getArguments().getString("title_service");
        desc_service = getArguments().getString("desc_service");
        desc_service = getArguments().getString("desc_service");
        id_service = getArguments().getString("id_service");
        image = getArguments().getString("image");
        //Elementos

        LayoutInflater myinflater = getActivity().getLayoutInflater();
        ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.header_detalles, list_detalle, false);
        Button btnCerrar = (Button) myHeader.findViewById(R.id.btnCerrar);
        Button btnReparar = (Button) myHeader.findViewById(R.id.btnReparar);
        list_detalle = (ListView) v.findViewById(R.id.list_detalle);
        txtTitle = (TextView) myHeader.findViewById(R.id.txtTitle);
        txtDesc = (TextView) myHeader.findViewById(R.id.txtDesc);
        editText = (EditText) myHeader.findViewById(R.id.editText);
        img = myHeader.findViewById(R.id.profile_image);
        //--------
        Glide.with(getActivity())
                .load(image)
                .error(R.drawable.tuberia_dummy)
                .into(img);

        //Actions
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).Cerrar();
            }
        });

        btnReparar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Reparar();

            }
        });

        list_detalle.addHeaderView(myHeader, null, false);
        txtTitle.setText(title_service);
        txtDesc.setText(desc_service);
        v.setFocusableInTouchMode(true);
        v.requestFocus();

        v.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        ((MainActivity) getActivity()).Cerrar();
                        return true;
                    }
                }
                return false;
            }
        });
        return v;
    }

    @Override
    public void onStart() {
        dialog = ProgressDialog.show(getActivity(), null, "Cargando detalles del servicio");
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                ListarServicios(id_service);
            }
        }, 1000);
        super.onStart();
    }

    public void Reparar() {
//        Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
//                R.anim.bottom_up);
//
//        hiddenPanel.startAnimation(bottomUp);
//        hiddenPanel.setVisibility(View.VISIBLE);
//        TextView btnAlgoMas = (TextView) hiddenPanel.findViewById(R.id.btnAlgoMas);
//        btnAlgoMas.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Animation bottomUp = AnimationUtils.loadAnimation(getActivity(),
//                        R.anim.bottom_down);
//
//                hiddenPanel.startAnimation(bottomUp);
//                hiddenPanel.setVisibility(View.INVISIBLE);
//            }
//        });
//        Button btnReparalo = (Button) hiddenPanel.findViewById(R.id.btnReparalo);
//        btnReparalo.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((MainActivity) getActivity()).AbrirOrden();
//            }
//        });
        Helper.OpenAlertBottom(getActivity(), "1", "Añadiste reparaciones a tu cuenta", null);
    }

    public void ListarServicios(String id_service) {
        final ArrayList<OServices> services = new ArrayList<>();
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "ChildrenServices")
                .addBodyParameter("id_service", id_service)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_service = object.optString("id_service");
                        String name = object.optString("name");
                        String image = object.optString("image");
                        String description = object.optString("description");
                        String time_new = object.optString("time_new");
                        String time_pre = object.optString("time_pre");
                        services.add(new OServices(id_service, image, name, description, time_pre, time_new, "0", ""));
                    }
                    dialog.dismiss();
                    adapter = new ADDetalles(getActivity(), services, services, Detalles.this);
                    list_detalle.setAdapter(adapter);

                    list_detalle.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        }
                    });
                } catch (JSONException e) {
                    Log.e("Login error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });


    }
}
