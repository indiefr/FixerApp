package com.creatio.fixer;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;

import net.gotev.uploadservice.MultipartUploadRequest;
import net.gotev.uploadservice.UploadNotificationConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.Date;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class ImageProfile extends AppCompatActivity {
    private Button btnEmpezar, btnEdit;
    private CircleImageView img;
    String id_user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_profile);
        //-----------------[ELEMENTS]------------------
        btnEmpezar = (Button) findViewById(R.id.btnEmpezar);
        btnEdit = (Button) findViewById(R.id.btnEdit);
        img = (CircleImageView) findViewById(R.id.img);
        final TypeWriteer tx = (TypeWriteer) findViewById(R.id.tx);
        //-------------------------------------------------

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ImageProfile.this);
        tx.setCharacterDelay(70);
        tx.animateText("Bienvenido " + sharedPref.getString("name","Sin nombre") +" "+ sharedPref.getString("last_name","Sin nombre"));
        new CountDownTimer(5000, 1000) {

            public void onTick(long millisUntilFinished) {
            }

            public void onFinish() {
                tx.animateText("Es hora de utilizar la app");
            }
        }.start();
        MediaPlayer mp = MediaPlayer.create(getApplicationContext(), R.raw.bouble);
        mp.start();

        btnEmpezar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                Intent intent = new Intent(ImageProfile.this, MainActivity.class);
                startActivity(intent);
            }
        });
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 1);
            }
        });

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        id_user = pref.getString("id_user", "0");

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                img.setImageURI(selectedImage);
                String filePath = getPath(selectedImage);
                String file_extn = filePath.substring(filePath.lastIndexOf(".") + 1);


                if (file_extn.equals("img") || file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("gif") || file_extn.equals("png")) {
                    //FINE
                    UploadImage(selectedImage);
                } else {
                    //NOT IN REQUIRED FORMAT
                    Toast.makeText(ImageProfile.this, "Imagen no soprotada por la aplicación, intenta con otra", Toast.LENGTH_SHORT).show();
                }

            }
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

    public void UploadImage(Uri uri) {
        AndroidNetworking.upload("http://api.fixerplomeria.com/v1/UploadImage")
                .addMultipartFile("file", new File(getPath(uri)))
                .addMultipartParameter("id_user", id_user)
                .setPriority(Priority.MEDIUM)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ImageProfile.this);
                SharedPreferences.Editor editor = sharedPref.edit();

                editor.putString("profile_image", response);

                editor.apply();

            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("UPLOADIMAGE error", error.toString());
            }
        });

    }
}
