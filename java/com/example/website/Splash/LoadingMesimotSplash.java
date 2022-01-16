package com.example.website.Splash;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Emails.EmailsAdapter;
import com.example.website.Permissions.URL;
import com.example.website.R;
import com.example.website.Tasks.Loz;
import com.example.website.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadingMesimotSplash extends Activity {
    private static int SPLASH_TIME_OUT = 3000;
    private TextView msg;
    private UserSession session;
    private String server_url;
    private Intent i;
    private URL url = new URL();
    private ArrayList<EmailsAdapter> emailsAdapters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loadingpage);
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        msg = (TextView) findViewById(R.id.fullscreen_content);
        msg.setTypeface(typeface);
        session = new UserSession(getApplicationContext());
        if (session.isUserLoggedIn()) {
            msg.setText("שלום, " + session.returnName() + "\n" + "אנא המתן...");
        } else {
            msg.setText("שלום!" + "\n" + "אנא המתן...");
        }
        new JsonTask().execute();
    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected String doInBackground(String... params) {
            getEmails();
            return "Done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            runOnUiThread(new Runnable() {
                public void run() {
                    startActivity(i);
                    finish();
                }
            });
        }

        public void getEmails() {
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
            JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.POST, server_url, array,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            Log.v("Response", response.toString());
                            emailsAdapters = new ArrayList<>();
                            try {
                                JSONArray firstArray = (JSONArray) response;
                                String status = firstArray.getString(0);
                                if (status.equals("NONE")) {
                                    String resp = firstArray.getString(1);
                                    Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                                } else {
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
                                i = new Intent(LoadingMesimotSplash.this, Loz.class);
                                i.putExtra("Emails", emailsAdapters);

                                } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("Response", error.toString());
                    Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });

            RequestQueue requestQueue = Volley.newRequestQueue(LoadingMesimotSplash.this);
            requestQueue.add(jsonArrayRequest);

        }
    }
}