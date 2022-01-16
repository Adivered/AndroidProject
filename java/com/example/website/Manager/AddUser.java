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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
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

public class AddUser extends AppCompatActivity {
    private EditText Username,Password,Name,Email,Rank;
    private TextView Head,txtUserRank,txtUserName,txtUserEmail,txtUserPassword,txtUserUsername;
    private UserSession session;
    private static ProgressDialog mProgressDialog;
    private long mLastClickTime = 0;
    private ImageView submit;
    private AlertDialog.Builder builder;
    private Toolbar toolbar;
    private URL url = new URL();
    private String server_url;
    private RequestQueue rQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adduser);
        session = new UserSession(getApplicationContext());
        setViews();
        setToolbar();
        setTypeface();
        builder = new AlertDialog.Builder(AddUser.this);
        final Intent intent = getIntent();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                new AlertDialog.Builder(AddUser.this).setMessage("האם אתה בטוח בפרטים?").setPositiveButton("כן",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addUser();
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


    public void addUser(){
        showSimpleProgressDialog(this, "שרת","טוען...",false);
        server_url = url.getServer_url("addUser");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", Username.getText().toString());
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
                                        rQueue.getCache().clear();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                removeSimpleProgressDialog();
                            }}
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response", error.toString());
                rQueue.getCache().clear();
                removeSimpleProgressDialog();

            }
        });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(this);
        rQueue.add(jsonArrayRequest );

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
        TextView[] allTxt = {txtUserUsername,txtUserPassword,txtUserEmail,Head,txtUserName,txtUserRank};
        EditText[] allEt = {Username,Password,Email,Rank,Name};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
        for (EditText editText : allEt) {
            editText.setTypeface(typeface);
        }
    }


    public void setViews(){
        Head =(TextView)findViewById(R.id.addUserHead);
        Username =(EditText)findViewById(R.id.et_AddUser_Username);
        Password =(EditText)findViewById(R.id.et_AddUser_Password);
        Email =(EditText)findViewById(R.id.et_AddUser_Email);
        Rank =(EditText)findViewById(R.id.et_AddUser_Rank);
        Name =(EditText)findViewById(R.id.et_AddUser_Name);
        submit = (ImageView)findViewById(R.id.etAddUser_Button);
        txtUserUsername = (TextView)findViewById(R.id.txtAddUserUsername);
        txtUserPassword = (TextView)findViewById(R.id.txtAddUserPassword);
        txtUserEmail = (TextView)findViewById(R.id.txtAddUserEmail);
        txtUserName = (TextView)findViewById(R.id.txtAddUserName);
        txtUserRank = (TextView)findViewById(R.id.txtAddUserRank);
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
