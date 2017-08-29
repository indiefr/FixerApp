package com.creatio.fixer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
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
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.bumptech.glide.Glide;
import com.creatio.fixer.Adapters.ADEvent;
import com.creatio.fixer.Adapters.ADNew;
import com.creatio.fixer.Objects.OCalendar;
import com.creatio.fixer.Objects.OOrders;

import net.cachapa.expandablelayout.ExpandableLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivityPlo extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public LinearLayout parent;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    AppBarLayout appBar;
    Button btnBadge;
    ImageButton btnStilson;
    int itemBadghe = 0;
    public ListView list_new;
    TabHost tabHost;
    ListView list_calendar;
    TextView txtName, txtNameH, txtMes;
    CircleImageView image_profile;
    ArrayList<OOrders> list = new ArrayList<>();
    ArrayList<OCalendar> listCalendar = new ArrayList<>();
    CalendarView calendarView;
    String fecha_gral;
    ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_plo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //Declare Elements
        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();
        calendarView = (CalendarView) findViewById(R.id.calendarView);
        list_new = (ListView) findViewById(R.id.list_new);
        list_calendar = (ListView) findViewById(R.id.list_calendar);
        ImageButton home = (ImageButton) findViewById(R.id.home);
        btnBadge = (Button) findViewById(R.id.btnBadge);
        txtMes = (TextView) findViewById(R.id.txtMes);
        txtName = (TextView) findViewById(R.id.txtName);
        btnStilson = (ImageButton) findViewById(R.id.btnStilson);
        parent = (LinearLayout) findViewById(R.id.snack_linear);
        appBar = (AppBarLayout) findViewById(R.id.appBar);
        background = (ImageView) findViewById(R.id.background);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            appBar.setOutlineProvider(null);
        }
        //Action of DRAWER
        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        final ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.nav_iniciar).setVisible(false);
        nav_Menu.findItem(R.id.nav_ordenes).setVisible(false);
        View hView = navigationView.getHeaderView(0);
        txtNameH = (TextView) hView.findViewById(R.id.txtNameUser);
        image_profile = (CircleImageView) hView.findViewById(R.id.image_profile);

        navigationView.setNavigationItemSelectedListener(this);
        //Actions of elements

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
        tabHost.addTab(spec);
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
            }
        });
        //------------------------------------------------
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
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
        Glide.with(MainActivityPlo.this)
                .load(image)
                .error(R.drawable.no_user)
                .into(image_profile);
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
        SharedPreferences.Editor edit = pref.edit();
        edit.clear();
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.nav_iniciar) {
            finish();
            Intent i = new Intent(MainActivityPlo.this, Login.class);
            startActivity(i);
        } else if (id == R.id.nav_close) {
            // Handle the camera action
            Map<String, ?> allEntries = pref.getAll();
            for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                edit.remove(entry.getKey());
                edit.putBoolean("login_spe", false);


            }
            edit.apply();
            finish();
            Intent i = new Intent(MainActivityPlo.this, Login.class);
            startActivity(i);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void UpdateStatus(String status) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        final String id_user = pref.getString("id_user", "0");
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/UpdateStatus")
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
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ReadCalendar")
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
        AndroidNetworking.post("http://api.fixerplomeria.com/v1/ReadOrders")
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


                        list.add(new OOrders(id_order, create_on, total, subtotal, lat_lng, init_date, id_specialist, name + " " + last_name, id_calendary, name_user, last_name_user, status_sc, status_so, id_user, hour_date,"",""));

                    }
                    if (flag) {
                        ADNew adapter = new ADNew(MainActivityPlo.this, list);
                        list_new.setAdapter(adapter);
                    } else {
                        list_new.setVisibility(View.GONE);
                        background.setVisibility(View.VISIBLE);
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

}
