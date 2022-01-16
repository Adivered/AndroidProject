package com.example.website.Background;

import android.content.Context;
import android.net.ConnectivityManager;
import android.util.Log;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;
import androidx.multidex.MultiDexApplication;

import com.example.website.Notifications.Notifications;
import com.example.website.Providers.VPN;

public class AppLifecycleObserver extends MultiDexApplication implements LifecycleObserver {

    public static final String TAG = AppLifecycleObserver.class.getName();
    Notifications notif = new Notifications();
    Context mContext;
    ConnectivityManager mCm;

    public AppLifecycleObserver(Context context, ConnectivityManager cm) {
        mContext = context;
        mCm = cm;

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    public void onEnterForeground() {
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    public void onEnterBackground() {
        Log.v(TAG, "BACKGROUND");
        VPN vpn = new VPN(mContext);
        String result = vpn.checkVPN(mContext, mCm);
        if (result == "VPN") {
            notif.createVPNNotification(mContext, "VPN", "לא לשכוח לכבות את הVPN");
        }


    }
}