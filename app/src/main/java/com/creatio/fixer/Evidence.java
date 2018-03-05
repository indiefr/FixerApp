package com.creatio.fixer;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creatio.fixer.Adapters.ADEvidence;
import com.creatio.fixer.Objects.OEvidence;
import com.mindorks.paracamera.Camera;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;

public class Evidence extends AppCompatActivity {
    Bundle extras;
    String id_sale;
    Camera camera;
    ArrayList<OEvidence> list = new ArrayList<>();
    ArrayList<OEvidence> list2 = new ArrayList<>();
    int TAKE_PHOTO_CODE = 0;
    public static int count = 0;
    private RecyclerView recyclerView;
    private RecyclerView recyclerView2;
    private Button btn, btn2;
    private Uri outputFileUri;
    private File newfile;
    private String type = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evidence);
        extras = getIntent().getExtras();
        id_sale = extras.getString("id_sale");
        setTitle("Orden " + id_sale);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.list_evidence);
        recyclerView2 = (RecyclerView) findViewById(R.id.list_evidence2);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView2.setLayoutManager(layoutManager2);
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);

        final String dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/picFolder/";
        File newdir = new File(dir);
        newdir.mkdirs();
        btn = (Button) findViewById(R.id.btn);
        btn2 = (Button) findViewById(R.id.btn2);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    type = "0";
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    type = "1";
                    camera.takePicture();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        GetEvidence("0");
        GetEvidence("1");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == Camera.REQUEST_TAKE_PHOTO) {
            Bitmap bitmap = camera.getCameraBitmap();

            if (bitmap != null) {
                Uri selectedImage = getImageUri(Evidence.this, bitmap);
                outputFileUri = selectedImage;
                UploadImage(outputFileUri, type);
            } else {
                Toast.makeText(this.getApplicationContext(), "Picture not taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = camera.getCameraBitmapPath();
        return Uri.parse(path);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        camera.deleteImage();
    }

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        cursor.moveToFirst();
        String imagePath = cursor.getString(column_index);

        return cursor.getString(column_index);
    }

    public void UploadImage(Uri uri, final String type) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.upload(url + "UploadEvidence")
                .addMultipartFile("file", new File(camera.getCameraBitmapPath()))
                .addMultipartParameter("id_specialist", id_user)
                .addMultipartParameter("type", type)
                .addMultipartParameter("id_sale", id_sale)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("UPLOADIMAGE", response);
                GetEvidence(type);

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("UPLOADIMAGE error", error.toString());
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public void GetEvidence(final String type) {
        list = new ArrayList<>();
        list2 = new ArrayList<>();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "GetEvidences")
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("id_sale", id_sale)
                .addBodyParameter("type", type)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        if (type.equalsIgnoreCase("0")) {
                            JSONObject obj = response.getJSONObject(i);
                            list.add(new OEvidence(obj.optString("id_evidence"), obj.optString("name"), obj.optString("create_on")));
                        } else {
                            JSONObject obj = response.getJSONObject(i);
                            list2.add(new OEvidence(obj.optString("id_evidence"), obj.optString("name"), obj.optString("create_on")));
                        }

                    }

                    if (type.equalsIgnoreCase("0")) {
                        ADEvidence adapter = new ADEvidence(Evidence.this, list, id_sale);
                        recyclerView.setAdapter(adapter);
                    } else {
                        ADEvidence adapter = new ADEvidence(Evidence.this, list2, id_sale);
                        recyclerView2.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("getevidence error", error.toString());
            }
        });
    }
}
