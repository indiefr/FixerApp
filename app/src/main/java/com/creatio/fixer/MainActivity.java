package com.creatio.fixer;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.creatio.fixer.Adapters.ADServices;
import com.creatio.fixer.Objects.OServices;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public ListView list_services;
    public LinearLayout parent;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    AppBarLayout appBar;
    Button btnBadge;
    ImageButton btnStilson;
    int item = 0;
    int itemBadghe = 0;
    TextView txtTitleService;
    ArrayList<OServices> servicesGral;
    String id_specialist, init_date, subtotal;
    int hour_date;
    TextView txtName;
    CircleImageView image_profile;
    boolean oxxo = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // --- [Declare Elements] ---
        ImageButton home = (ImageButton) findViewById(R.id.home);
        btnBadge = (Button) findViewById(R.id.btnBadge);
        btnStilson = (ImageButton) findViewById(R.id.btnStilson);

        list_services = (ListView) findViewById(R.id.list_services);
        parent = (LinearLayout) findViewById(R.id.snack_linear);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBar.setOutlineProvider(null);
        }
        appBar.bringToFront();
        CircleImageView image_profile_button = (CircleImageView) findViewById(R.id.image_profile_button);
        // --- [Cargar funciones] ---
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        LeerServicios();


        // --- [Action of DRAWER] ---
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_disponibilidad).setVisible(false);
        nav_Menu.findItem(R.id.nav_iniciar).setVisible(false);
        View hView = navigationView.getHeaderView(0);
        txtName = (TextView) hView.findViewById(R.id.txtNameUser);
        image_profile = (CircleImageView) hView.findViewById(R.id.image_profile);

        navigationView.setNavigationItemSelectedListener(this);
        // --- [Actions of elements] ---
        btnStilson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirOrden();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });
        btnBadge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirOrden();
            }
        });
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    drawer.openDrawer(GravityCompat.START);
                }
            }
        });

        //Manejo de fragmentos

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();


        //-------------------

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int items = pref.getInt("badge", 0);
        String image = pref.getString("profile_image", "");
        image = image.replace("\"", "");
        image = image.replace('\\', '/');
        image = image.replace("//", "/");
        String name = pref.getString("name", "Sin registro") + " " + pref.getString("last_name", "Sin registro");
        btnBadge.setText("" + items);
        txtName.setText(name);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        final SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        if (id == R.id.nav_close) {
            // Handle the camera action
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("¿Estás seguro de cerrar la sesión?");
            alert.setMessage("Al cerrar la sesión, se borraran todos tus datos.");
            alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {

                        edit.remove(entry.getKey());
                        edit.putBoolean("login", false);

                    }
                    edit.apply();
                    finish();
                    Intent i = new Intent(MainActivity.this, Login.class);
                    startActivity(i);
                }
            });
            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();

        } else if (id == R.id.nav_ordenes) {

            Intent i = new Intent(MainActivity.this, Ordenes.class);
            startActivity(i);

        } else if (id == R.id.nav_iniciar) {
            finish();
            Intent i = new Intent(MainActivity.this, Login.class);
            startActivity(i);
        } else if (id == R.id.nav_account) {
            finish();
            Intent i = new Intent(MainActivity.this, MyAccount.class);
            startActivity(i);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void AlgoMas(View v) {
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.bottom_down);
        ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.snack_linear);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.INVISIBLE);
    }

    public void Detalles(String id_service, String title_service, String desc_service, String image) {
        Detalles hello = new Detalles();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager != null) {
            fragmentTransaction.attach(hello);
        }
        Bundle bundle = new Bundle();
        bundle.putString("title_service", title_service);
        bundle.putString("desc_service", desc_service);
        bundle.putString("id_service", id_service);
        bundle.putString("image", image);
        hello.setArguments(bundle);
        fragmentTransaction.add(R.id.fragment_container, hello, "Detalles");
        setTitle("Detalles");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        FrameLayout fr = (FrameLayout) findViewById(R.id.fragment_container);
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.bottom_up);
        fr.startAnimation(bottomUp);
        fr.setVisibility(View.VISIBLE);
    }

    public void Cerrar() {
        FrameLayout fr = (FrameLayout) findViewById(R.id.fragment_container);
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.fade);
        fr.startAnimation(bottomUp);
        fr.setVisibility(View.GONE);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int items = pref.getInt("badge", 0);
        btnBadge.setText("" + items);

    }

    public void AbrirOrden() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int items = pref.getInt("badge", 0);
        if (items == 0) {
            Helper.ShowAlert(MainActivity.this, "No hay elementos en la orden", "Te invitamos a seleccionar al menos un servicio para poder ver la orden", 0);
            return;

        }
        Orden hello = new Orden();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager != null) {
            fragmentTransaction.attach(hello);
        }
        fragmentTransaction.add(R.id.fragment_container, hello, "Orden");
        setTitle("Orden");
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        FrameLayout fr = (FrameLayout) findViewById(R.id.fragment_container);
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivity.this,
                R.anim.bottom_up);
        fr.startAnimation(bottomUp);
        fr.setVisibility(View.VISIBLE);
    }

    public void AbrirCalendar(String total) {

        Calendario hello = new Calendario();
        fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        fragmentTransaction = fragmentManager.beginTransaction();
        if (fragmentManager != null) {
            fragmentTransaction.attach(hello);
        }
        fragmentTransaction.add(R.id.fragment_container, hello, "Calendario");
        setTitle("Calendario");
        this.subtotal = total;
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();

    }

    public void ChangeBadge(String id_service, String type) {
        //type = 0 es nuevo, type =  1 es pre
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int items = pref.getInt("badge", 0);
        itemBadghe = items + 1;
        btnBadge.setText("" + itemBadghe);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("id_service" + id_service, id_service + "|" + type);
        editor.putInt("badge", itemBadghe);

        editor.apply();

    }

    public void OpenPlace(String id_specialist, String init_date, int hour_date) {
        this.id_specialist = id_specialist;
        this.init_date = init_date;
        this.hour_date = hour_date;
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            startActivityForResult(builder.build(MainActivity.this), 199);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        Log.e("Entro places", "Entro");
        super.onActivityResult(requestCode, resultCode, data);
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final Map<String, ?> allEntries = pref.getAll();

        if (requestCode == 199 && resultCode == -1) {
            //Lanzar alerta metodo de pago
            final Place place = PlacePicker.getPlace(data, this);
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle("Atención");

            LayoutInflater inflater = getLayoutInflater();
            View views = inflater.inflate(R.layout.alert_metodo, null);
            RadioGroup rdgGrupo = (RadioGroup) views.findViewById(R.id.rdgGrupo);
            rdgGrupo.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                    if (checkedId == R.id.radioButton) {
                        oxxo = false;
                    } else {
                        oxxo = true;
                    }
                }
            });
            alert.setView(views);
            alert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(MainActivity.this, CardForm.class);

                    String toastMsg = String.format("Place: %s", place.getName());
                    // BuildNotification("El tecnico a revisado tu solicitud en la dirección " + place.getName());
                    String latlng = place.getLatLng().latitude + "," + place.getLatLng().longitude;
                    intent.putExtra("latlng", latlng);
                    intent.putExtra("id_specialist", id_specialist);
                    intent.putExtra("init_date", init_date);
                    intent.putExtra("hour_date", hour_date);
                    intent.putExtra("subtotal", subtotal);
                    intent.putExtra("oxxo", oxxo);
                    startActivity(intent);
                    Cerrar();
                }
            });
            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        int items = pref.getInt("badge", 0);
        btnBadge.setText("" + items);
    }

    public void LeerServicios() {
        final ProgressDialog dialog = ProgressDialog.show(MainActivity.this, null, "Cargando servicio");
        // --- [Header elements] ---
        dialog.show();
        servicesGral = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/Services")
                .setPriority(Priority.IMMEDIATE)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("res ", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_service = object.optString("id_service");
                        String name = object.optString("name");
                        String description = object.optString("description");
                        String image = object.optString("image");
                        String time_pre = object.optString("time_pre");
                        String time_new = object.optString("time");
                        servicesGral.add(new OServices(id_service, image, name, description, time_pre, time_new, "", ""));


                    }
                    LayoutInflater myinflater = getLayoutInflater();
                    final ViewGroup myHeader = (ViewGroup) myinflater.inflate(R.layout.header_services, list_services, false);
                    list_services.removeHeaderView(myHeader);
                    txtTitleService = (TextView) myHeader.findViewById(R.id.txtTitleService);


                    final LinearLayout ly_gral = (LinearLayout) myHeader.findViewById(R.id.ly_gral);

                    Glide.with(MainActivity.this)
                            .load(servicesGral.get(0).getImage())
                            .error(R.drawable.transition_image)
                            .placeholder(R.drawable.placeholder)
                            .into(new SimpleTarget<GlideDrawable>() {
                                @Override
                                public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                    ly_gral.setBackground(resource);
                                }
                            });
                    txtTitleService.setText(servicesGral.get(0).getTitle());
                    ImageButton btnAfter = (ImageButton) myHeader.findViewById(R.id.btnAfter);
                    btnAfter.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            ADServices adapter = new ADServices(MainActivity.this, services, services.get(item).getTitle());
