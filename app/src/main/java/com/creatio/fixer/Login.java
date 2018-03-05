package com.creatio.fixer;

import android.Manifest;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.drawable.TransitionDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
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

import java.util.Arrays;


public class Login extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {
    private FirebaseAuth.AuthStateListener mAuthListener;
    public String TAG = "Login";
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    Button btnGoogle, btnLogin, btnRegister;
    EditText edtEmail, edtPass;
    Switch swSesion;
    ScrollView layout;
    String type;
    String refreshedToken = "sin token";
    Button btnFacebook;
    CallbackManager callbackManager;

    private boolean checkIfAlreadyhavePermission() {
        int result = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestForSpecificPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.INTERNET, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.CAMERA}, 101);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //granted
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Permission request
        int MyVersion = Build.VERSION.SDK_INT;
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            if (!checkIfAlreadyhavePermission()) {
                requestForSpecificPermission();
            }
        }
        //------------------
        setContentView(R.layout.activity_login);
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        Boolean login = pref.getBoolean("login", false);
//        if (login) {
//            finish();
//            Intent intent = new Intent(Login.this, MainActivity.class);
//            startActivity(intent);
//        }
        Boolean login_spe = pref.getBoolean("login_spe", false);
        if (login_spe) {
            finish();
            Intent intent = new Intent(Login.this, MainActivityPlo.class);
            startActivity(intent);
        }
        AndroidNetworking.initialize(getApplicationContext());

        //Cargar elementos

        layout = (ScrollView) findViewById(R.id.layout);
        swSesion = (Switch) findViewById(R.id.swSesion);
        btnGoogle = (Button) findViewById(R.id.btnGoogle);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegister = (Button) findViewById(R.id.btnRegister);
        edtEmail = (EditText) findViewById(R.id.edtEmail);
        edtPass = (EditText) findViewById(R.id.edtPass);
        final TransitionDrawable transition = (TransitionDrawable) layout.getBackground();
        btnFacebook = (Button) findViewById(R.id.btnFacebook);
        callbackManager = CallbackManager.Factory.create();

        //Acciones elementos
        btnFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logInWithReadPermissions(Login.this, Arrays.asList("public_profile", "email", "user_friends"));

            }
        });
        // Callback registration
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("LoginActivity", response.toString());

                                // Application code

                                try {
                                    String email = object.getString("email");
                                    String name = object.getString("name");
                                    String id = object.getString("id");
                                    Login(email, id, name, "fb", id, "");
                                    type = "user";
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender");
                request.setParameters(parameters);
                request.executeAsync();


            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login.this, Register.class);
                startActivity(intent);
                finish();
            }
        });
        swSesion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    transition.startTransition(500);
                    btnGoogle.setVisibility(View.GONE);
                    btnFacebook.setVisibility(View.GONE);
                    btnRegister.setVisibility(View.GONE);

                } else {
                    transition.reverseTransition(500);
                    btnGoogle.setVisibility(View.VISIBLE);
                    btnFacebook.setVisibility(View.VISIBLE);
                    btnRegister.setVisibility(View.VISIBLE);
                }
            }
        });
        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        //Configuracion FIREBASE

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
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
                type = "user";

                if (swSesion.isChecked()) {
                    type = "spec";
                }
                String email = edtEmail.getText().toString();
                final String pass = edtPass.getText().toString();
                if (!Helper.isValidEmail(email)) {
                    edtEmail.setError("Correo inválido");
                    return;
                }
                if (!Helper.isValidPassword(pass)) {
                    edtPass.setError("Password inválido");
                    return;
                }
                if (email.equalsIgnoreCase("user") && pass.equalsIgnoreCase("user")) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Login.this);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putBoolean("login", true);
                    editor.apply();
                    finish();
                    Intent i = new Intent(Login.this, MainActivity.class);
                    startActivity(i);
                }
                if (email.equalsIgnoreCase("admin") && pass.equalsIgnoreCase("admin")) {
                    finish();
                    Intent i = new Intent(Login.this, MainActivityPlo.class);
                    startActivity(i);
                } else {

                    Login(email, pass, "", "normal", "", "");
                }

            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1);
    }

    private void CrearUsuarioEmail(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, "El usuario no existe.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void LoginEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(Login.this, "El usuario no existe",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

    private void firebaseAuthWithGoogle(final GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        String name = acct.getDisplayName();
                        String id = acct.getId();
                        String email = acct.getEmail();
                        String image = acct.getPhotoUrl().toString();


                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            type = "user";
                            Login(email, id, name, "gg", id, image);
                        }
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
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

    public void Login(final String email, final String pass, String name, final String media, final String id, final String image) {
        final ProgressDialog dialog = ProgressDialog.show(Login.this, null, "Iniciando sesión");
        // --- [Header elements] ---
        if ((name.length() < 9 || name.matches("\\d+(?:\\.\\d+)?")) && type == "user") {
            //Pedir nombre
            final Dialog dialogname = new Dialog(Login.this);
            dialogname.setContentView(R.layout.alert_fixer_name);
            // set the custom dialog components - text, image and button
            TextView txtTitle = (TextView) dialogname.findViewById(R.id.txtTitle);
            TextView txtMsj = (TextView) dialogname.findViewById(R.id.txtMsj);
            final EditText edtName = (EditText) dialogname.findViewById(R.id.edtName);
            txtTitle.setText("Atención");
            txtMsj.setText("Para darte un mejor servicio es necesario poner tu nombre completo. Con nombre y apellidos reales, no debe de incluir caracteres especiales com @ o #, ni números.");


            Button btnAceptar = (Button) dialogname.findViewById(R.id.btnAceptar);
            Button btnCancelar = (Button) dialogname.findViewById(R.id.btnCancelar);

            // if button is clicked, close the custom dialog
            btnAceptar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Login(email, pass, edtName.getText().toString(), media, id, image);
                }
            });
            btnCancelar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialogname.show();
        } else {
            dialog.show();
            String url = "http://api.fixerplomeria.com/v1/";
            if (Helper.debug) {
                url = "http://apitest.fixerplomeria.com/v1/";
            }
            AndroidNetworking.post(url + "Login")
                    .addBodyParameter("type", type)
                    .addBodyParameter("email", email)
                    .addBodyParameter("pass", pass)
                    .addBodyParameter("token", refreshedToken)
                    .addBodyParameter("media", media)
                    .addBodyParameter("id", id)
                    .addBodyParameter("name", name)
                    .addBodyParameter("last", "")
                    .addBodyParameter("image", image)
                    .setPriority(Priority.MEDIUM)
                    .build().getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    if (response.toString().equalsIgnoreCase("No user")) {
                        Helper.ShowAlert(Login.this, "Atención", "El usuario no existe en nuestro servicio, verifica el correo y la contraseña de nuevo.", 0);
                        dialog.dismiss();
                    }
                    try {

                        for (int i = 0; i < response.length(); i++) {


                            if (type == "user") {
                                JSONObject object = response.getJSONObject(i);
                                String id_user = object.optString("id_user");
                                String name = object.optString("name");
                                String last_name = object.optString("last_name");
                                String email = object.optString("email");
                                String profile_image = object.optString("profile_img");
                                String status = object.optString("status");

                                String client_id_conekta = object.optString("client_id_conekta");

                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Login.this);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("id_user", id_user);
                                editor.putString("name", name);
                                editor.putString("last_name", last_name);
                                editor.putString("email", email);
                                editor.putInt("badge", 0);
                                editor.putString("profile_image", profile_image);
                                editor.putString("status", status);
                                editor.putBoolean("login", true);

                                if (client_id_conekta.equalsIgnoreCase("0")) {
                                    editor.putBoolean("conekta", false);
                                    Log.e("conekta id  false", client_id_conekta);
                                } else {
                                    editor.putBoolean("conekta", true);
                                    Log.e("conekta id", client_id_conekta);
                                }
                                editor.apply();
                                Helper.WriteLog(Login.this, "Usuario ha iniciado sesión .");
                                finish();
                                Intent intent = new Intent(Login.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                JSONObject object = response.getJSONObject(i);
                                String id_specialist = object.optString("id_specialist");
                                String name = object.optString("name");
                                String last_name = object.optString("last_name");
                                String email = object.optString("email");
                                String profile_image = object.optString("photo");
                                String status = object.optString("status");
                                String phone = object.optString("phone");
                                String age = object.optString("age");
                                String is_contratist = object.optString("is_contratist");
                                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(Login.this);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("id_user", id_specialist);
                                editor.putString("name", name);
                                editor.putString("last_name", last_name);
                                editor.putString("email", email);
                                editor.putInt("badge", 0);
                                editor.putString("phone", phone);
                                editor.putString("age", age);
                                editor.putString("profile_image", profile_image);
                                editor.putString("status", status);
                                editor.putBoolean("login_spe", true);
                                editor.putString("is_contratist", is_contratist);
                                editor.apply();
                                Helper.WriteLog(Login.this, "Especialista ha iniciado sesión .");
                                finish();
                                Intent intent = new Intent(Login.this, MainActivityPlo.class);
                                startActivity(intent);
                            }
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        Log.e("Login error catch", e.toString());
                        Helper.ShowAlert(Login.this, "Atención", "El usuario no existe en nuestro servicio, verifica el correo y la contraseña de nuevo.", 0);

                        dialog.dismiss();
                    }

                }

                @Override
                public void onError(ANError error) {
                    // handle error
                    Log.e("Login error", error.toString());
                    Helper.ShowAlert(Login.this, "Atención", "El usuario no existe en nuestro servicio, verifica el correo y la contraseña de nuevo.", 0);

                    dialog.dismiss();
                }
            });
        }
    }

}
