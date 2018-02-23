package com.creatio.fixer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creatio.fixer.Adapters.ADMySpe;
import com.creatio.fixer.Objects.OMySpecialist;
import com.creatio.fixer.Objects.OSpecialist;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MySpecialists extends AppCompatActivity {
    private ListView lvSpe;
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_specialists);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        lvSpe = findViewById(R.id.lvSpe);
        GetMyEspe();
    }
    public void GetMyEspe(){
        final ArrayList<OMySpecialist> listspe = new ArrayList<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MySpecialists.this);
        listspe.clear();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/LoadMySpecialists")
                .addBodyParameter("id_user", pref.getString("id_user", "0"))
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("response ", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_specialist = object.optString("id_specialist");
                        String name = object.optString("name");
                        String last_name = object.optString("last_name");
                        String desc = object.optString("description");
                        String age = object.optString("age");
                        String email = object.optString("email");
                        String password = object.optString("password");

                        listspe.add(new OMySpecialist(id_specialist, name, last_name, desc,age,email,password));
                    }
                    ADMySpe adapter =  new ADMySpe(MySpecialists.this,listspe);
                    lvSpe.setAdapter(adapter);
                } catch (JSONException e) {
                    Log.e("error ", e.toString());
                }

            }

            @Override
            public void onError(ANError anError) {
                Log.e("Responce array error", anError.toString());
            }
        });

    }
}
