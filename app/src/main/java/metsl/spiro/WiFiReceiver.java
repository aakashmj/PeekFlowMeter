package metsl.spiro;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.widget.Toast;

/**
 * Created by Aakash on 26-05-2017.
 */
public class WiFiReceiver  extends BroadcastReceiver {

    WifiManager wifiManager;
    @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = conMan.getActiveNetworkInfo();

            if (netInfo != null && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                WifiInfo info = wifiManager.getConnectionInfo();
                String ssid = info.getSSID();
                Toast.makeText(context, "Wifi Connected To" + ssid, Toast.LENGTH_SHORT).show();
            } else{
            Toast.makeText(context, "Wifi Not Connected!", Toast.LENGTH_SHORT).show();
        }}
    }

