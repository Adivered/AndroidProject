package com.example.website.Emails;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.example.website.Background.AppLifecycleObserver;
import com.example.website.R;
import com.example.website.UserSession;

public class EmailDisplay extends AppCompatActivity {
    private TextView Topic,Message,From,Date,For,Head;
    private UserSession session;
    private String topic, message,date,from;
    private long mLastClickTime = 0;
    private AlertDialog.Builder builder;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.email);
        session = new UserSession(getApplicationContext());
        setViews();
        setTypeface();
        setToolbar();
        setIntents();
        setText();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this,cm);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.reportlogin_bar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_2_60);
        }
    }

    void setViews(){
        Head =(TextView)findViewById(R.id.subEmailHead);
        For =(TextView)findViewById(R.id.subEmailFor);
        Topic =(TextView)findViewById(R.id.emailTopic);
        Message =(TextView)findViewById(R.id.emailContent);
        From =(TextView)findViewById(R.id.emailFrom);
        Date =(TextView)findViewById(R.id.emailDate);
        builder = new AlertDialog.Builder(EmailDisplay.this);
    }

    public void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {Head,For,Topic,Message,From,Date};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setIntents(){
        Intent intent = getIntent();
        topic = intent.getExtras().getString("כותרת");
        message = intent.getExtras().getString("אימייל");
        from = intent.getExtras().getString("מאת");
        date = intent.getExtras().getString("תאריך");
    }

    void setText(){
        Head.setText("הודעות", TextView.BufferType.SPANNABLE);
        For.setText(session.returnName(), TextView.BufferType.SPANNABLE);
        Topic.setText(topic,TextView.BufferType.SPANNABLE);
        Message.setText(message,TextView.BufferType.SPANNABLE);
        From.setText(from,TextView.BufferType.SPANNABLE);
        Date.setText(date,TextView.BufferType.SPANNABLE);
    }

}