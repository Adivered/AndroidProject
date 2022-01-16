package com.example.website.Tasks;
/*
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Homepage;
import com.example.website.MainActivity;
import com.example.website.Providers.MySingelton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class check {

    ProgressDialog progressDialog;
    JSONArray array;
    JSONObject jsonPhoneLogObject;




         public void sendLog() {
            progressDialog = new ProgressDialog(Homepage.this);
            progressDialog.setMessage("אנא המתן...");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            array = new JSONArray();
            if (isPermissionGranted()) {
                StringBuffer sb = new StringBuffer();
                Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                        null, null, null);
                int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
                int contact = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
                int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
                int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
                int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
                sb.append("Call Details :");
                while (managedCursor.moveToNext()) {
                    JSONObject jsonParam = new JSONObject();
                    String phNumber = managedCursor.getString(number);
                    String contactName = managedCursor.getString(contact);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    String dir = null;
                    int dircode = Integer.parseInt(callType);
                    switch (dircode) {
                        case CallLog.Calls.OUTGOING_TYPE:
                            dir = "שיחה יוצאת";
                            break;

                        case CallLog.Calls.INCOMING_TYPE:
                            dir = "שיחה נכנסת";
                            break;

                        case CallLog.Calls.MISSED_TYPE:
                            dir = "שיחה שלא נענתה";
                            break;
                    }
                    try {
                        jsonParam.put("Contact", contactName);
                        jsonParam.put("Number", phNumber);
                        jsonParam.put("Type", dir);
                        jsonParam.put("Date", callDayTime);
                        jsonParam.put("Duration", callDuration);
                        jsonParam.put("Username", session.returnUsername());
                        jsonParam.put("Userfullname", session.returnName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.print(jsonParam.toString());
                    array.put(jsonParam);
                }
                Log.v("list", array.toString());
                JsonArrayRequest jobReq = new JsonArrayRequest(Request.Method.POST, url, array,
                        new Response.Listener<JSONArray>() {
                            @Override
                            public void onResponse(JSONArray response) {
                                Log.v("list", array.toString());
                                session.setRegisterPhoneLog();
                                builder = new AlertDialog.Builder(Homepage.this);
                                builder.setTitle("דוח סיכום יום");
                                builder.setMessage("נשלח");
                                Log.v("Response", response.toString());
                                builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                alertDialog.setCancelable(false);
                                alertDialog.setCanceledOnTouchOutside(false);
                                progressDialog.dismiss();
                            }},
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                Log.v("Error", volleyError.toString());
                                builder = new AlertDialog.Builder(Homepage.this);
                                builder.setTitle("דוח סיכום יום");
                                builder.setMessage("נכשל");
                                builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                progressDialog.dismiss();
                            }
                        }) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        return headers;
                    }
                };
                jobReq.setRetryPolicy(new DefaultRetryPolicy(
                        10000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
                MySingelton.getInstance(Homepage.this).addToRequesetQueue(jobReq);
            } else {
                progressDialog.dismiss();
            }
        }
*/