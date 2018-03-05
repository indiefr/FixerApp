package com.creatio.fixer;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
import com.creatio.fixer.Adapters.ADEvent;
import com.creatio.fixer.Adapters.ADHistory;
import com.creatio.fixer.Adapters.ADNew;
import com.creatio.fixer.Objects.OCalendar;
import com.creatio.fixer.Objects.OOrders;
import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityPlo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public LinearLayout parent;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private AppBarLayout appBar;
    private Button btnBadge;
    private ImageButton btnStilson;
    private int itemBadghe = 0;
    public ListView list_new, list_history;
    private TabHost tabHost;
    private ListView list_calendar;
    private TextView txtName, txtNameH, txtMes;
    private CircleImageView image_profile;
    private ArrayList<OOrders> list = new ArrayList<>();
    private ArrayList<OCalendar> listCalendar = new ArrayList<>();
    private ArrayList<OOrders> listHistory = new ArrayList<>();
    private CalendarView calendarView;
    private String fecha_gral;
    private ImageView background, imgNohistory;
    private RatingBar rtBarSpe;
    private TextView txtRate;
    private String refreshToken = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_plo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //=====[Declare Elements]======
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        list_new = (ListView) findViewById(R.id.list_new);
        list_history = (ListView) findViewById(R.id.listHistory);
        list_calendar = (ListView) findViewById(R.id.list_calendar);
        ImageButton home = (ImageButton) findViewById(R.id.home);
        btnBadge = (Button) findViewById(R.id.btnBadge);
        txtMes = (TextView) findViewById(R.id.txtMes);
        txtName = (TextView) findViewById(R.id.txtName);
        btnStilson = (ImageButton) findViewById(R.id.btnStilson);
        parent = (LinearLayout) findViewById(R.id.snack_linear);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        background = (ImageView) findViewById(R.id.background);
        imgNohistory = (ImageView) findViewById(R.id.imgNohistory);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBar.setOutlineProvider(null);
        }
        //=====[Action of DRAWER]=======
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        appBar.bringToFront();
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_account).setVisible(false);
        nav_Menu.findItem(R.id.nav_iniciar).setVisible(false);
        nav_Menu.findItem(R.id.nav_ordenes).setVisible(false);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivityPlo.this);
        if (pref.getString("is_contratist", "0").equalsIgnoreCase("1")) {
            nav_Menu.findItem(R.id.nav_add).setVisible(true);
            nav_Menu.findItem(R.id.nav_myspe).setVisible(true);
        } else {
            nav_Menu.findItem(R.id.nav_add).setVisible(false);
            nav_Menu.findItem(R.id.nav_myspe).setVisible(false);
        }
        View hView = navigationView.getHeaderView(0);
        txtNameH = (TextView) hView.findViewById(R.id.txtNameUser);
        image_profile = (CircleImageView) hView.findViewById(R.id.image_profile);
        rtBarSpe = (RatingBar) hView.findViewById(R.id.rtBarSpe);
        rtBarSpe.setVisibility(View.VISIBLE);
        txtRate = (TextView) hView.findViewById(R.id.txtRate);
        txtRate.setVisibility(View.VISIBLE);
        navigationView.setNavigationItemSelectedListener(this);
        //Actions of elements
        GetOrdersCount();
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

        //-------------------[FRAGMENTOS]---------------

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        //------------------------------------------------
        /*
         * -------------------[TABHOST]---------------
         * */
        SimpleDateFormat ss = new SimpleDateFormat("EEEE, d MMMM");
        SimpleDateFormat ss2 = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(calendarView.getDate());
        String currentdate = ss.format(date);
        fecha_gral = ss2.format(date);
        tabHost = (TabHost) findViewById(R.id.tabHost);
        tabHost.setup();

        //-------------------------Tab 1
        TabHost.TabSpec spec = tabHost.newTabSpec("Nuevas");
        spec.setContent(R.id.tab1);
        spec.setIndicator("Nuevas");
        tabHost.addTab(spec);
        /*@Configuration */
        LeerOrdenes();
        //------------------------Tab 2
        spec = tabHost.newTabSpec("Próximas");
        spec.setContent(R.id.tab2);

        spec.setIndicator("Próximas");
        if (!pref.getString("is_contratist", "0").equalsIgnoreCase("1")) {
            tabHost.addTab(spec);
        }

        /*@Configuration */

        //-------------------------Tab 3
        spec = tabHost.newTabSpec("Historial");
        spec.setContent(R.id.tab3);
        spec.setIndicator("Historial");
        tabHost.addTab(spec);

        tabHost.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equalsIgnoreCase("Próximas")) {
                    LeerCalendario(fecha_gral);
                }
                if (tabId.equalsIgnoreCase("Nuevas")) {
                    LeerOrdenes();
                }
                if (tabId.equalsIgnoreCase("Historial")) {
                    LeerHistorial();
                }
            }
        });
        //------------------------------------------------
        int items = pref.getInt("badge", 0);
        String image = pref.getString("profile_image", "");
        String name = pref.getString("name", "Sin registro") + " " + pref.getString("last_name", "Sin registro");
        String status = pref.getString("status", "0");
        if (status.equalsIgnoreCase("0")) {
            tabHost.setVisibility(View.INVISIBLE);
            nav_Menu.findItem(R.id.nav_disponibilidad).setTitle("Marcar disponible");
        }
        btnBadge.setText("" + items);
        txtName.setText(name);
        txtNameH.setText(name);

        final ExpandableLayout expandable = (ExpandableLayout) findViewById(R.id.expandable_layout);
        ImageButton btnExpand = (ImageButton) findViewById(R.id.btnExpand);
        btnExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandable.isExpanded()) {
                    expandable.collapse();
                } else {
                    expandable.expand();
                }
            }
        });

        txtMes.setText(currentdate);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth) {
                try {
                    String fecha_selected = year + "-" + (month + 1) + "-" + dayOfMonth;
                    if (month < 10) {
                        fecha_selected = year + "-0" + (month + 1) + "-" + dayOfMonth;
                    } else {
                        fecha_selected = year + "-" + (month + 1) + "-" + dayOfMonth;
                    }
                    SimpleDateFormat ss = new SimpleDateFormat("EEEE, d MMMM");
                    SimpleDateFormat ss2 = new SimpleDateFormat("yyyy-MM-dd");
                    Calendar c = Calendar.getInstance();
                    c.setTime(ss2.parse(fecha_selected));
                    java.util.Date date = c.getTime();
                    String currentdate = ss.format(date);
                    fecha_gral = ss2.format(date);
                    LeerCalendario(fecha_selected);
                    txtMes.setText(currentdate);
                    if (expandable.isExpanded()) {
                        expandable.collapse();
                    } else {
                        expandable.expand();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
        GetRate();
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


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivityPlo.this);
        final SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.nav_iniciar) {
            finish();
            Intent i = new Intent(MainActivityPlo.this, Login.class);
            startActivity(i);
        } else if (id == R.id.nav_close) {
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivityPlo.this);
            alert.setTitle("¿Estás seguro de cerrar la sesión?");
            alert.setMessage("Al cerrar la sesión, se borraran todos tus datos.");
            alert.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Map<String, ?> allEntries = pref.getAll();
                    for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                        edit.remove(entry.getKey());
                        edit.putBoolean("login_spe", false);


                    }
                    edit.apply();
                    finish();
                    Intent i = new Intent(MainActivityPlo.this, Login.class);
                    startActivity(i);
                }
            });
            alert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            alert.show();

        } else if (id == R.id.nav_disponibilidad) {
            int items = pref.getInt("badge", 0);

            String status = pref.getString("status", "0");
            if (status.equalsIgnoreCase("0")) {
                UpdateStatus("1");
                tabHost.setVisibility(View.VISIBLE);
                this.setTitle("Marcar no disponible");
            } else {
                UpdateStatus("0");
                tabHost.setVisibility(View.INVISIBLE);
                this.setTitle("Marcar disponible");
            }
        } else if (id == R.id.nav_add) {
            Intent intent = new Intent(MainActivityPlo.this, RegisterEspecialist.class);
            startActivity(intent);
        } else if (id == R.id.nav_myspe) {
            Intent intent = new Intent(MainActivityPlo.this, MySpecialists.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void UpdateStatus(String status) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "UpdateStatus")
                .addBodyParameter("id_specialist", id_user)
                .addBodyParameter("status", "1")
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {


            }

            @Override
            public void onError(ANError error) {
                // handle error
                Log.e("Login error", error.toString());
            }
        });
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("status", status);
        editor.apply();
    }

    public void BtnReparar(View v) {
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivityPlo.this,
                R.anim.bottom_up);
        ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.snack_linear);
        Button btnReparalo = (Button) hiddenPanel.findViewById(R.id.btnReparalo);
        btnReparalo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AbrirOrden();
            }
        });
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.VISIBLE);
        ChangeBadge();
        v.setEnabled(false);
        if (v.isEnabled()) {
            Toast.makeText(MainActivityPlo.this, "Ya ha sido añadido a tu lista.", Toast.LENGTH_SHORT).show();
        }
    }

    public void AlgoMas(View v) {
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivityPlo.this,
                R.anim.bottom_down);
        ViewGroup hiddenPanel = (ViewGroup) findViewById(R.id.snack_linear);
        hiddenPanel.startAnimation(bottomUp);
        hiddenPanel.setVisibility(View.INVISIBLE);
    }

    public void AbrirOrden() {
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
        Animation bottomUp = AnimationUtils.loadAnimation(MainActivityPlo.this,
                R.anim.bottom_up);
        fr.startAnimation(bottomUp);
        fr.setVisibility(View.VISIBLE);
    }

    public void ChangeBadge() {
        itemBadghe = itemBadghe + 1;
        btnBadge.setText("" + itemBadghe);
    }


    public void LeerCalendario(String fecha_gral) {
        listCalendar = new ArrayList<>();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "ReadCalendar")
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("fecha", fecha_gral)
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_specialist = object.optString("id_specialist");
                        String name_user = object.optString("name");
                        String last_name_user = object.optString("commits");
                        String id_calendary = object.optString("id_calendary");
                        String id_orden = object.optString("id_sale");
                        String status = object.optString("status");
                        listCalendar.add(new OCalendar(id_specialist, name_user, last_name_user, "", "Orden: " + id_orden, "", status, id_orden, null));
                    }
                    ADEvent adapterEvent = new ADEvent(MainActivityPlo.this, listCalendar);
                    list_calendar.setAdapter(adapterEvent);
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

    boolean flag = false;

    public void LeerOrdenes() {
        flag = false;
        list = new ArrayList<>();
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "ReadOrders")
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("type", "specialist")
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {

                try {
                    for (int i = 0; i < response.length(); i++) {
                        flag = true;
                        JSONObject object = response.getJSONObject(i);
                        String id_order = object.optString("id_sale");
                        String create_on = object.optString("create_on");
                        String total = object.optString("total");
                        String subtotal = object.optString("subtotal");
                        String lat_lng = object.optString("lat_lng");
                        String init_date = object.optString("init_date");
                        String id_specialist = object.optString("id_specialist");
                        String name = object.optString("name");
                        String name_user = object.optString("name_user");
                        String last_name = object.optString("last_name");
                        String last_name_user = object.optString("last_name_user");
                        String id_calendary = object.optString("id_calendary");
                        String status_sc = object.optString("status_sc");
                        String status_so = object.optString("status_so");
                        String id_user = object.optString("id_user");
                        String hour_date = object.optString("hour_date");
                        String reference = object.optString("reference");


                        list.add(new OOrders(id_order, create_on, total, subtotal, lat_lng, init_date, id_specialist, name + " " + last_name, id_calendary, name_user, last_name_user, status_sc, status_so, id_user, hour_date, "", "", "", reference, ""));

                    }
                    if (flag) {
                        ADNew adapter = new ADNew(MainActivityPlo.this, list);
                        list_new.setAdapter(adapter);
                    } else {
                        list_new.setVisibility(View.GONE);
                        background.setVisibility(View.VISIBLE);
                    }
                    UpdateToken();
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

    public void LeerHistorial() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        listHistory = new ArrayList<>();
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        String url2 = "ReadOrdersHistoryNormal";
        if (pref.getString("is_contratist", "0").equalsIgnoreCase("1")) {
            url2 = "ReadOrdersHistory";
        }
        final String id_user = pref.getString("id_user", "0");
        AndroidNetworking.post(url + url2)
                .addBodyParameter("id_user", id_user)
                .addBodyParameter("type", "specialist")
                .setPriority(Priority.MEDIUM)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("response his", response.toString());
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String id_order = object.optString("id_sale");
                        String create_on = object.optString("create_on");
                        String total = object.optString("total");
                        String subtotal = object.optString("subtotal");
                        String lat_lng = object.optString("lat_lng");
                        String init_date = object.optString("init_date");
                        String id_specialist = object.optString("id_specialist");
                        String name = object.optString("name");
                        String name_user = object.optString("name_user");
                        String last_name = object.optString("last_name");
                        String last_name_user = object.optString("last_name_user");
                        String id_calendary = object.optString("id_calendary");
                        String status_sc = object.optString("status_sc");
                        String status_so = object.optString("status_so");
                        String id_user = object.optString("id_user");
                        String hour_date = object.optString("hour_date");
                        String reference = object.optString("reference");


                        listHistory.add(new OOrders(id_order, create_on, total, subtotal, lat_lng, init_date, id_specialist, name + " " + last_name, id_calendary, name_user, last_name_user, status_sc, status_so, id_user, hour_date, "", "", "", reference, ""));
                        list_history.setVisibility(View.VISIBLE);
                        imgNohistory.setVisibility(View.GONE);
                        if (pref.getString("is_contratist", "0").equalsIgnoreCase("0")) {
                            ADNew adapter = new ADNew(MainActivityPlo.this, listHistory);
                            list_history.setAdapter(adapter);
                        } else {
                            ADHistory adapter = new ADHistory(MainActivityPlo.this, listHistory);
                            list_history.setAdapter(adapter);
                        }

                    }

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

    public void GetRate() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "GetRate")
                .addBodyParameter("id_specialist", id_user)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String rate = object.optString("rate");
                        double rated = roundTwoDecimals(Double.parseDouble(rate) * 2);
                        rtBarSpe.setRating(Float.parseFloat(rate));
                        txtRate.setText("" + rated);
                    }

                } catch (JSONException e) {
                    Log.e("Login error", e.toString());
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    double roundTwoDecimals(double d) {
        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return Double.valueOf(twoDForm.format(d));
    }

    public void GetOrdersCount() {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "GetOrdersCount")
                .addBodyParameter("id_specialist", id_user)
                .build().getAsJSONArray(new JSONArrayRequestListener() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        JSONObject object = response.getJSONObject(i);
                        String count = object.optString("count");
                        btnBadge.setText("" + count);
                    }

                } catch (JSONException e) {
                    Log.e("Login error", e.toString());
                }
            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }

    public void UpdateToken() {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivityPlo.this);
        FirebaseApp.initializeApp(this);
        refreshToken = FirebaseInstanceId.getInstance().getToken();
        String url = "http://api.fixerplomeria.com/v1/";
        if (Helper.debug) {
            url = "http://apitest.fixerplomeria.com/v1/";
        }
        AndroidNetworking.post(url + "UpdateTokenSpecialist")
                .addBodyParameter("id_user", pref.getString("id_user", ""))
                .addBodyParameter("token", refreshToken)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.e("TAG RATE", response);

            }

            @Override
            public void onError(ANError anError) {

            }
        });
    }
}
