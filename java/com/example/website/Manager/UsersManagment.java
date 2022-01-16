package com.example.website.Manager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
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

public class UsersManagment extends AppCompatActivity implements OnStartDragListener {

    private UserSession session;
    private UsersRecyclerView rvAdapter;
    private RecyclerView rv;
    private LinearLayout recycle_users_layout;
    private ImageView addUser;
    private static ProgressDialog mProgressDialog;
    private AlertDialog.Builder builder;
    private ArrayList<User> userDataList;
    private TextView manager_users_head;
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
        setContentView(R.layout.users_management);
        session = new UserSession(getApplicationContext());
        setViews();
        setToolbar();
        setItemClickListener();
        getUsers();
        enableSwipeToDeleteAndUndo();


        addUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                if (SystemClock.elapsedRealtime() - mLastClickTime < 1000) {
                    return;
                }
                mLastClickTime = SystemClock.elapsedRealtime();
                Intent intent = new Intent(UsersManagment.this, AddUser.class);
                startActivity(intent);
            }
        });

    }


    public void getUsers(){
        showSimpleProgressDialog(this, "שרת","טוען...",false);
        server_url = url.getServer_url("getUsers");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("Username", session.returnUsername());
            jsonObject.put("Name", session.returnName());
        } catch (JSONException e) {
            Log.e("JSONObject Here", e.toString());
        }
        array.put(jsonObject);
        JsonArrayRequest jsonArrayRequest  = new JsonArrayRequest(Request.Method.POST, server_url,array,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        userDataList = new ArrayList<>();
                        Log.v("Response", response.toString());
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
                                    User user = new User();
                                    user.setUserID(jsonObject.getInt("id"));
                                    user.setUsername(jsonObject.getString("username"));
                                    user.setPassword(jsonObject.getString("password"));
                                    user.setEmail(jsonObject.getString("email"));
                                    user.setPrivateName(jsonObject.getString("name"));
                                    user.setRank(jsonObject.getString("rank"));
                                    userDataList.add(user);
                                }
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        setupListview();
                        rQueue.getCache().clear();
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.v("ERROR", error.toString());
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
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


    private void setupListview() {
        removeSimpleProgressDialog();  //will remove progress dialog
        rvAdapter = new UsersRecyclerView(this,userDataList,this,
                new UsersRecyclerView.OnItemCheckListener() {
                    @Override
                    public void onItemCheck(User user) {
                        select_counter++;
                    }

                    @Override
                    public void onItemUncheck(User user) {
                        //currentSelectedItems.remove(item);
                        select_counter--;
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


    private void enableDragAndDrop(UsersRecyclerView adapter, RecyclerView recycle){
        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(recycle);
    }
    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(this) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
                final int position = viewHolder.getAdapterPosition();
                final User user = rvAdapter.getData().get(position);
                Log.v("item",user.getPrivateName());
                final int pos = position;
                rvAdapter.removeItem(position);
                Snackbar snackbar = Snackbar
                        .make(recycle_users_layout, "נמחק", Snackbar.LENGTH_LONG)
                        .addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
                            public void onDismissed(Snackbar transientBottomBar, int event) {
                                if (event != DISMISS_EVENT_ACTION) {
                                    deleteItem(pos-1);
                                }
                            }
                        });
                snackbar.setAction("בטל", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        rvAdapter.restoreItem(position,user);
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
        final User user = rvAdapter.getData().get(position);
        showSimpleProgressDialog(this, "שרת","רק רגע...",false);
        server_url = url.getServer_url("deleteUser");
        JSONArray array = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("username", user.getUsername());
        }catch (JSONException e) {
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
        manager_users_head = (TextView) findViewById(R.id.manager_users_head);
        rv = (RecyclerView) findViewById(R.id.recyvleUsers);
        recycle_users_layout = (LinearLayout) findViewById(R.id.recycle_users_layout);
        toolbar = (Toolbar) findViewById(R.id.reportlogin_bar);
        addUser = (ImageView) findViewById(R.id.addUser);
    }

    public void setTypeface(){
        Typeface typeface = Typeface.createFromAsset(getAssets(), "font/varela.ttf");
        TextView[] allTxt = {manager_users_head};
        for (TextView textView : allTxt) {
            textView.setTypeface(typeface);
        }
    }

    void setItemClickListener(){
        mItemClickListener = new UsersManagment.OnListItemClickListener() {
            @Override
            public void onListItemClick(int position) {
                mItemClickListener.onListItemClick(position);

            }
        };
    }


    interface OnListItemClickListener {
        void onListItemClick(int position);
    }

    @Override
    public void onResume() {
        getUsers();
        super.onResume();
    }
}
