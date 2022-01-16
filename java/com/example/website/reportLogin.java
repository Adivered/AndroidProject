package com.example.website;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Permissions.PhoneLog;
import com.example.website.Permissions.URL;
import com.example.website.Providers.OfflineGPS;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;

public class reportLogin extends AppCompatActivity {
    private TextView Name,Status,etLogoutHour,etLoginHour,buttonTXT;
    private String server_url;
    private AlertDialog.Builder builder;
    private long mLastClickTime = 0;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private UserSession session;
    private RequestQueue rQueue;
    private ImageView imgView,etButtonIMG;
    private OfflineGPS gps;
    private LinearLayout etButton;
    private AlertDialog.Builder imageBuilder;
    private AlertDialog ad;
    private ProgressDialog progressDialog;
    private Toolbar toolbar;
    private URL url = new URL();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlogin);
        session = new UserSession(getApplicationContext());
        setViews();
        setToolbar();
        setTypeface();


        getUserStatus();
        if (session.isUserLoggedInKnisa()) {
            Name.setText("שלום, "  + session.returnName(), TextView.BufferType.SPANNABLE);
        } else {
            Name.setText("שלום, " + session.returnName() + " נראה שעדיין לא ביצעת כניסה למערכת", TextView.BufferType.SPANNABLE);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null){
            Bitmap btm = BitmapFactory.decodeFile(result.getBarcodeImagePath());
            showImage(btm, result);
            if(result.getContents() == null){
                //android.widget.Toast.makeText(this, "בוטל באמצע", Toast.LENGTH_LONG).show();
                closeDialog();
            }

        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void showImage(final Bitmap btm, final IntentResult result){
        imageBuilder = new AlertDialog.Builder(this);
        imageBuilder.setTitle("תצוגה מקדימה");
        if(imgView.getParent() != null) {
            ((ViewGroup)imgView.getParent()).removeView(imgView); // <- fix
        }
        imgView.setImageBitmap(btm);
        imageBuilder.setView(imgView);
        imageBuilder.setPositiveButton("שלח", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                uploadImage(btm);
                doLogin(result);
            }
        }).setNegativeButton("בטל", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        ad = imageBuilder.show();

    }

    public void closeDialog(){
        try {
            if (ad != null) {
                if (ad.isShowing()) {
                    ad.cancel();
                    ad = null;
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

    private void uploadImage(Bitmap bitmap){

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        String encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        server_url = url.getServer_url("uploadImage");
        JSONObject jsonObject = new JSONObject();
        try {
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            jsonObject.put("image", encodedImage);
            jsonObject.put("username", session.returnUsername());
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, server_url, jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject jsonObject) {
                        Log.e("aaaaaaa", jsonObject.toString());
                        rQueue.getCache().clear();
                        Toast.makeText(getApplication(), "Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("aaaaaaa", volleyError.toString());

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        rQueue = Volley.newRequestQueue(reportLogin.this);
        rQueue.add(jsonObjectRequest);

    }

    public  boolean isPermissionGranted() {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
    }

    public void doLogin(final IntentResult result){
        server_url = url.getServer_url("updateKnisa");
        final ProgressDialog progressDialog = new ProgressDialog(reportLogin.this);
        progressDialog.setMessage("אנא המתן...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", session.returnUsername());
                jsonObject.put("name", session.returnName());
                jsonObject.put("LAT", gps.getLatitude());
                jsonObject.put("LONG", gps.getLongitude());
                jsonObject.put("QRCODE", result.getContents());
            } catch (JSONException e) {
                Log.e("JSONObject Here", e.toString());
            }
        array.put(jsonObject);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JSONArray firstArray = (JSONArray) response;
                                String status = firstArray.getString(0);
                                String resp = firstArray.getString(1);
                                if (status.equals("OK")) {
                                    session.createUserKnisaSession();
                                    Name.setText(session.returnName() + " התחברת בהצלחה!");
                                    builder.setTitle("התחברות");
                                    builder.setMessage(resp);
                                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    progressDialog.dismiss();
                                    rQueue.getCache().clear();
                                }
                                else{
                                    builder.setTitle("התחברות");
                                    builder.setMessage(resp);
                                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                    progressDialog.dismiss();
                                    rQueue.getCache().clear();
                                }
                            }catch (JSONException e) {
                                    e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Log.e("aaaaaaa", volleyError.toString());

                }
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(reportLogin.this);
            rQueue.add(jsonArrayRequest );

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


    public void getUserStatus(){
        server_url = url.getServer_url("userStatus");
        final ProgressDialog progressDialog = new ProgressDialog(reportLogin.this);
        progressDialog.setMessage("אנא המתן...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", session.returnUsername());
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonObject);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            String baseline = "סטטוס: ";
                            String off_status = "<font color='#B22222'>לא מחובר</font>";
                            //t.setText(Html.fromHtml(first + next));
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            if (status.equals("NONE")) {
                                Status.setText(Html.fromHtml(baseline + off_status ,Html.FROM_HTML_MODE_LEGACY));
                                etLoginHour.setText("שעת התחברות: נא להתחבר");
                                etLogoutHour.setText("שעת התנתקות: אין");
                                progressDialog.dismiss();
                                rQueue.getCache().clear();
                            } else {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    if(session.isUserLoggedInKnisa()){
                                        off_status = "<font color='#228B22'>מחובר</font>";
                                        Status.setText(Html.fromHtml(baseline + off_status ,Html.FROM_HTML_MODE_LEGACY));
                                        //Status.setText("סטטוס: מחובר");
                                    }
                                    else {
                                        off_status = "<font color='#B22222'>מנותק</font>";
                                        Status.setText(Html.fromHtml(baseline + off_status ,Html.FROM_HTML_MODE_LEGACY));
                                        //Status.setText("סטטוס: מנותק");
                                    }
                                    etLoginHour.setText("שעת התחברות: " + jsonObject.getString("loginhour"));
                                    if (jsonObject.isNull("logouthour")) {
                                        etLogoutHour.setText("שעת התנתקות: אין");
                                    } else {
                                        etLogoutHour.setText("שעת התנתקות: " + jsonObject.getString("logouthour"));
                                    }
                                    progressDialog.dismiss();
                                    rQueue.getCache().clear();
                                }
                            }
                            if(session.isUserLoggedInKnisa() == false)
                            {
                                createLoginButton();
                            }else{
                                createLogoutButton();
                            }
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("aaaaaaa", volleyError.toString());
                progressDialog.dismiss();

            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(reportLogin.this);
        rQueue.add(jsonArrayRequest );

    }


    public void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {Name,Status,etLogoutHour,etLoginHour,buttonTXT};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setViews(){
        Name = (TextView) findViewById(R.id.loggedName);
        Status = (TextView) findViewById(R.id.etStatus);
        etLogoutHour = (TextView) findViewById(R.id.etLogoutHour);
        etLoginHour = (TextView) findViewById(R.id.etLoginHour);
        buttonTXT = (TextView) findViewById(R.id.buttonTXT);
        etButton = (LinearLayout) findViewById(R.id.etButton);
        imgView = (ImageView) findViewById(R.id.imgView);
        etButtonIMG = (ImageView) findViewById(R.id.etButtonIMG);
        gps = new OfflineGPS(reportLogin.this);
        builder = new AlertDialog.Builder(reportLogin.this);
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


    @Override
    public void onBackPressed() {
        finish();
    }


    public void doLogout() {
        server_url = url.getServer_url("updateYezia");
        PhoneLog phoneLog = new PhoneLog(this,session.returnUsername(),session.returnName());
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מבצע יציאה \n אנא המתן...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        if (session.isUserLoggedInKnisa()) {
                phoneLog.sendLog();
                JSONArray array = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("Username", session.returnUsername());
                    jsonObject.put("Lat", gps.getLatitude());
                    jsonObject.put("Lon", gps.getLongitude());
                } catch (JSONException e) {
                    Log.e("JSONObject Here", e.toString());
                }
                array.put(jsonObject);
                JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, server_url, array,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                try {
                                    JSONArray firstArray = (JSONArray) response;
                                    String status = firstArray.getString(0);
                                    String resp = firstArray.getString(1);
                                    if (status.equals("OK")) {
                                        session.createUserKnisaSession();
                                        session.clearPhoneLog();
                                        Name.setText(session.returnName() + " שעת יציאה: ");
                                        builder.setTitle("יציאה");
                                        builder.setMessage(resp);
                                        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                session.logoutKnisa();
                                                finish();
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                        progressDialog.dismiss();
                                        rQueue.getCache().clear();
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.e("aaaaaaa", volleyError.toString());

                    }
                });
                jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                rQueue = Volley.newRequestQueue(reportLogin.this);
                rQueue.add(jsonArrayRequest);

            }else{
                progressDialog.dismiss();
                Log.v("FAIL","SEND LOG FAILED");
            }
        }

    public void createLogoutButton() {
        buttonTXT.setText("יציאה מהמערכת");
        etButtonIMG.setImageResource(R.mipmap.export_480);
        etButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if(session.isUserLoggedInKnisa()) {
                    doLogout();
                }
            }
        });
    }

    public void createLoginButton(){
        buttonTXT.setText("כניסה למערכת");
        etButtonIMG.setImageResource(R.mipmap.camera_512);
        etButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                if (session.isUserLoggedInKnisa() == false) {
                    if (isPermissionGranted()) {
                        IntentIntegrator integrator = new IntentIntegrator(reportLogin.this);
                        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
                        integrator.setPrompt("Scan");
                        integrator.setCameraId(0);
                        integrator.setBeepEnabled(false);
                        integrator.setBarcodeImageEnabled(true);
                        integrator.initiateScan();
                    }else{
                        Toast.makeText(reportLogin.this, "נא לאפשר גישה למצלמה", Toast.LENGTH_LONG).show();
                    }
                }else {
                    Toast.makeText(reportLogin.this, "מחובר למערכת, אין צורך להתחבר שנית", Toast.LENGTH_LONG).show();
                }
            }});
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
    public void onStop(){
        super.onStop();
    }
    @Override
    public void onUserLeaveHint(){
        super.onUserLeaveHint();
    }
}
