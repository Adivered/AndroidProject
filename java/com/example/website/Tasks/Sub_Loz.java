package com.example.website.Tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;

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

public class Sub_Loz extends AppCompatActivity {
    private TextView taskName,taskInfo,Status,Date,For,Head,txtTaskName,txtTaskInfo,txtTaskDate,txtTaskStatus;
    private UserSession session;
    private static ProgressDialog mProgressDialog;
    private long mLastClickTime = 0;
    private LinearLayout submit;
    private String server_url;
    private AlertDialog.Builder builder;
    private Toolbar toolbar;
    private URL url = new URL();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mesima);
        session = new UserSession(getApplicationContext());
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        setViews();
        //setCycle();
        setToolbar();
        setTypeface();
        Head.setText("משימות", TextView.BufferType.SPANNABLE);
        For.setText(session.returnName(), TextView.BufferType.SPANNABLE);

        builder = new AlertDialog.Builder(Sub_Loz.this);

        final Intent intent = getIntent();
        taskName.setText(intent.getExtras().getString("משימה"), TextView.BufferType.SPANNABLE);
        taskInfo.setText(intent.getExtras().getString("תוכן"), TextView.BufferType.SPANNABLE);
        Status.setText(intent.getExtras().getString("סטטוס"), TextView.BufferType.SPANNABLE);
        Date.setText(intent.getExtras().getString("תאריך"), TextView.BufferType.SPANNABLE);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                new AlertDialog.Builder(Sub_Loz.this).setMessage("אתה בטוח שסיימת?").setPositiveButton("כן",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateMesimaStatus(intent);
                            }
                        }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();

            }
        });
    }


    public void updateMesimaStatus(Intent intent){
            showSimpleProgressDialog(this, "שרת","טוען...",false);
            server_url = url.getServer_url("updateTaskStatus");
            JSONArray array = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", session.returnUsername());
                jsonObject.put("task", intent.getExtras().getString("משימה"));
                jsonObject.put("info", intent.getExtras().getString("תוכן"));
                jsonObject.put("date", intent.getExtras().getString("תאריך") );
            } catch (JSONException e) {
                Log.e("JSONObject Here", e.toString());
            }
            array.put(jsonObject);
            JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("Response", response.toString());
                            JSONArray firstArray = (JSONArray) response;
                            try {
                                String status;
                                status = firstArray.getString(0);
                                String resp = firstArray.getString(1);
                                if (status.equals("OK")) {
                                    Log.v("Response", resp);
                                    builder.setTitle("משימה");
                                    builder.setMessage(resp);
                                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                finish();
                                            }
                                        });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    removeSimpleProgressDialog();
                                }
                                else{
                                    Log.v("Response", resp);
                                    builder.setTitle("משימה");
                                    builder.setMessage("נכשל");
                                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    removeSimpleProgressDialog();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
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

    public void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {taskName,taskInfo,Status,Date,For,Head,txtTaskName,txtTaskInfo,txtTaskDate,txtTaskStatus};
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
        Head =(TextView)findViewById(R.id.submesimaHead);
        For =(TextView)findViewById(R.id.subtaskFor);
        taskName =(TextView)findViewById(R.id.taskName);
        taskInfo =(TextView)findViewById(R.id.taskInfo);
        Status =(TextView)findViewById(R.id.taskStatus);
        Date =(TextView)findViewById(R.id.taskDate);
        submit = (LinearLayout)findViewById(R.id.etSetMesimaDone);
        txtTaskName = (TextView)findViewById(R.id.txtTaskName);
        txtTaskInfo = (TextView)findViewById(R.id.txtTaskinfo);
        txtTaskDate = (TextView)findViewById(R.id.txtTaskDate);
        txtTaskStatus = (TextView)findViewById(R.id.txtTaskStatus);
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

}
