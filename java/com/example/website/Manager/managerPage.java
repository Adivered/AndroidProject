package com.example.website.Manager;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Background.AppLifecycleObserver;
import com.example.website.LogInfo;
import com.example.website.Permissions.URL;
import com.example.website.Providers.ListAdapter;
import com.example.website.R;
import com.example.website.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class managerPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private TextView Name;
    private EditText Topic,Content,Task_Date;
    private LinearLayout sendMessage,seeWhosLoggedin,addTask,UsersManagment;
    private ListView listView;
    private UserSession session;
    private String server_url, LemiMail,LemiTask;
    private URL url = new URL();
    private static ProgressDialog mProgressDialog;
    private AlertDialog.Builder builder;
    private long mLastClickTime = 0;
    private Spinner ToWhom;
    private DatePickerDialog.OnDateSetListener dateBuilder;
    private static DatePickerDialog dateBuilder2;
    private static AlertDialog dialogBuilder;
    private Boolean donotpass = Boolean.FALSE;
    private Calendar myCalendar = Calendar.getInstance();
    private ArrayList<LogInfo> dataModelArrayList;
    private ListAdapter listAdapter;
    private RequestQueue rQueue;
    private Typeface typeface;
    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manager);
        setViews();
        setToolbar();
        setTypeface();
        session = new UserSession(getApplicationContext());
        if (session.isUserLoggedInKnisa()) {
            Name.setText(Name.getText().toString() + session.returnName(), TextView.BufferType.SPANNABLE);
        }


        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                showMSGDialog();
            }
        });

        seeWhosLoggedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                askForDateLog();
            }
        });

        addTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                addTaskDialog();
            }
        });

        UsersManagment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(managerPage.this, UsersManagment.class);
                startActivity(intent);
            }
        });
    }


    public void askForDateLog(){
            dateBuilder = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                String date = sdf.format(myCalendar.getTime());
                askForLog(date);
            }
        };
        new DatePickerDialog(managerPage.this, dateBuilder, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void askForLog(final String date) {
        showSimpleProgressDialog(this, "שרת","טוען...",false);
        server_url = url.getServer_url("logTable");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonObject = new JSONArray(response.toString());
                                dataModelArrayList = new ArrayList<>();
                                for (int i = 0; i < jsonObject.length(); i++) {
                                    LogInfo person = new LogInfo();
                                    JSONObject dataobj = jsonObject.getJSONObject(i);
                                    person.setName(dataobj.getString("username"));
                                    person.setLoggedIn(dataobj.getString("loggedin"));
                                    person.setLoginHour(dataobj.getString("loginhour"));
                                    person.setLogoutHour(dataobj.getString("logouthour"));
                                    dataModelArrayList.add(person);
                                }
                                setupListview();
                    } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Date", date);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void setupListview(){
        removeSimpleProgressDialog();  //will remove progress dialog
        listAdapter = new ListAdapter(this, dataModelArrayList);
        showLogDialog();
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

    public void showLogDialog(){
        builder = new AlertDialog.Builder(managerPage.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = (View) inflater.inflate(R.layout.custom_dialog, null);
        builder.setView(convertView);
        ListView lv = (ListView) convertView.findViewById(R.id.listViewID);
        listAdapter = new ListAdapter(this, dataModelArrayList);
        lv.setAdapter(listAdapter);
        Button buttonOk = (Button)convertView.findViewById(R.id.buttonOk);
        final AlertDialog ad = builder.show();
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ad.dismiss();
            }
        });
    }

    public void showMSGDialog(){
        builder = new AlertDialog.Builder(managerPage.this);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.sendmsg, null);
        Topic =  (EditText) convertView.findViewById(R.id.inputTopic);
        Topic.setTypeface(typeface);
        Content =  (EditText) convertView.findViewById(R.id.inputContent);
        Content.setTypeface(typeface);
        ToWhom = (Spinner) convertView.findViewById(R.id.inputLemiMail);
        ArrayAdapter<CharSequence> ToWhomAdapter = ArrayAdapter.createFromResource(this,
                R.array.lemiMail, android.R.layout.simple_spinner_item);
        ToWhomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ToWhom.setAdapter(ToWhomAdapter);
        ToWhom.setOnItemSelectedListener(this);
        Topic.setOnClickListener(new View.OnClickListener() { ///### KOTERET ###///
            @Override
            public void onClick(View view) {
                final EditText inputTopic = new EditText(managerPage.this);
                inputTopic.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                Topic.setText(inputTopic.getText().toString());
            }
        });
        Content.setOnClickListener(new View.OnClickListener() { ///### KOTERET ###///
            @Override
            public void onClick(View view) {
                final EditText inputContent = new EditText(managerPage.this);
                inputContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                Content.setText(inputContent.getText().toString());
            }
        });
        builder.setView(convertView);
        Button buttonOk = (Button)convertView.findViewById(R.id.buttonOk);
        final AlertDialog msgAD = builder.show();
        buttonOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEditTexts();
                checkSpinners();
                if (donotpass == Boolean.FALSE) {
                    Log.v("YES","YES");
                    sendMessage(msgAD, convertView, Topic.getText().toString(),Content.getText().toString(),LemiMail);

                }
            }
        });
    }

    public void checkSpinners(){
        Spinner[] allSpinners = {ToWhom};
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
        EditText[] allEts = {Topic, Content};
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

    public void checkTaskEditTexts(EditText Task_Topic,EditText Task_Date, EditText Task_Content){
        EditText[] allEts = {Task_Topic, Task_Date,Task_Content};
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


    public void sendMessage(final AlertDialog msgAD, View convertView , String Topic, String Message, String ToWho){
        final ProgressDialog progressDialog = new ProgressDialog(managerPage.this);
        progressDialog.setMessage("שולח...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        if(convertView.getParent() != null) {
            ((ViewGroup)convertView.getParent()).removeView(convertView); // <- fix
        }
        server_url = url.getServer_url("sendMessage");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("username", session.returnUsername());
                jsonObject.put("name", session.returnName());
                jsonObject.put("Topic", Topic);
                jsonObject.put("Message", Message);
                jsonObject.put("ToWho", ToWho);
            } catch (JSONException e) {
                Log.e("JSONObject Here", e.toString());
            }
            array.put(jsonObject);
            JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                    new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                msgAD.dismiss();
                                JSONArray firstArray = (JSONArray) response;
                                String status = firstArray.getString(0);
                                String resp = firstArray.getString(1);
                                if (status.equals("OK")) {
                                    Toast.makeText(managerPage.this, "נשלח בהצלחה!", Toast.LENGTH_LONG).show();
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

            rQueue = Volley.newRequestQueue(managerPage.this);
            rQueue.add(jsonArrayRequest );

        }

    public void addTaskDialog(){
        builder = new AlertDialog.Builder(managerPage.this);
        LayoutInflater inflater = getLayoutInflater();
        final View convertView = (View) inflater.inflate(R.layout.sendtask, null);
        final EditText Task_Topic =  (EditText) convertView.findViewById(R.id.inputTaskTopic);
        Task_Date =  (EditText) convertView.findViewById(R.id.inputTaskDate);
        final EditText Task_Content =  (EditText) convertView.findViewById(R.id.inputTaskContent);
        final Button Task_Button = (Button)convertView.findViewById(R.id.buttonTaskOk);
        Task_Topic.setTypeface(typeface);
        Task_Date.setTypeface(typeface);
        Task_Content.setTypeface(typeface);
        Task_Button.setTypeface(typeface);
        ToWhom = (Spinner) convertView.findViewById(R.id.inputLemiTask);
        ArrayAdapter<CharSequence> ToWhomAdapter = ArrayAdapter.createFromResource(this,
                R.array.lemiMail, android.R.layout.simple_spinner_item);
        ToWhomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ToWhom.setAdapter(ToWhomAdapter);
        ToWhom.setOnItemSelectedListener(this);
        Task_Topic.setOnClickListener(new View.OnClickListener() { ///### KOTERET ###///
            @Override
            public void onClick(View view) {
                final EditText inputTopic = new EditText(managerPage.this);
                inputTopic.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                Task_Topic.setText(inputTopic.getText().toString());
            }
        });
        Task_Date.setOnClickListener(new View.OnClickListener() { ///### DATE ###///
            @Override
            public void onClick(View view) {
                dateBuilder2 = new DatePickerDialog(managerPage.this,
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
        Task_Content.setOnClickListener(new View.OnClickListener() { ///### KOTERET ###///
            @Override
            public void onClick(View view) {
                final EditText inputContent = new EditText(managerPage.this);
                inputContent.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                Task_Content.setText(inputContent.getText().toString());
            }
        });
        builder.setView(convertView);
        final AlertDialog taskAD = builder.show();
        taskAD.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        Task_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkTaskEditTexts(Task_Topic,Task_Date,Task_Content);
                checkSpinners();
                if (donotpass == Boolean.FALSE) {
                    sendTask(taskAD, convertView,Task_Topic.getText().toString(),Task_Date.getText().toString(), Task_Content.getText().toString());
                }
            }
        });
    }


    public void sendTask(final AlertDialog taskAD, View convertView , String Topic,String Date, String Info){
        final ProgressDialog progressDialog = new ProgressDialog(managerPage.this);
        progressDialog.setMessage("שולח...");
        progressDialog.setCancelable(true);
        progressDialog.show();
        if(convertView.getParent() != null) {
            ((ViewGroup)convertView.getParent()).removeView(convertView); // <- fix
        }
        server_url = url.getServer_url("sendMesima");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", session.returnUsername());
            jsonObject.put("name", session.returnName());
            jsonObject.put("Topic", Topic);
            jsonObject.put("Date", Date);
            jsonObject.put("Info", Info);
            jsonObject.put("ToWho", LemiTask);
            Log.v("ToWho", LemiTask);
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonObject);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url, array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            taskAD.dismiss();
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            String resp = firstArray.getString(1);
                            if (status.equals("OK")) {
                                Toast.makeText(managerPage.this, "נשלח בהצלחה!", Toast.LENGTH_LONG).show();
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

        rQueue = Volley.newRequestQueue(managerPage.this);
        rQueue.add(jsonArrayRequest );

    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        Task_Date.setText(sdf.format(myCalendar.getTime()));
        Task_Date.clearFocus();
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        ((TextView) adapterView.getChildAt(0)).setTypeface(typeface);
        if(pos != 0){
            switch (adapterView.getId()) {
                case R.id.inputLemiMail:
                    LemiMail = String.valueOf(adapterView.getItemAtPosition(pos));
                case R.id.inputLemiTask:
                    LemiTask = String.valueOf(adapterView.getItemAtPosition(pos));
            }
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void hideKeyboard() {
        InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public void setTypeface(){
        typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {Name};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this,cm);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }

    public void setViews(){
        Name = (TextView) findViewById(R.id.loggedName);
        listView = findViewById(R.id.listViewID);
        sendMessage = (LinearLayout) findViewById(R.id.buttonMessage);
        seeWhosLoggedin = (LinearLayout) findViewById(R.id.buttonLogs);
        UsersManagment = (LinearLayout) findViewById(R.id.buttonUsersManagment);
        addTask = (LinearLayout) findViewById(R.id.addTask);
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

    @Override
    public void onBackPressed() {
        finish();
    }

}
