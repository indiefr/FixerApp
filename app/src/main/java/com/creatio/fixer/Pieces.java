package com.creatio.fixer;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.creatio.fixer.Adapters.ADPieces;
import com.creatio.fixer.Objects.OPieces;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Pieces extends AppCompatActivity {
    ArrayList<OPieces> list = new ArrayList<>();
    private EditText edt_pieces;
    private RecyclerView recyclerView;
    private ADPieces adapter;
    private TextView txtPieces;
    String id_service, id_sale;
    Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pieces);
        extras = getIntent().getExtras();
        id_service = extras.getString("id_service");
        id_sale = extras.getString("id_sale");
        recyclerView = (RecyclerView) findViewById(R.id.list_pieces);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        edt_pieces = (EditText) toolbar.findViewById(R.id.edt_pieces);
        txtPieces = (TextView) findViewById(R.id.txtPieces);
        TextView txtNameService = (TextView) findViewById(R.id.txtNameService);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        recyclerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                return false;
            }
        });

        recyclerView.setLayoutManager(layoutManager);
        adapter = new ADPieces(Pieces.this, list, id_service, id_sale);
        txtNameService.setText("Piezas de " + extras.getString("name_service"));
        recyclerView.setAdapter(adapter);
        edt_pieces.addTextChangedListener(new TextWatcher() {
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
        PiecesOfservice();
        ReadPieces();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void ReadPieces() {
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "ReadPiecesOrder")
                .addBodyParameter("id_order", id_sale)
                .addBodyParameter("id_service", id_service)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_piece = object.optString("id_piece");
                        String name = object.optString("name");
                        String price = object.optString("price");
                        txtPieces.append("\n" + name);

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    public void PiecesOfservice() {
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "Pieces")
                .addBodyParameter("id_service", id_service)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("TAG", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_piece = object.optString("id_piece");
                        String name = object.optString("name");
                        String description = object.optString("description");
                        String price = object.optString("price");
                        String id_store = object.optString("id_store");
                        String status = object.optString("status");
                        String name_store = object.optString("nameStore");
                        String image = object.optString("image");
                        String code = object.optString("code");
                        list.add(new OPieces(id_piece, name, description, id_store, status, price, name_store, image, code));


                    }
                    adapter = new ADPieces(Pieces.this, list, id_service, id_sale);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("piece try error", e.toString());
                }

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });
    }

    public void ChangeText(String name) {
        if (list.contains(name)) {


        }
    }

    public void Cerrar() {

        finish();
        Helper.ShowAlert(Pieces.this, "Buen trabajo", "Se han agregado las peizas correctamente", 0);
    }
}
