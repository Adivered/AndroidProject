package com.example.website.Providers;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

public class VPN extends Activity {

    Context mContext;

    public VPN(Context context){
        mContext = context;
    }

    public String checkVPN(Context ctx, ConnectivityManager connectivityManager) {
        String result = "None";
        if (connectivityManager != null) {
                Network network = connectivityManager.getActiveNetwork();
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
                if (capabilities == null) {
                    result = "None";
                }
                if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                    result = "WIFI";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                    result = "MOBILE";
                } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                    result = "VPN";
                }
            }
        Log.v("checkVPN result: ", result);
        return result;
    }

   /* public void dialogVPN() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("חיבור לרשת");
        builder.setMessage("נראה שאתה לא מחובר לרשת, נא לאפשר VPN");
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.net.vpn.SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //startActivityForResult(intent, 10);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("התעלם", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
            }
        });
        builder.show();
    }

    public void dialogVPNOFF() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("חיבור לרשת");
        builder.setMessage("כיבוי VPN");
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent("android.net.vpn.SETTINGS");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
              //  startActivityForResult(intent, 10);
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("התעלם", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                dialog.cancel();
              //  endSplashToLogin();
            }
        });
        builder.show();
    }*/


}
