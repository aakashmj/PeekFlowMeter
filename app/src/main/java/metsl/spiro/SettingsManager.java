package metsl.spiro;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by aakash on 8/17/15.
 */
public class SettingsManager {
    public String device_type, serial_port,gainvalue,ip,machine_type, vid, pid,time_intervals, live_View_type,print_type,longlead_type , ssid,analysis,offgrid;

    public int  port, first_lead, second_lead,connectiontype ;

    private String[] leads = {"I", "II", "III", "aVR", "aVL", "aVF", "V1","V2","V3","V4","V5","V6"};

    private SharedPreferences _sharedPreferences;
    private OnSharedPreferenceChangeListener _changeListener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(
                SharedPreferences sharedPreferences, String key) {
            importPreferences();
            OnPreferencesUpdated();
        }
    };


    public SettingsManager(Context context) {
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);// _context.getSharedPreferences("REMClient", 0);
        _sharedPreferences
                .registerOnSharedPreferenceChangeListener(_changeListener);

        importPreferences();
    }

    public void Destroy() {
        _sharedPreferences
                .unregisterOnSharedPreferenceChangeListener(_changeListener);
    }
    public void OnPreferencesUpdated() {
    }

    /**
     * imports the preferences
     */
    private void importPreferences() {
        device_type =  _sharedPreferences.getString("start_devicetype","eUno-WiFi");
        analysis =  _sharedPreferences.getString("start_analysis","OFF");

        offgrid =   _sharedPreferences.getString("offgrid_key","OFF");

        time_intervals = _sharedPreferences.getString("sync_frequency","10");

        gainvalue = _sharedPreferences.getString("gain_value","1");

        print_type = _sharedPreferences.getString("print_type","3 x 4");

        live_View_type = _sharedPreferences.getString("live_view_type", "6 x 2");

        longlead_type = _sharedPreferences.getString("longLead_value", "II");

        serial_port = _sharedPreferences.getString("serial_port", "wifi");

       machine_type = _sharedPreferences.getString("machine_type", "ELR");

        ip = _sharedPreferences.getString("IP", "192.45.1.0");

        port = Integer.valueOf(_sharedPreferences.getString("Port","9801"));

        first_lead = Arrays.asList(leads).indexOf(_sharedPreferences.getString("first_lead","I"));

        second_lead = Arrays.asList(leads).indexOf(_sharedPreferences.getString("second_lead","II"));

        vid = _sharedPreferences.getString("VID", "I");

        pid = _sharedPreferences.getString("PID", "II");

        ssid = _sharedPreferences.getString("SSID" ,"METSL-P2P");

        Log.d("time_intervals",""+time_intervals);
        Log.d("LongLead",""+longlead_type);
        Log.d("print_type",""+print_type);
        Log.d("live_View_type",""+live_View_type);
        Log.d("serial_port",""+serial_port);
        Log.d("device_type",""+device_type);
        Log.d("machine_type",""+machine_type);
        Log.d("ip",""+ip);
        Log.d("port",""+port);
        Log.d("first_lead",""+first_lead);
        Log.d("second_lead",""+second_lead);
        Log.d("vid ",""+vid);
        Log.d("pid ",""+pid);
        Log.d("gain ",""+gainvalue);

       Log.d("ssid ",""+ssid);
        Log.d("analysis ",""+analysis);

        Log.d("offgrid ",""+offgrid);


    }
}
