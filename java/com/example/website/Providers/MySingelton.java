package com.example.website.Providers;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.example.website.Camera.LruBitmapCache;

public class MySingelton {


    private static MySingelton mInstance;
    private RequestQueue requestQueue;
    private static Context mCtx;
    private ImageLoader mImageLoader;



    private MySingelton(Context context){
        mCtx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized MySingelton getInstance(Context context){
        if(mInstance == null){
            mInstance = new MySingelton(context);
        }
        return mInstance;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if (mImageLoader == null) {
            mImageLoader = new ImageLoader(this.requestQueue,
                    new LruBitmapCache());
        }
        return this.mImageLoader;
    }


    public RequestQueue getRequestQueue(){
        if(requestQueue == null){
            requestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());

        }
        return requestQueue;
    }

    public <T>void addToRequesetQueue(Request<T> request){
        requestQueue.add(request);
    }
}
