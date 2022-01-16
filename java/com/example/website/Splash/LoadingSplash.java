package com.example.website.Splash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Homepage;
import com.example.website.MainActivity;
import com.example.website.Permissions.URL;
import com.example.website.Providers.NetworkSchedulerService;
import com.example.website.R;
import com.example.website.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class LoadingSplash extends Activity {
        private static int SPLASH_TIME_OUT = 3000;
        private TextView msg;
        private UserSession session;
        private AlertDialog.Builder builder;
        private String connStatus, server_url;
        private URL url = new URL();
        private RequestQueue rQueue;
        private Boolean firstTime = true;
        private Calendar myCalendar = Calendar.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.loadingsplash);
            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
            msg = (TextView) findViewById(R.id.fullscreen_content);
            msg.setTypeface(typeface);
            builder = new AlertDialog.Builder(LoadingSplash.this);
            session = new UserSession(getApplicationContext());
            scheduleJob();
            if (session.isUserLoggedIn()) {
                msg.setText("שלום, " + session.returnName() + "\n" + "אנא המתן...");
                if(session.isSaved()){
                    connStatus = checkVPN();
                    if (connStatus == "MOBILE") {
                        dialogVPN();
                        firstTime = false;
                    }
                    else {
                        checkLoggedIn();
                    }
                }
                else{
                    endSplashToLogin();
                }
            } else {
                msg.setText("שלום!" + "\n" + "אנא המתן...");
                endSplashToLogin();
            }

        }


        public void endSplashToHomepage(){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent i = new Intent(LoadingSplash.this, Homepage.class);
                    startActivity(i);
                    finish();
                }
            }, SPLASH_TIME_OUT);

        }

    public void endSplashToLogin(){
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(LoadingSplash.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);

    }

    public void checkLoggedIn(){
        JSONObject postToAPI = new JSONObject();
        String myFormat = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        String date = sdf.format(myCalendar.getTime());
        try {
            postToAPI.put("Username", session.returnUsername());
            postToAPI.put("Date", date);
            autoLogUser(postToAPI);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void autoLogUser(JSONObject postToAPI){
        server_url = url.getServer_url("isLoggedIn");
        JSONArray array = new JSONArray();
        array.put(postToAPI);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            if (status.equals("OK")) {
                                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                session.createUserKnisaSession();
                                rQueue.getCache().clear();
                                endSplashToHomepage();
                            } else {
                                session.logoutKnisa();
                                rQueue.getCache().clear();
                                endSplashToHomepage();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("ERROR", volleyError.toString());
                String message = null;
                if (volleyError instanceof NetworkError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ServerError) {
                    message = "The server could not be found. Please try again after some time!!";
                } else if (volleyError instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                } else if (volleyError instanceof NoConnectionError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                } else if (volleyError instanceof TimeoutError) {
                    message = "לא נמצא חיבור עם השרת. \n נא לבדוק חיבור לרשת VPN";
                    builder.setMessage(message);
                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                }
            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(LoadingSplash.this);
        rQueue.add(jsonArrayRequest );

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

    public void dialogVPN() {
        builder = new AlertDialog.Builder(LoadingSplash.this);
        builder.setTitle("חיבור לרשת");
        builder.setMessage("נראה שאתה לא מחובר לרשת, נא לאפשר VPN");
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
                endSplashToLogin();
            }
        });
        builder.show();
    }

    public void dialogVPNOFF() {
        builder = new AlertDialog.Builder(LoadingSplash.this);
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
                endSplashToLogin();
            }
        });
        builder.show();
    }



    private void scheduleJob() {
        JobInfo myJob = new JobInfo.Builder(0, new ComponentName(this, NetworkSchedulerService.class))
                .setRequiresCharging(true)
                .setMinimumLatency(1000)
                .setOverrideDeadline(2000)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .setPersisted(true)
                .build();

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(myJob);
    }

    @Override
    protected void onStop() {
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent); // INTERNET LISTENER
    }

    @Override
    public void onResume() {
        if(!firstTime) {
            connStatus = checkVPN();
            if (session.isUserLoggedIn() && session.isSaved() && connStatus != "MOBILE") {
                checkLoggedIn();
            }
        }
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
    }