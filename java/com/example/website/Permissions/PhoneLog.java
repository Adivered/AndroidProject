package com.example.website.Permissions;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class PhoneLog extends AppCompatActivity {


    Context ctx;
    String username, name,server_url;
    private RequestQueue rQueue;
    private URL url = new URL();

    public PhoneLog(Context ctx, String Username, String Name){
        this.ctx = ctx;
        this.username = Username;
        this.name = Name;
        this.server_url = url.getServer_url("sendLog");
    }


    public JSONArray requestPhoneLog(JSONArray array){
        if (ActivityCompat.checkSelfPermission((Activity)ctx, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
        }
        Cursor managedCursor = ctx.getContentResolver().query(
                CallLog.Calls.CONTENT_URI, null, null,
                null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int contact = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            JSONObject jsonParam = new JSONObject();
            Date callDayTime = new Date(Long.valueOf(managedCursor.getString(date)));
            String dir = null;
            int dircode = Integer.parseInt(managedCursor.getString(type));
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
                jsonParam.put("Contact", managedCursor.getString(contact));
                jsonParam.put("Number", managedCursor.getString(number));
                jsonParam.put("Type", dir);
                jsonParam.put("Date", callDayTime);
                jsonParam.put("Duration", managedCursor.getString(duration));
                jsonParam.put("Username", username);
                jsonParam.put("Userfullname", name);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(jsonParam);
        }
        return array;
    }

    public void sendLog() {
        if (isPermissionGranted()) {
            JSONArray array = new JSONArray();
            requestPhoneLog(array);
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, server_url, array,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("Response", response.toString());
                            try {
                                JSONArray firstArray = (JSONArray) response;
                                String status = firstArray.getString(0);
                                if (status.equals("SUCCESS")) { // SENT
                                    Log.v("LOG", "SENT");
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Response", error.toString());

                }
            });
            jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                    10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            rQueue = Volley.newRequestQueue(ctx);
            rQueue.add(jsonArrayRequest);

        }
    }


    public  boolean isPermissionGranted() {

        if (ActivityCompat.checkSelfPermission((Activity)ctx, Manifest.permission.READ_CALL_LOG)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        else{
            ActivityCompat.requestPermissions((Activity) ctx, new String[]{Manifest.permission.READ_CALL_LOG}, 1);
            return false;
        }
    }

}
