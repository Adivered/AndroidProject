package com.example.website;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.RequestQueue;
import com.example.website.Background.AppLifecycleObserver;
import com.example.website.Manager.managerPage;
import com.example.website.Permissions.Permissions;
import com.example.website.Providers.OfflineGPS;
import com.example.website.Splash.LoadingMailSplash;
import com.example.website.Tasks.Loz;
import com.google.android.material.navigation.NavigationView;

public class Homepage extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private TextView Name;
    private LinearLayout knisaPage, pgishPage, tasksPage , emailsPage;
    private DrawerLayout drawer;
    private NavigationView navigationView;
    private UserSession session;
    private OfflineGPS gps;
    private long mLastClickTime = 0;
    private AlertDialog.Builder builder;
    private Typeface typeface;
    private ProgressDialog progressDialog;
    private RequestQueue rQueue;
    private ActionBarDrawerToggle mActionBarDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);
        session = new UserSession(Homepage.this);
        setCycle();
        setViews();
        setNavigation();
        setActionbar();
        setTypeface();
        setWelcomeMessage();
        if (isLocationPermission()) {
            gps = new OfflineGPS(Homepage.this);
        }

        // KNISA
        knisaPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(Homepage.this, reportLogin.class);
                startActivity(intent);
            }
        });

        // EMAILS
        emailsPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(Homepage.this, LoadingMailSplash.class);
                startActivity(intent);
            }
        });

        // PGISHA
        pgishPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (session.isUserLoggedInKnisa()) {
                    Intent intent = new Intent(Homepage.this, ReportMeeting.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Homepage.this, "יש לבצע כניסה למערכת", Toast.LENGTH_LONG).show();
                }
            }
        });

        // Mesimot
        tasksPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (session.isUserLoggedInKnisa()) {
                    Intent intent = new Intent(Homepage.this, Loz.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(Homepage.this, "יש לבצע כניסה למערכת", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void dialogVPNOFF() {
        builder = new AlertDialog.Builder(Homepage.this);
        builder.setTitle("חיבור לרשת");
        builder.setMessage("כיבוי VPN");
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.net.vpn.SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, 10);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("התעלם", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        builder.show();
    }


    private void foo(){
        registerReceiver(connectionBroadcastReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }


    private BroadcastReceiver connectionBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getExtras() == null)
                return;
            ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getState() == NetworkInfo.State.CONNECTED) {
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                   // Toast.makeText(Homepage.this, "Permission granted", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Homepage.this, "לא ניתנה הרשאה", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public boolean isLocationPermission() {
        if(!Permissions.Check_FINE_LOCATION(Homepage.this))
        {
            Permissions.Request_FINE_LOCATION(Homepage.this,22);
            return true;
        }
        else{
            return false;
        }
    }

    private void showEvent()
    {
        Menu nav_Menu = navigationView.getMenu();
        nav_Menu.findItem(R.id.manager).setVisible(true);
    }


    @Override
    public boolean onNavigationItemSelected(final MenuItem item) {
        switch(item.getItemId()) {
            case R.id.logofftoolbar:
                mLastClickTime = SystemClock.elapsedRealtime();
                new AlertDialog.Builder(Homepage.this).setMessage("האם ברצונך להתנתק לחלוטין?").setPositiveButton("כן",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                session.logoutKnisa();
                                session.clearPhoneLog();
                                session.logoutUser();
                                dialogVPNOFF();
                                Intent intent = new Intent(Homepage.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                                Toast.makeText(Homepage.this, "להתראות", Toast.LENGTH_LONG).show();
                            }
                        }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
                return true;

            case R.id.manager:
                Intent intent = new Intent(Homepage.this, managerPage.class);
                startActivity(intent);
                return true;

        }
        return true;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mActionBarDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
        mActionBarDrawerToggle.onConfigurationChanged(newConfig);
    }

    public String checkVPN() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        String result = "None";
        if (connectivityManager != null) {
                Network network = connectivityManager.getActiveNetwork();
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities == null) {
                    result = "None";
                }
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = "WIFI";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = "MOBILE";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = "VPN";
                }
            }
        Log.v("checkVPN result: ", result);
        return result;
    }

    @Override
    public boolean onPrepareOptionsMenu(final Menu menu)
    {
        menu.clear();
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void setNavigationDetails(){
        if(session.returnRank().equals("manager")){
            showEvent();
        }
        View header = navigationView.getHeaderView(0);
        TextView navName = (TextView)header.findViewById(R.id.navigation_privatename);
        navName.setText(session.returnName());
        navName.setTypeface(typeface);
        TextView navRank = (TextView)header.findViewById(R.id.navigation_person_rank);
        navRank.setText("סקודה סיטיגו");
        navRank.setTypeface(typeface);
    }

    void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this, cm);
            ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    void setViews(){
            Name = (TextView) findViewById(R.id.loggedName);
            knisaPage = (LinearLayout) findViewById(R.id.buttonLoginReport);
            pgishPage = (LinearLayout) findViewById(R.id.buttonMeetingReport);
            tasksPage = (LinearLayout) findViewById(R.id.buttonMesimot);
            emailsPage = (LinearLayout) findViewById(R.id.buttonEmails);
            drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            navigationView = (NavigationView) findViewById(R.id.nav_view);
    }

    void setWelcomeMessage(){
        if (session.isUserLoggedInKnisa()) {
            Name.setText("שלום, " + session.returnName(), TextView.BufferType.SPANNABLE);
        } else {
            Name.setText("שלום, " + session.returnName() + "\n" + " נראה שעדיין לא ביצעת כניסה למערכת", TextView.BufferType.SPANNABLE);
        }
    }

    public void setTypeface(){
        TextView[] allTxt = {Name};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setNavigation(){
        navigationView.setNavigationItemSelectedListener(Homepage.this);
        navigationView.bringToFront();
        navigationView.getMenu().getItem(0).setChecked(false);
        setNavigationDetails();
    }

    void setActionbar(){
        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(mActionBarDrawerToggle);
        mActionBarDrawerToggle.syncState();

    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        String result = checkVPN();
        if (drawer.isDrawerOpen(Gravity.RIGHT)) {
            drawer.closeDrawer(Gravity.RIGHT);
        }else if(result == "VPN"){
            dialogVPNOFF();
        }else{
            moveTaskToBack(true);
        }
    }

    @Override
    public void onResume() {
        setWelcomeMessage();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onUserLeaveHint(){
        super.onUserLeaveHint();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }



}