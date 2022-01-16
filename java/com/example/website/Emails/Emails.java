package com.example.website.Emails;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Background.AppLifecycleObserver;
import com.example.website.Permissions.URL;
import com.example.website.R;
import com.example.website.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Emails extends AppCompatActivity {
    private String server_url;
    private UserSession session;
    private EmailsRecy rvAdapter;
    private RecyclerView rv;
    private static ProgressDialog mProgressDialog;
    private ArrayList<EmailsAdapter> emailsAdapters;
    private TextView Head, For;
    private long mLastClickTime = 0;
    private Toolbar toolbar;
    private URL url = new URL();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emails);
        session = new UserSession(getApplicationContext());
        setViews();
        setTypeface();
        setToolbar();
        getEmails();

    }

    void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this,cm);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    public void getEmails(){
        showSimpleProgressDialog(this, "שרת","טוען...",false);
        server_url = url.getServer_url("getEmails");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", session.returnUsername());
            jsonObject.put("Name", session.returnName());
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonObject);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        emailsAdapters = new ArrayList<>();
                        try {
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            if (status.equals("NONE")) {
                                String resp = firstArray.getString(1);
                                Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    EmailsAdapter email = new EmailsAdapter();
                                    email.setTopic(jsonObject.getString("Topic"));
                                    email.setMessage(jsonObject.getString("Message"));
                                    email.setDate(jsonObject.getString("Date"));
                                    email.setFrom(jsonObject.getString("From"));
                                    emailsAdapters.add(email);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setupListview();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response", error.toString());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                removeSimpleProgressDialog();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void setupListview(){
        removeSimpleProgressDialog();  //will remove progress dialog
        rv.setLayoutManager(new GridLayoutManager(this,1));
        rvAdapter = new EmailsRecy(Emails.this, emailsAdapters);
        rv.setAdapter(rvAdapter);
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (mProgressDialog != null) {
                if (mProgressDialog.isShowing()) {
                    mProgressDialog.dismiss();
                    mProgressDialog = null;
                }
            }
        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();

        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void showSimpleProgressDialog(Context context, String title,
                                                String msg, boolean isCancelable) {
        try {
            if (mProgressDialog == null) {
                mProgressDialog = ProgressDialog.show(context, title, msg);
                mProgressDialog.setCancelable(isCancelable);
            }

            if (!mProgressDialog.isShowing()) {
                mProgressDialog.show();
            }

        } catch (IllegalArgumentException ie) {
            ie.printStackTrace();
        } catch (RuntimeException re) {
            re.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            finish();
        }

        return super.onOptionsItemSelected(item);
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
        Head =(TextView)findViewById(R.id.emailHead);
        For =(TextView)findViewById(R.id.emailFor);
        rv = (RecyclerView)findViewById(R.id.recEmail);
        rvAdapter = new EmailsRecy(this);
        Head.setText("תיבת דואר", TextView.BufferType.SPANNABLE);
        For.setText(session.returnName(), TextView.BufferType.SPANNABLE);
    }

    void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {Head,For};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }
}
