/*package com.example.website;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Providers.MySingelton;
import com.example.website.Providers.OfflineGPS;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class reportLogin2 extends AppCompatActivity {
    TextView Name;
    private String url = "http://192.168.21.118:5000/updateKnisa";
    private String logouturl = "http://192.168.21.118:5000/updateYezia";
    private String imgurl = "http://192.168.21.118:5000/uploadimage";
    AlertDialog.Builder builder;
    private long mLastClickTime = 0;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST = 1888;
    UserSession session;
    String username,name;
    RequestQueue rQueue;
    JSONObject jsonObject;
    JSONArray array;
    private ImageView imgView;
    OfflineGPS gps;
    LinearLayout LoginHourButton, LogoutHourButton, etReturnHome;
    AlertDialog.Builder imageBuilder;
    AlertDialog ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportlogin);
        Name = (TextView) findViewById(R.id.loggedName);
        LoginHourButton = (LinearLayout) findViewById(R.id.etSetLoginHour);
        LogoutHourButton = (LinearLayout) findViewById(R.id.etSetLogoutHour);
        gps = new OfflineGPS(reportLogin.this);
        etReturnHome = (LinearLayout) findViewById(R.id.etReturnHome);
        imgView = (ImageView) findViewById(R.id.imgView);
        builder = new AlertDialog.Builder(reportLogin.this);
        session = new UserSession(getApplicationContext());
        username = session.returnUsername();
        name = session.returnName();
        if (session.isUserLoggedInKnisa()) {
            Log.v("DEBUG", "USER STATUS: " + session.isUserLoggedInKnisa());
            Name.setText(Name.getText().toString() + name, TextView.BufferType.SPANNABLE);
        } else {
            Name.setText(Name.getText().toString() + name + " נראה שעדיין לא ביצעת כניסה למערכת", TextView.BufferType.SPANNABLE);
        }


        etReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new  Intent(getApplicationContext(),Homepage.class);
                startActivity(intent);
                finish();
            }
        });

        LoginHourButton.setOnClickListener(new View.OnClickListener() {
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


        LogoutHourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Log.v("Session", "Status" + session.isUserLoggedInKnisa() );
                if (session.isUserLoggedInKnisa()) {
                    if (session.isPhoneLogSent() == false) {
                        builder.setTitle("דוח סיכום יום");
                        builder.setMessage("נראה שעוד לא שלחת דוח סיכום יום, נא לשלוח");
                        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    } else {
                        final String lat3 = String.valueOf(gps.getLatitude());
                        final String lon3 = String.valueOf(gps.getLongitude());
                        StringRequest stringRequest = new StringRequest(Request.Method.POST, logouturl, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                builder.setTitle("יציאה");
                                if(response.equals("נא לעדכן שעת כניסה קודם")){
                                    builder.setMessage(response);
                                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }else{
                                    builder.setMessage(response);
                                    builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            session.logoutKnisa();
                                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.show();
                                }

                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                error.printStackTrace();
                            }
                        }) {
                            @Override
                            protected Map<String, String> getParams() throws AuthFailureError {
                                Map<String, String> params = new HashMap<String, String>();
                                params.put("username", username);
                                params.put("LAT", lat3);
                                params.put("LONG", lon3);
                                return params;
                            }

                            @Override
                            public Map<String, String> getHeaders() throws AuthFailureError {
                                HashMap<String, String> headers = new HashMap<String, String>();
                                headers.put("Content-Type", "application/json; charset=utf-8");
                                return headers;
                            }
                        };
                        MySingelton.getInstance(reportLogin.this).addToRequesetQueue(stringRequest);

                    }
                }else{
                    Toast.makeText(reportLogin.this, "יש לבצע כניסה למערכת", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        Log.v("Result", "is" + result);
        Log.v("Contents", "is" + result.getContents());
        Log.v("Image Path", "Is " + result.getBarcodeImagePath());
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
        try {
            jsonObject = new JSONObject();
            String imgname = String.valueOf(Calendar.getInstance().getTimeInMillis());
            jsonObject.put("name", imgname);
            Log.e("Image name", imgname.toString().trim());
            jsonObject.put("image", encodedImage);
            Log.e("Image name ", encodedImage.toString().trim());
            jsonObject.put("username", username);
            Log.e("Username: ", username);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, imgurl, jsonObject,
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

        rQueue = Volley.newRequestQueue(reportLogin.this);
        rQueue.add(jsonObjectRequest);

    }

    public  boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
            Log.v("TAG","Permission is granted");
            return true;
        } else {

            Log.v("TAG","Permission is revoked");
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
            return false;

        }
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
        final ProgressDialog progressDialog = new ProgressDialog(reportLogin.this);
        progressDialog.setMessage("אנא המתן...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        final String lat2 = String.valueOf(gps.getLatitude());
        final String lon2 = String.valueOf(gps.getLongitude());
        array = new JSONArray();
        try {
            jsonObject = new JSONObject();
            jsonObject.put("username", username);
            jsonObject.put("name", name);
            jsonObject.put("LAT", lat2);
            jsonObject.put("LONG", lon2);
            jsonObject.put("QRCODE", result.getContents());
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonObject);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            String resp = firstArray.getString(1);
                            if (status.equals("OK")) {
                                Log.v("Response", resp);
                                session.createUserKnisaSession();
                                session.clearPhoneLog();
                                Name.setText(name + " התחברת בהצלחה!");
                                builder.setTitle("התחברות");
                                builder.setMessage(resp);
                                builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                        startActivity(intent);
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

        rQueue = Volley.newRequestQueue(reportLogin.this);
        rQueue.add(jsonArrayRequest );

    }


    @Override
    public void onBackPressed() {
        Intent intent = new  Intent(getApplicationContext(),Homepage.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }
}
*/