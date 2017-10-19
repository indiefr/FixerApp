package com.creatio.fixer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class Register extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String TAG = "Register";
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    EditText edtEmail, edtPass, edtLast, edtName, edtPhone;
    ScrollView layout;
    Button btnLogin,btnYa;
    String refreshedToken = "sin token";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        AndroidNetworking.initialize(getApplicationContext());

        //Cargar elementos
        layout = (ScrollView) findViewById(R.id.layout);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        edtName = (EditText) findViewById(R.id.edtName);
        edtLast = (EditText) findViewById(R.id.edtLast);
        edtPhone = (EditText) findViewById(R.id.edtPhone);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnYa = (Button) findViewById(R.id.btnYa);
        final TransitionDrawable transition = (TransitionDrawable) layout.getBackground();

        //Acciones elementos
        btnYa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Register.this,Login.class);
                startActivity(intent);
                finish();
            }
        });
        //Configuracion FIREBASE

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("981476225204-n1in3rrqrgh83obasa703ph1gqar51lu.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        FirebaseApp.initializeApp(this);
        refreshedToken = FirebaseInstanceId.getInstance().getToken();
        mAuth = FirebaseAuth.getInstance();
        // ...
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = edtEmail.getText().toString();
                String name = edtName.getText().toString();
                String last = edtLast.getText().toString();
                String phone = edtPhone.getText().toString();
                final String pass = edtPass.getText().toString();
                if (!Helper.isValidEmail(email)) {
                    edtEmail.setError("Correo inválido");
                    return;
                }
                if (!Helper.isValidPassword(pass)) {
                    edtPass.setError("Password inválido");
                    return;
                }
                if (!Helper.isValidName(name)) {
                    edtName.setError("Nombre Invalido");
                    return;
                }
                if (!Helper.isValidName(last)) {
                    edtLast.setError("Apellido Invalido");
                    return;
                }
                if (!Helper.isValidPhone(phone)) {
                    edtPhone.setError("Teléfono Invalido");
                    return;
                }


                AndroidNetworking.post("http://api.fixerplomeria.com/v1/Register")
                        .addBodyParameter("email", email)
                        .addBodyParameter("pass", pass)
                        .addBodyParameter("name", name)
                        .addBodyParameter("last", last)
                        .addBodyParameter("phone", phone)
                        .addBodyParameter("type", "user")
                        .addBodyParameter("token", refreshedToken)
                        .setPriority(Priority.MEDIUM)
                        .build().getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {

                        try {
                            if (response.getString(0).equalsIgnoreCase("No user")){
                                Helper.ShowAlert(Register.this,"Atención","El correo ya existe en nuestro servicio, intenta con otro.",0);
                            }
                            for (int i = 0; i < response.length(); i++) {


                                JSONObject object = response.getJSONObject(i);
                                String id_user = object.optString("id_user");
                                String name = object.optString("name");
                                String last_name = object.optString("last_name");
                                String email = object.optString("email");
                                String profile_image = object.optString("profile_image");
                                String status = object.optString("status");
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Register.this);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("id_user", id_user);
                                editor.putString("name", name);
                                editor.putString("last_name", last_name);
                                editor.putString("email", email);
                                editor.putInt("badge", 0);
                                editor.putString("profile_image", profile_image);
                                editor.putString("status", status);
                                editor.putBoolean("login", true);
                                editor.apply();
                                Helper.WriteLog(Register.this, "Usuario ha iniciado sesión .");
                                finish();
                                Intent intent = new Intent(Register.this, ImageProfile.class);
                                startActivity(intent);

                            }
                        } catch (JSONException e) {
                            Log.e("Login error catch", e.toString());
                        }

                    }

                    @Override
                    public void onError(ANError error) {
                        // handle error
                        Log.e("Login error", error.toString());
                    }
                });


            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Register.this, Login.class);
        startActivity(intent);
        finish();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Register.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // ...
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
