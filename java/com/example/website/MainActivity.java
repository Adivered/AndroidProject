package com.example.website;

import android.app.ProgressDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Permissions.URL;
import com.example.website.Providers.NetworkSchedulerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private EditText username, password;
    private TextView vpnStatus,check_box_text,knisa_text;
    private CheckBox rememberMe;
    private Button loginButton;
    private static final String PREFER_NAME = "Reg";
    private SharedPreferences sharedPreferences;
    private UserSession session;
    private String connStatus, server_url;
    private AlertDialog.Builder builder;
    private Boolean saveMe = false;
    private long mLastClickTime = 0;
    private RequestQueue rQueue;
    private Calendar myCalendar = Calendar.getInstance();
    private URL url = new URL();
    //private BroadcastReceiver mNetworkReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginpage);
        session = new UserSession(this);
        setViews();
        setFont();
        sharedPreferences = getSharedPreferences(PREFER_NAME, Context.MODE_PRIVATE);
        scheduleJob();
        connStatus = checkVPN();
        Log.v("CheckBox", "--->" + rememberMe.isChecked());
        if (session.isUserLoggedIn() && session.isSaved()) {
            if (connStatus == "MOBILE") {
                dialogVPN();
            }
            else {
                checkLoggedIn();
            }
        }


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                if (rememberMe.isChecked()) {
                    Log.v("CheckBox", "--->" + rememberMe.isChecked());
                    saveMe = true;
                } else {
                    saveMe = false;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                connStatus = checkVPN();
                if (connStatus == "MOBILE") {
                    dialogVPN();
                } else {
                    String userN = username.getText().toString();
                    String userP = password.getText().toString();
                    if (userN.trim().length() >= 3 && userP.trim().length() >= 3) {
                        logUser();
                    }
                    else{
                        new AlertDialog.Builder(MainActivity.this).setMessage("שם משתמש או סיסמא לא תקינים").setPositiveButton("אישור",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                }).create().show();

                    }
                }
            }
        }
        );
    }

    public void logUser(){
        server_url = url.getServer_url("logUser");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מאמת...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        JSONArray array = new JSONArray();
        JSONObject jsonLogin = new JSONObject();
        try {
            jsonLogin.put("Username", username.getText().toString());
            jsonLogin.put("Password", password.getText().toString());
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonLogin);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.v("Response", response.toString());
                        try {
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            if (status.equals("NONE")) {
                                String resp = firstArray.getString(1);
                                progressDialog.dismiss();
                                new AlertDialog.Builder(MainActivity.this).setMessage(resp).setPositiveButton("אישור",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        }).create().show();
                            }
                            else if(status.equals("WRONG")){
                                String resp = firstArray.getString(1);
                                progressDialog.dismiss();
                                new AlertDialog.Builder(MainActivity.this).setMessage(resp).setPositiveButton("אישור",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                }).create().show();
                            }
                            else {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    session.createUserLoginSession(jsonObject.getString("username"), jsonObject.getString("name"), jsonObject.getString("rank"), jsonObject.getString("password"), saveMe);
                                    progressDialog.dismiss();
                                    checkLoggedIn();
                                    break;

                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response", error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();

            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

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
        Log.v("postToApi", postToAPI.toString());
    }
    public String checkVPN() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        String result = "None";
        if (connectivityManager != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities == null) {
                    result = "None";
                }
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    vpnStatus.setVisibility(View.VISIBLE);
                    result = "WIFI";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = "MOBILE";
                    vpnStatus.setVisibility(View.INVISIBLE);
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    vpnStatus.setVisibility(View.VISIBLE);
                    result = "VPN";
                }
            }
        }
        Log.v("checkVPN result: ", result);
        return result;
    }


    public void dialogVPN() {
        builder = new AlertDialog.Builder(MainActivity.this);
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
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (resultCode == RESULT_OK) {
                Log.v("VPN ACTIVITY RESULT", "- RESULT OK ---> setVPNVisible");
                setVPNVisible();
            } else {
                Log.v("VPN ACTIVITY RESULT", "- RESULT CANCEL");
            }
        }
    }

    public void setVPNVisible() {
        String check = checkVPN();
        if (check == "VPN") {
            vpnStatus.setVisibility(View.VISIBLE);
        }
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
        // A service can be "started" and/or "bound". In this case, it's "started" by this Activity
        // and "bound" to the JobScheduler (also called "Scheduled" by the JobScheduler). This call
        // to stopService() won't prevent scheduled jobs to be processed. However, failing
        // to call stopService() would keep it alive indefinitely.
        stopService(new Intent(this, NetworkSchedulerService.class));
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent startServiceIntent = new Intent(this, NetworkSchedulerService.class);
        startService(startServiceIntent); // INTERNET LISTENER
        checkVPN();
    }

    @Override
    public void onResume() {
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

    public void autoLogUser(JSONObject postToAPI){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("שלום " + session.returnName() + "... \n" +"אנא המתן... ");
        progressDialog.setCancelable(true);
        progressDialog.show();
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
                                String resp = firstArray.getString(1);
                                Log.v("Response", resp);
                                    if (status.equals("OK")) {
                                    Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    session.createUserKnisaSession();
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    rQueue.getCache().clear();
                                    finish();
                                }
                                else {
                                    session.logoutKnisa();
                                    Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    progressDialog.dismiss();
                                    rQueue.getCache().clear();
                                    finish();
                                }
                            }catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("ERROR", volleyError.toString());

                }
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(MainActivity.this);
            rQueue.add(jsonArrayRequest );

        }

        public void setViews(){
            username = (EditText) findViewById(R.id.etUsername);
            password = (EditText) findViewById(R.id.etPassword);
            loginButton = (Button) findViewById(R.id.buttonLogin);
            vpnStatus = (TextView) findViewById(R.id.loginpage_vpnStatus);
            knisa_text = (TextView) findViewById(R.id.loginpage_title);
            check_box_text = (TextView) findViewById(R.id.loginpage_saveMeText);
            rememberMe = (CheckBox) findViewById(R.id.saveMe);
        }

        public void setFont(){
            Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
            username.setTypeface(typeface);
            password.setTypeface(typeface);
            vpnStatus.setTypeface(typeface);
            rememberMe.setTypeface(typeface);
            knisa_text.setTypeface(typeface);
            check_box_text.setTypeface(typeface);
            loginButton.setTypeface(typeface);


        }

    }