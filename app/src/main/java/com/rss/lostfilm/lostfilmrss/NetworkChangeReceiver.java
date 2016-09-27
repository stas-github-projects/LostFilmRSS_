package com.rss.lostfilm.lostfilmrss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

/**
 * Created by Stas on 10.02.2015.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if(_check_internet_connect(context))
            {
                Toast.makeText(context, "!!!", Toast.LENGTH_SHORT);
                context.startService(intent);
                int i=0;
            }
        }

    private boolean _check_internet_connect(Context context)
    {
        //Context context=getb();
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //wi-fi
        boolean isConnected;
        try {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = activeNetwork.isConnectedOrConnecting();
        }
        catch (Exception e)
        {isConnected=false;}
        return isConnected;
    }
}