//                            list_services.setAdapter(adapter);
//                            LinearLayout ly_gral = (LinearLayout) myHeader.findViewById(R.id.ly_gral);
//                            final TransitionDrawable transition = (TransitionDrawable) ly_gral.getBackground();
//                            transition.reverseTransition(200);
                            item = item - 1;

                            if (item > (servicesGral.size() - 1)) {
                                item = 0;

                            }
                            if (item < 0) {
                                item = (servicesGral.size() - 1);
                            }
                            Glide.with(MainActivity.this)
                                    .load(servicesGral.get(item).getImage())
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(new SimpleTarget<GlideDrawable>() {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                            ly_gral.setBackground(resource);
                                        }
                                    });
                            ListarServicios(servicesGral.get(item).getId_service(), true);
//                            LinearLayout ly_gral = (LinearLayout) myHeader.findViewById(R.id.ly_gral);
//                            final TransitionDrawable transition = (TransitionDrawable) ly_gral.getBackground();
//                            transition.reverseTransition(200);

//                            list_services.animate()
//                                    .translationX(-list_services.getWidth())
//                                    .alpha(1.0f)
//                                    .setDuration(200)
//                                    .setListener(new AnimatorListenerAdapter() {
//                                        @Override
//                                        public void onAnimationEnd(Animator animation) {
//                                            super.onAnimationEnd(animation);
//                                            list_services.setVisibility(View.GONE);
//                                            list_services.animate()
//                                                    .translationX(0)
//                                                    .alpha(1.0f)
//                                                    .setDuration(100)
//                                                    .setListener(new AnimatorListenerAdapter() {
//                                                        @Override
//                                                        public void onAnimationEnd(Animator animation) {
//                                                            super.onAnimationEnd(animation);
//                                                            list_services.setVisibility(View.VISIBLE);
//                                                        }
//                                                    });
//                                        }
//                                    });


                        }
                    });
                    ImageButton btnBefore = (ImageButton) myHeader.findViewById(R.id.btnBefore);
                    btnBefore.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            ADServices adapter = new ADServices(MainActivity.this, services, services.get(item).getTitle());
