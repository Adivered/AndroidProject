package com.example.website.Manager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.RequestQueue;
import com.example.website.Background.AppLifecycleObserver;
import com.example.website.R;
import com.example.website.UserSession;

import java.util.Calendar;

public class Meetings extends AppCompatActivity {

    TextView Koteret;
    Button addMeeting,deleteMeeting,todayMeetings, futureMeetings;
    UserSession session;
    private static ProgressDialog mProgressDialog;
    AlertDialog.Builder builder;
    private long mLastClickTime = 0;
    DatePickerDialog.OnDateSetListener dateBuilder;
    static AlertDialog dialogBuilder;
    Calendar myCalendar = Calendar.getInstance();
    RequestQueue rQueue;
    Typeface typeface;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager_meetings);
        setViews();
        setToolbar();
        setTypeface();
        session = new UserSession(getApplicationContext());

        addMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
            }
        });

        deleteMeeting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();;
            }
        });

        todayMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();;
            }
        });

        futureMeetings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();;
            }
        });
    }


    public void setTypeface(){
        typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {Koteret};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this,cm);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    public void setViews(){
        Koteret = (TextView) findViewById(R.id.pgishaHead);
        todayMeetings = (Button) findViewById(R.id.todayPgisha);
        futureMeetings = (Button) findViewById(R.id.futurePgisha);
        addMeeting = (Button) findViewById(R.id.addPgisha);
        deleteMeeting = (Button) findViewById(R.id.deletePgisha);
    }

    public void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.reportlogin_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_2_60);

        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}
