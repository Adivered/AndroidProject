package com.example.website.Tasks;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ProcessLifecycleOwner;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.website.Background.AppLifecycleObserver;
import com.example.website.MotionEvents.OnStartDragListener;
import com.example.website.MotionEvents.SimpleItemTouchHelperCallback;
import com.example.website.MotionEvents.SwipeToDeleteCallback;
import com.example.website.Permissions.URL;
import com.example.website.R;
import com.example.website.UserSession;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Loz extends AppCompatActivity implements OnStartDragListener {


    interface OnListItemClickListener {
        void onListItemClick(int position);
    }


    private UserSession session;
    private RecyvlerViewAdapter rvAdapter;
    private RecyclerView rv;
    private LinearLayout recycleLayout, syamti,syamti_invisible;
    private static ProgressDialog mProgressDialog;
    private AlertDialog.Builder builder;
    private ArrayList<Mesimot> mesimotDataList;
    private TextView Head;
    private ItemTouchHelper mItemTouchHelper;
    private OnListItemClickListener mItemClickListener;
    private long mLastClickTime = 0;
    private Toolbar toolbar;
    private RequestQueue rQueue;
    private int select_counter = 0;
    private URL url = new URL();
    private String server_url;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.yomanmesimot);
        session = new UserSession(getApplicationContext());
        setViews();
        setToolbar();
        setItemClickListener();
        getTasks();
        enableSwipeToDeleteAndUndo();


    syamti.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick (View view){
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();
        setSyamti();
    }
    });

}
    public void getTasks(){
        showSimpleProgressDialog(this, "שרת","טוען...",false);
        server_url = url.getServer_url("getTasks");
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
                        mesimotDataList = new ArrayList<>();
                        try {
                            JSONArray firstArray = (JSONArray) response;
                            String status = firstArray.getString(0);
                            if (status.equals("NONE")) {
                                String resp = firstArray.getString(1);
                                Toast.makeText(getApplicationContext(), resp, Toast.LENGTH_SHORT).show();
                            }
                            else {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    Mesimot mesima = new Mesimot();
                                    mesima.setTaskName(jsonObject.getString("Topic"));
                                    mesima.setTaskInfo(jsonObject.getString("Info"));
                                    mesima.setDate(jsonObject.getString("Date"));
                                    mesima.setStatus("לא בוצע");
                                    mesimotDataList.add(mesima);
                                }
                            }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            setupListview();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ERROR", error.toString());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(jsonArrayRequest);

    }

    private void setupListview() {
        removeSimpleProgressDialog();  //will remove progress dialog
        rvAdapter = new RecyvlerViewAdapter(this,mesimotDataList,this,
                new RecyvlerViewAdapter.OnItemCheckListener() {
            @Override
            public void onItemCheck(Mesimot item) {
                select_counter++;
                if(syamti_invisible.getVisibility() == View.GONE) {
                    syamti_invisible.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onItemUncheck(Mesimot item) {
                //currentSelectedItems.remove(item);
                select_counter--;
                if(syamti_invisible.getVisibility() == View.VISIBLE) {
                    if(select_counter == 0)
                        syamti_invisible.setVisibility(View.GONE);
                }

            }
        });
        //rvAdapter = new RecyvlerViewAdapter(this, mesimotDataList,this);
        rv.setLayoutManager(new GridLayoutManager(this, 1));
        rv.setAdapter(rvAdapter);
        enableDragAndDrop(rvAdapter, rv);
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

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
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

    void setCycle(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        AppLifecycleObserver appLifecycleObserver = new AppLifecycleObserver(this,cm);
        ProcessLifecycleOwner.get().getLifecycle().addObserver(appLifecycleObserver);
    }


    private void enableDragAndDrop(RecyvlerViewAdapter adapter, RecyclerView recycle){
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recycle);
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final Mesimot item = rvAdapter.getData().get(position);
                Log.v("item",item.getDate());
                rvAdapter.removeItem(position);
                Snackbar snackbar = Snackbar
                        .make(recycleLayout, "נמחק", Snackbar.LENGTH_LONG)
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    deleteItem(position);
                                }
                            }
                        });
                snackbar.setAction("בטל", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rvAdapter.restoreItem(position,item);
                        rv.scrollToPosition(position);
                }
                });
                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };
        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(rv);
    }

    public void deleteItem(int position){
        final Mesimot item = rvAdapter.getData().get(position);
        showSimpleProgressDialog(this, "שרת","רק רגע...",false);
        server_url = url.getServer_url("updateTaskVisibility");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
                jsonObject.put("username", session.returnUsername());
                jsonObject.put("task", item.getTaskName());
                jsonObject.put("info", item.getTaskInfo());
                jsonObject.put("date", item.getDate() );
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
                            String status;
                            try {
                                status = firstArray.getString(0);
                                String resp = firstArray.getString(1);
                                if (status.equals("OK")) {
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


        public void setSyamti(){
            RecyclerView rv2 = this.findViewById(R.id.recyvleYoman);
            final int count = rv2.getAdapter().getItemCount();
            new AlertDialog.Builder(this).setMessage("אתה בטוח שסיימת?").setPositiveButton("כן",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            showSimpleProgressDialog(Loz.this, "שרת","רק רגע...",false);
                            for (int i = 0; i < count; i++) {
                                final Mesimot item = rvAdapter.getData().get(i);
                                if (item.isSelected()) {
                                    updateMesimaStatus(item);
                                }
                            }
                        }
                    }).setNegativeButton("לא", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
            }

    public void updateMesimaStatus(Mesimot item){
        server_url = url.getServer_url("updateTaskStatus");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        builder = new AlertDialog.Builder(Loz.this);
        try {
            jsonObject.put("username", session.returnUsername());
            jsonObject.put("task", item.getTaskName());
            jsonObject.put("info", item.getTaskInfo());
            jsonObject.put("date", item.getDate() );
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
                                builder.setTitle("משימה");
                                builder.setMessage(resp);
                                builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        rQueue.getCache().clear();
                                        finish();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                removeSimpleProgressDialog();
                            }
                            else{
                                Log.v("Response", resp);
                                builder.setTitle("משימה");
                                builder.setMessage("נכשל");
                                builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                                rQueue.getCache().clear();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("Response", error.toString());
                error.printStackTrace();
                }
            });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        rQueue = Volley.newRequestQueue(Loz.this);
        rQueue.add(jsonArrayRequest );

    }

    void setToolbar(){
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.back_2_60);
        }
    }

    void setViews(){
        Head = (TextView) findViewById(R.id.mesimaHead);
        Head.setText("משימות", TextView.BufferType.SPANNABLE);
        rv = (RecyclerView) findViewById(R.id.recyvleYoman);
        recycleLayout = (LinearLayout) findViewById(R.id.recycle_tasks_layout);
        syamti = (LinearLayout) findViewById(R.id.yomanmesimot_linear_setMesimotDone);
        syamti_invisible = (LinearLayout) findViewById(R.id.yomanmesimot_linear_invisible);
        toolbar = (Toolbar) findViewById(R.id.reportlogin_bar);
    }

    public void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {Head};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setItemClickListener(){
        mItemClickListener = new OnListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                mItemClickListener.onListItemClick(position);

            }
        };
    }

    }
