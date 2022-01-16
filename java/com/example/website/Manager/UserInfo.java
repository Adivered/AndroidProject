package com.example.website.Manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Permissions.URL;
import com.example.website.R;
import com.example.website.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class UserInfo extends AppCompatActivity {
    private EditText Username,Password,Name,Email,ID,Rank;
    private TextView For,Head,txtUserRank,txtUserName,txtUserEmail,txtUserPassword,txtUserUsername,txtUserID;
    private UserSession session;
    private static ProgressDialog mProgressDialog;
    private long mLastClickTime = 0;
    private LinearLayout submit;
    private AlertDialog.Builder builder;
    private Toolbar toolbar;
    private URL url = new URL();
    private String server_url;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo);
        session = new UserSession(getApplicationContext());
        setViews();
        setToolbar();
        setTypeface();

        builder = new AlertDialog.Builder(UserInfo.this);
        final Intent intent = getIntent();
        For.setText(intent.getExtras().getString("שם"), TextView.BufferType.SPANNABLE);
        Username.setText(intent.getExtras().getString("משתמש"), TextView.BufferType.SPANNABLE);
        Password.setText(intent.getExtras().getString("סיסמא"), TextView.BufferType.SPANNABLE);
        Email.setText(intent.getExtras().getString("אימייל"), TextView.BufferType.SPANNABLE);
        Name.setText(intent.getExtras().getString("שם"), TextView.BufferType.SPANNABLE);
        ID.setText(intent.getExtras().getString("מספר"), TextView.BufferType.SPANNABLE);
        Rank.setText(intent.getExtras().getString("דרגה"), TextView.BufferType.SPANNABLE);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                new AlertDialog.Builder(UserInfo.this).setMessage("אתה בטוח שברצונך לערוך משתמש זה?").setPositiveButton("כן",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                updateUserDetails(intent);
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


    public void updateUserDetails(Intent intent){
        showSimpleProgressDialog(this, "שרת","טוען...",false);
        server_url = url.getServer_url("updateUserDetails");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", session.returnUsername());
            jsonObject.put("NewUsername", Username.getText().toString());
            jsonObject.put("Password", Password.getText().toString());
            jsonObject.put("Email", Email.getText().toString());
            jsonObject.put("Name", Name.getText().toString());
            jsonObject.put("Rank", Rank.getText().toString());
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
        TextView[] allTxt = {txtUserUsername,txtUserPassword,txtUserID,txtUserEmail,For,Head,txtUserName,txtUserRank};
        EditText[] allEt = {Username,Password,Email,Rank,Name};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
        for (EditText editText : allEt) {
            editText.setTypeface(typeface);
        }
    }


    public void setViews(){
        Head =(TextView)findViewById(R.id.userInfoHead);
        For =(TextView)findViewById(R.id.userInfoFor);
        ID =(EditText)findViewById(R.id.et_userinfo_userID);
        Username =(EditText)findViewById(R.id.et_userinfo_Username);
        Password =(EditText)findViewById(R.id.et_userinfo_Password);
        Email =(EditText)findViewById(R.id.et_userinfo_Email);
        Rank =(EditText)findViewById(R.id.et_userinfo_Rank);
        Name =(EditText)findViewById(R.id.et_userinfo_Name);
        submit = (LinearLayout)findViewById(R.id.etEditUserInformation);
        txtUserUsername = (TextView)findViewById(R.id.txtUserUsername);
        txtUserPassword = (TextView)findViewById(R.id.txtUserPassword);
        txtUserID = (TextView)findViewById(R.id.txtUserID);
        txtUserEmail = (TextView)findViewById(R.id.txtUserEmail);
        txtUserName = (TextView)findViewById(R.id.txtUserName);
        txtUserRank = (TextView)findViewById(R.id.txtUserRank);
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