//                            list_services.setAdapter(adapter);
//                            LinearLayout ly_gral = (LinearLayout) myHeader.findViewById(R.id.ly_gral);
//                            final TransitionDrawable transition = (TransitionDrawable) ly_gral.getBackground();
//                            transition.startTransition(200);
                            item = item + 1;
                            if (item > (servicesGral.size() - 1)) {
                                item = 0;
                            }
                            if (item < 0) {
                                item = (servicesGral.size() - 1);
                            }
                            Glide.with(MainActivity.this)
                                    .load(servicesGral.get(item).getImage())
                                    .placeholder(R.drawable.placeholder)
                                    .error(R.drawable.placeholder)
                                    .into(new SimpleTarget<GlideDrawable>() {
                                        @Override
                                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> glideAnimation) {
                                            ly_gral.setBackground(resource);
                                        }
                                    });
                            ListarServicios(servicesGral.get(item).getId_service(), false);
//                            LinearLayout ly_gral = (LinearLayout) myHeader.findViewById(R.id.ly_gral);
//
//                            final TransitionDrawable transition = (TransitionDrawable) ly_gral.getBackground();
//                            transition.startTransition(200);
//                            list_services.animate()
//                                    .translationX(list_services.getWidth())
//                                    .alpha(1.0f)
//                                    .setDuration(200)
//                                    .setListener(new AnimatorListenerAdapter() {
//                                        @Override
//                                        public void onAnimationEnd(Animator animation) {
//                                            super.onAnimationEnd(animation);
//                                            list_services.setVisibility(View.GONE);
//                                            list_services.animate()
//                                                    .translationX(0)
//                                                    .alpha(1.0f)
//                                                    .setDuration(100)
//                                                    .setListener(new AnimatorListenerAdapter() {
//                                                        @Override
//                                                        public void onAnimationEnd(Animator animation) {
//                                                            super.onAnimationEnd(animation);
//                                                            list_services.setVisibility(View.VISIBLE);
//                                                        }
//                                                    });
//                                        }
//                                    });


                        }
                    });
                    list_services.addHeaderView(myHeader, null, false);
                    ListarServicios(servicesGral.get(0).getId_service(), false);
                    dialog.dismiss();
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

    public void ListarServicios(String id_service, final boolean flagAfter) {
        final ArrayList<OServices> services = new ArrayList<>();
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ChildrenServices")
                .addBodyParameter("id_service", id_service)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("res ", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_service = object.optString("id_service");
                        String name = object.optString("name");
                        String image = object.optString("image");
                        String description = object.optString("description");
                        String time_pre = object.optString("time_pre");
                        String time_new = object.optString("time");
                        services.add(new OServices(id_service, image, name, description, time_pre, time_new, "", ""));
                    }
                    ADServices adapter = new ADServices(MainActivity.this, services, flagAfter);

                    list_services.setAdapter(adapter);
                    list_services.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Detalles(services.get(position - 1).getId_service(), services.get(position - 1).getTitle(), services.get(position - 1).getDesc(),services.get(position - 1).getImage());
                        }
                    });

                    txtTitleService.setText(servicesGral.get(item).getTitle());
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
