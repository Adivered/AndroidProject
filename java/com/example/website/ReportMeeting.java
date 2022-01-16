package com.example.website;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Background.AppLifecycleObserver;
import com.example.website.Permissions.URL;
import com.example.website.Providers.OfflineGPS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportMeeting extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    TextView Head,txtSend,txtSogEsek, txtAnaf, txtTafkid, txtTozaa,txtNEsek,txtLoc,txtDate,txtEishKesher;
    UserSession session;
    String lon2, lat2, spinEsek, spinAnaf, spinTafkid, spinTozaa,server_url;
    EditText Esek, Location, Time, EishKesher;
    Spinner sogEsek, sogAnaf, sogTafkid, sogTozaa;
    LinearLayout SendReport;
    OfflineGPS gps;
    Toolbar toolbar;
    Boolean donotpass = Boolean.FALSE;
    private long mLastClickTime = 0;
    AlertDialog.Builder builder;
    static AlertDialog dialogBuilder;
    static DatePickerDialog dateBuilder2;
    final Calendar myCalendar = Calendar.getInstance();
    RequestQueue rQueue;
    URL url = new URL();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.reportpgisha);
        //setCycle();
        setViews();
        setSpinners();
        setToolbar();
        setTypeface();
        //////////// ################ \\\\\\\\\\\\\\
        session = new UserSession(getApplicationContext());
        updateLabel();
        clearFocus();
        Esek.requestFocus();


        Esek.setOnClickListener(new View.OnClickListener() { ///### KOTERET ###///
            @Override
            public void onClick(View view) {
                final EditText inputTitle = new EditText(ReportMeeting.this);
                inputTitle.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                Esek.setText(inputTitle.getText().toString());
            }
        });

        Time.setOnClickListener(new View.OnClickListener() { ///### DATE ###///
            @Override
            public void onClick(View view) {
                dateBuilder2 = new DatePickerDialog(ReportMeeting.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month,
                                                  int day) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, month);
                                myCalendar.set(Calendar.DAY_OF_MONTH, day);
                                updateLabel();
                            }
                        },
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                dateBuilder2.show();
            }
        });

        EishKesher.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_NEXT) {
                    hideKeyboard();
                    textView.clearFocus();
                    sogEsek.requestFocus();
                    sogEsek.performClick();
                }
                return true;
            }
        });

        Location.setOnClickListener(new View.OnClickListener() { ///### LOCATION ###///
            @Override
            public void onClick(View view) {
                final EditText inputLocation = new EditText(ReportMeeting.this);
                inputLocation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                Location.setText(inputLocation.getText().toString());
            }

        });


        EishKesher.setOnClickListener(new View.OnClickListener() { ///### CONTENT ###///
            @Override
            public void onClick(View view) {
                final EditText inputContent = new EditText(ReportMeeting.this);
                inputContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                EishKesher.setText(inputContent.getText().toString());
            }

        });


        SendReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                checkEditTexts();
                checkSpinners();
                if (isPermissionGranted()) {
                    checkEditTexts();
                    checkSpinners();
                    if (donotpass == Boolean.FALSE) {
                        sendReport();
                    }
                }
            }
        });
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Time.setText(sdf.format(myCalendar.getTime()));
        Time.clearFocus();
    }

    public static void removeSimpleProgressDialog() {
        try {
            if (dateBuilder2 != null) {
                if (dateBuilder2.isShowing()) {
                    dateBuilder2.dismiss();
                    dateBuilder2 = null;
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

    private void clearFocus() {
        Time.clearFocus();
        Esek.clearFocus();
        Location.clearFocus();
        EishKesher.clearFocus();
        Time.setFocusable(true);
        Esek.setFocusableInTouchMode(true);
        Location.setFocusableInTouchMode(true);
        EishKesher.setFocusableInTouchMode(true);
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        hideKeyboard();
        ((TextView) adapterView.getChildAt(0)).setTypeface(typeface);
        //((TextView) adapterView.getChildAt(0)).setTextSize(12);
        if(pos != 0){
            switch (adapterView.getId()) {
                case R.id.inputSogEsek:
                    spinEsek = String.valueOf(adapterView.getItemAtPosition(pos));
                case R.id.inputSogAnaf:
                        spinAnaf = String.valueOf(adapterView.getItemAtPosition(pos));
                case R.id.inputSogEishTafkid:
                        spinTafkid = String.valueOf(adapterView.getItemAtPosition(pos));
                case R.id.inputResults:
                        spinTozaa = String.valueOf(adapterView.getItemAtPosition(pos));
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void showFillDialog() {
        if (dialogBuilder != null && dialogBuilder.isShowing()) {
            return;
        }else{
            builder = new AlertDialog.Builder(this)
                    .setTitle("שגיאה")
                    .setMessage("נא למלא את כל השדות");
            dialogBuilder = builder.create();
            dialogBuilder.show();

        }

    }

    public void checkSpinners(){
        Spinner[] allSpinners = {sogEsek, sogAnaf, sogTafkid, sogTozaa};
        for (Spinner spinner : allSpinners) {
            int position = spinner.getSelectedItemPosition();
            if (position == 0) {
                showFillDialog();
                spinner.requestFocus();
                donotpass = Boolean.TRUE;
                break;
            }
        }
    }

    public void checkEditTexts(){
        EditText[] allEts = {Esek, Time, Location, EishKesher};
        for (EditText editText : allEts) {
            String text = editText.getText().toString();
            if (text.length() == 0) {
                showFillDialog();
                editText.requestFocus();
                donotpass = true;
                break;
            }
            else
            {
                donotpass = false;
            }
        }
    }

    public  boolean isPermissionGranted() {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return true;
        }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            } else {
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            }
        }
    }

    public void setSpinners(){
        //////////// ESEK \\\\\\\\\\\\\\
        sogEsek = (Spinner) findViewById(R.id.inputSogEsek); //SOG ESEK
        ArrayAdapter<CharSequence> esekAdapter = ArrayAdapter.createFromResource(this,
                R.array.sogEsek, R.layout.spinner_custom_dropdown);
        esekAdapter.setDropDownViewResource(R.layout.spinner_custom_dropdown);
        sogEsek.setAdapter(esekAdapter);
        sogEsek.setOnItemSelectedListener(this);
        //////////// Anaf \\\\\\\\\\\\\\
        sogAnaf = (Spinner) findViewById(R.id.inputSogAnaf); //SOG ANAF
        ArrayAdapter<CharSequence> anafAdapter = ArrayAdapter.createFromResource(this,
                R.array.sogAnaf, R.layout.spinner_custom_dropdown);
        anafAdapter.setDropDownViewResource(R.layout.spinner_custom_dropdown);
        sogAnaf.setAdapter(anafAdapter);
        sogAnaf.setOnItemSelectedListener(this);
        //////////// Tafkid \\\\\\\\\\\\\\
        sogTafkid = (Spinner) findViewById(R.id.inputSogEishTafkid); //SOG TAFKID EISH KESHER
        ArrayAdapter<CharSequence> tafkidAdapter = ArrayAdapter.createFromResource(this,
                R.array.sogTafkid, R.layout.spinner_custom_dropdown);
        tafkidAdapter.setDropDownViewResource(R.layout.spinner_custom_dropdown);
        sogTafkid.setAdapter(tafkidAdapter);
        sogTafkid.setOnItemSelectedListener(this);
        //////////// Tozaa \\\\\\\\\\\\\\
        sogTozaa = (Spinner) findViewById(R.id.inputResults); //SOG TOZAA
        ArrayAdapter<CharSequence> tozaaAdapter = ArrayAdapter.createFromResource(this,
                R.array.sogResult, R.layout.spinner_custom_dropdown);
        tozaaAdapter.setDropDownViewResource(R.layout.spinner_custom_dropdown);
        sogTozaa.setAdapter(tozaaAdapter);
        sogTozaa.setOnItemSelectedListener(this);
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

    public void setViews(){
        Head =(TextView)findViewById(R.id.pgishaHead);
        Head.setText("דווח פגישה", TextView.BufferType.SPANNABLE);
        SendReport = (LinearLayout) findViewById(R.id.SendReport);
        txtSend = (TextView)findViewById(R.id.txtSend);
        Esek = (EditText) findViewById(R.id.inputNEsek); // shem esek
        txtNEsek = (TextView)findViewById(R.id.txtNEsek);
        Location = (EditText) findViewById(R.id.inputLoc); // ktovet
        txtLoc = (TextView)findViewById(R.id.txtLoc);
        Time = (EditText) findViewById(R.id.inputDate); // DATE
        txtDate = (TextView)findViewById(R.id.txtDate);
        EishKesher = (EditText) findViewById(R.id.inputSogEishKesher); // SHEM EISH KESHER
        txtEishKesher = (TextView)findViewById(R.id.txtSogEishKesher);
        txtAnaf = (TextView)findViewById(R.id.txtSogAnaf);
        txtTozaa = (TextView)findViewById(R.id.txtResults);
        txtTafkid = (TextView)findViewById(R.id.txtSogEishTafkid);
        txtSogEsek = (TextView)findViewById(R.id.txtSogEsek);
    }


    public void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        EditText[] allEts = {Esek, Time, Location, EishKesher};
        TextView[] allTxt = {Head, txtSend, txtSogEsek, txtAnaf, txtTafkid, txtTozaa,txtNEsek,txtLoc,txtDate,txtEishKesher};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
        for (EditText editText : allEts) {
            editText.setTypeface(typeface);
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this).setMessage("אתה בטוח שסיימת?").setPositiveButton("כן",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).create().show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                return false;
            }
            mLastClickTime = SystemClock.elapsedRealtime();
            Intent intent = new Intent(getApplicationContext(), Homepage.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this,cm);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard(){
        InputMethodManager inputManager = (InputMethodManager)   getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public void sendReport(){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("אנא המתן...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        server_url = url.getServer_url("updatePgisha");
        JSONArray array = new JSONArray();
        JSONObject jsonPgishaObject = new JSONObject();
        gps = new OfflineGPS(ReportMeeting.this);
        lat2 = String.valueOf(gps.getLatitude());
        lon2 = String.valueOf(gps.getLongitude());
        try {
            jsonPgishaObject.put("Username", session.returnUsername());
            jsonPgishaObject.put("Name", session.returnName());
            jsonPgishaObject.put("Esek", Esek.getText().toString());
            jsonPgishaObject.put("Time", Time.getText().toString());
            jsonPgishaObject.put("Location", Location.getText().toString());
            jsonPgishaObject.put("EishKesher", EishKesher.getText().toString());
            jsonPgishaObject.put("spinEsek", spinEsek);
            jsonPgishaObject.put("spinAnaf", spinAnaf);
            jsonPgishaObject.put("spinTafkid", spinTafkid);
            jsonPgishaObject.put("sogTozaa", spinTozaa);
            jsonPgishaObject.put("LongCor", lon2);
            jsonPgishaObject.put("LatCor", lat2);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonPgishaObject);
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
                                progressDialog.dismiss();
                                new AlertDialog.Builder(ReportMeeting.this).setMessage(resp).setPositiveButton("אישור",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                                startActivity(intent);
                                                progressDialog.dismiss();
                                                rQueue.getCache().clear();
                                                finish();
                                            }
                                        }).create().show();
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
        rQueue = Volley.newRequestQueue(ReportMeeting.this);
        rQueue.add(jsonArrayRequest );

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
}
