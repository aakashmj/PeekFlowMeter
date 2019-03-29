package metsl.spiro;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.util.ArrayList;

import me.drakeet.materialdialog.MaterialDialog;
import metsl.spiro.usbcomm.UsbVitalTrackCallback;
import metsl.spiro.usbcomm.VitalTrackUSBCommunication;

public class MainActivity extends AppCompatActivity implements UsbVitalTrackCallback {
    ArrayList<Devices> devicelist = new ArrayList<Devices>();
    final ArrayList<String> devicelist_device = new ArrayList<String>();
    int patient_id;
    static String DevID;

    protected static final String TAG = null;
    final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    TextView mtvDeviceID, mtvHeartrate;//, mtvSPO2, mtvBP, mtvFEV, mtvFVC,
    //mtvPERF, mtvRatio;
    TextView mtvMachineStatus;
    Button mbtConnect, mbtSave, mbtSaveAll, mbtNibp;
    //VitalTrackBluetoothCommunication vBluetoothCommunication;
    public static VitalTrackUSBCommunication vitalTrackUSBCommunication;
    public static UsbManager mUsbManager;

    Mode _Mode;
    UsbECGView _MonitorView;
    LinearLayout LiveGraph;
    String patientID = "";
    private static int _FinalHR, _FinalSPO2Val, _FinalSystolic,
            _FinalDiastolic;
    private static float _FinalFEV, _FinalFVC, _FinalPEFR, _FinalRatio;
    private int _ECGData[][];
    private boolean _ECGSaveDataFlag = false;
    private int _ECGCounter = 0;
    FileIO fileop;
    Record record1;
    boolean isConnected = false;
    String machinestatus = "Machine Status : ---";
    String currentmode = "Mode : ---";
    ConnectionType connectionType = ConnectionType.USB;
    Context ctx;
    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = getApplicationContext();
        mtvMachineStatus = findViewById(R.id.tv_top_overlay);
        SetMessage();
        //mHandler = new MyHandler(this);
        try {
            if (getIntent().getStringExtra("patientid") != null)
                patientID = getIntent().getStringExtra("patientid");
        } catch (Exception ignored) {

        }

        // mtvMachineStatus.setEllipsize(TruncateAt.MARQUEE);
        //mSpirospinner = (Spinner) findViewById(R.id.spinner_Spiro);
        //mModespinner = (Spinner) findViewById(R.id.spinner_Mode);
        mbtNibp = findViewById(R.id.button_NIBP);
        mbtConnect = findViewById(R.id.button_Connect);
        mbtSave = findViewById(R.id.button_Save);
        mbtSaveAll = findViewById(R.id.button_SaveAll);
        mtvHeartrate = findViewById(R.id.tv_HeartRateResponse);
        _ECGData = new int[5000][8];
        _Mode = Mode.ECG;
        initView();
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        PendingIntent mPermissionIntent = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
        IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver, filter);
        patient_id = getIntent().getExtras().getInt("pID");
        PatientDetailsAdapter patientDetailsAdapter = new PatientDetailsAdapter(getBaseContext());
        patientDetailsAdapter.open();
        PatientDetailsCL patientDetailsCLs = patientDetailsAdapter.GetDetailsbyPatientDetailsID(patient_id);
        patientDetailsAdapter.close();
        DevicesDbAdapter deviceAdapter = new DevicesDbAdapter(getApplicationContext());
        deviceAdapter.open();
        devicelist = deviceAdapter.GetAllDevices();
        for (int i = 0; i < devicelist.size(); i++) {
            Devices dev = new Devices();
            dev = devicelist.get(i);
            dev.DEVICEID.toLowerCase();
            devicelist_device.add(dev.DEVICEID.toLowerCase());
        }

        deviceAdapter.close();
        vitalTrackUSBCommunication = new VitalTrackUSBCommunication();
        VitalVariables.read_ptr = 0;
        VitalVariables.write_ptr = 0;
        VitalVariables.diff_pointer = 0;
        VitalVariables._ContiECGData = new int[30000][13];

        mbtNibp.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (connectionType) {
                    case BLUETOOTH:
                        //		vBluetoothCommunication.StartNIBP();
                        break;
                    case USB:
                        vitalTrackUSBCommunication.StartNIBP();
                        break;
                    default:
                        break;
                }

                if (mbtNibp.getText().toString().contains("Start")) {
                    mbtSave.setEnabled(true);
                    mbtNibp.setText("Stop NIBP");
                } else {
                    mbtNibp.setText("Start NIBP");
                }
            }
        });
        mbtConnect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mbtConnect.getTag().equals("connect")) {
                    showConnectionDialog();
                } else {
                    switch (connectionType) {
                        case BLUETOOTH:
                            //vBluetoothCommunication.StopConnection();
                            break;
                        case USB:
                            vitalTrackUSBCommunication.stopIoManager();
                            break;
                        default:
                            break;
                    }

                    mbtConnect.setBackgroundResource(R.drawable.button_connect);
                    mbtConnect.setTag("connect");
                    mbtNibp.setEnabled(false);
                    mbtSaveAll.setEnabled(false);
                    mbtSave.setEnabled(false);
                }
            }
        });

        mbtSave.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (_Mode) {
                    case SPIRO:
                        _FinalFVC = VitalVariables._FVC;
                        _FinalFEV = VitalVariables._FEV1;
                        _FinalPEFR = VitalVariables._PEFR;
                        _FinalRatio = VitalVariables._Ratio;
                        mbtSaveAll.setEnabled(true);
                        break;
                    case ECG:
                        _FinalHR = VitalVariables.HeartRate;
                        //sampling rate is 240 so [2400]
                        _ECGData = new int[5000][8];
                        _ECGCounter = 0;
                        _ECGSaveDataFlag = true;
                        _MonitorView.SetisSaving(true);
                        mtvHeartrate.setText(String.valueOf(_FinalHR));
                        //	mModespinner.setEnabled(false);
                        mbtSave.setEnabled(false);
                        mbtSaveAll.setEnabled(false);
                        break;
                    default:
                        break;
                }

            }
        });
        mbtSaveAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                createFile();
            }
        });

        mbtNibp.setEnabled(false);
        mbtSaveAll.setEnabled(false);
        mbtSave.setEnabled(false);


    }

    public void showConnectionDialog() {
        LayoutInflater inflater = this.getLayoutInflater();
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);
        alertDialogBuilder.setCancelable(true);
        final View view = inflater.inflate(R.layout.chooseconnection_dialog,
                null);
        TextView _title = view.findViewById(R.id.tv_title);
        TextView MSG = view.findViewById(R.id.tv_alertMsg);
        _title.setText("Choose Connection");
        MSG.setText("Connect With");
        Button bToothButton = view.findViewById(R.id.bt_Bluetooth);
        Button usbButton = view.findViewById(R.id.bt_USB);
        alertDialogBuilder.setView(view);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        bToothButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.cancel();
				/*connectionType = ConnectionType.BLUETOOTH;
				Intent intent = new Intent(MainActivity.this, BTPair.class);
				startActivityForResult(intent, 202);*/

            }
        });

        usbButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                connectionType = ConnectionType.USB;
                vitalTrackUSBCommunication.startIoManager(ctx, mUsbManager,
                        MainActivity.this);
            }
        });

    }

    public void initView() {
        LiveGraph = findViewById(R.id.LiveGraph);
        _MonitorView = new UsbECGView(getBaseContext(), this);
        LiveGraph.addView(_MonitorView);
        _MonitorView.setmode(_Mode);
        _MonitorView.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (_MonitorView.gain_val < 3)
                    _MonitorView.gain_val++;
                else
                    _MonitorView.gain_val = 0;
                switch (_MonitorView.gain_val) {
                    case 0:
                        Toast.makeText(getBaseContext(), "Gain is : 0.5", Toast.LENGTH_LONG).show();
                        break;
                    case 1:
                        Toast.makeText(getBaseContext(), "Gain is : 1", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getBaseContext(), "Gain is : 1.5", Toast.LENGTH_LONG).show();
                        break;
                    default:
                        Toast.makeText(getBaseContext(), "Gain is : 2", Toast.LENGTH_LONG).show();
                        break;
                }
                return false;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
		/*	vBluetoothCommunication = new VitalTrackBluetoothCommunication();
			BluetoothDevice device = VitalVariables.mBluetoothAdapter
					.getRemoteDevice(data.getStringExtra("selectedDeviceName"));
			vBluetoothCommunication.StartConnection(this, device);*/
        }
    }

    public void createFile() {
        record1 = new Record();
        record1.DEVICE_RECORDED_DATETIME = new Date();
        List<Byte> temparr = new ArrayList<>();

        for (int i = 0; i < 5000; i++) {
            temparr.add((byte) 0xF1);
            temparr.add((byte) 18);
            for (int j = 0; j < 8; j++) {
                int tmp_val = _ECGData[i][j] + 3000;
                int ecglsb = (tmp_val & 0x007f);
                int ecgmsb = (tmp_val >> 7) & 0x007f;

                temparr.add((byte) ecgmsb);
                temparr.add((byte) ecglsb);
            }
            //	temparr.add((byte) 0);
            //	temparr.add((byte) 0);
        }

        byte[] temBs = new byte[temparr.size()];
        int cnt = 0;
        for (Byte byte1 : temparr) {
            temBs[cnt] = byte1;
            cnt++;
        }

        FileIO fileIO = new FileIO();

        fileIO.SetFilename(Environment
                .getExternalStorageDirectory()
                + File.separator
                + "eUNO_WiFi"
                + File.separator + DateFormat.format("yyyyMMddhhmmss", record1.DEVICE_RECORDED_DATETIME) + ".bin");
        fileIO.OpenFile();

        fileIO.writeToFile(temBs);
        fileIO.CloseFile();

        assert fileIO != null;
        Toast.makeText(getApplicationContext(), "Data Saved successfully"+fileIO.GetFilename().toString(),
                Toast.LENGTH_SHORT).show();
        assert fileIO != null;
        PatientDetailsAdapter patientDetailsAdapter = new PatientDetailsAdapter(getBaseContext());
        patientDetailsAdapter.open();
        PatientDetailsCL patientDetailsCLs = patientDetailsAdapter.GetDetailsbyPatientDetailsID(patient_id);
        patientDetailsAdapter.close();
        record1.setPatientDetails(patientDetailsCLs);
        record1.ECGFILE = fileIO.GetFilename();
        record1.ANALYSIS = "";
        record1.AUTO_INTERPRETATION = "";
        record1.BPDIA = 0;
        record1.BPMEAN = 0;
        record1.BPSYS = 0;
        record1.BPDIA = 0;
        record1.COMMENTS = "";
        record1.DEVICE_TYPE = 3;
        record1.COMMENTS = "NA";
        record1.DEVICE_RECORDED_DATETIME = new Date();
        if (DevID != null) {
            record1.DEVICEID = DevID;
        } else {
            record1.DEVICEID = "";
        }
        record1.ECGFILE = fileIO.GetFilename();
        record1.PULSERATE = UsbVitalVariables.HeartRate;

        RecordDbAdapter recordAdapter = new RecordDbAdapter(getBaseContext());
        recordAdapter.open();


        boolean insert_status = recordAdapter.InsertRecord(record1);

        recordAdapter.close();

        boolean net_status = checkInternetconn();
        if(net_status) {
//Uploader service call here
            finish();
        }else {
            finish();
        }
    }





    @SuppressLint("NewApi")
    private void hideSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        // remove the following flag for version < API 19
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    @Override
    public void onConnectComplete(String devicename) {
        isConnected = true;
        mtvMachineStatus.setSelected(true);
        mbtConnect.setBackgroundResource(R.drawable.button_stop);
        mbtConnect.setTag("stop");
        //	mModespinner.setEnabled(true);
        if (_Mode != Mode.IDLE)
            mbtSave.setEnabled(true);
        mbtSaveAll.setEnabled(false);
        mbtNibp.setEnabled(true);
        Toast.makeText(getApplicationContext(), "device name" + devicename, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onConnectionFailed() {
        isConnected = false;
        mbtConnect.setBackgroundResource(R.drawable.button_connect);
	/*	mModespinner.setEnabled(false);
		mSpirospinner.setEnabled(false);
	*/	mbtNibp.setEnabled(false);
        mbtSaveAll.setEnabled(false);
        mbtSave.setEnabled(false);

        mbtConnect.setTag("connect");
    }

    @Override
    public void onDeviceID(String device_ID) {

    }

    @Override
    public void onLiveECG(ArrayList<ArrayList<Integer>> LiveECG) {
        for (int i = 0; i < LiveECG.size(); i++) {
            int[] ecgdata = ArrayUtils.toPrimitive(LiveECG.get(i).toArray(
                    new Integer[LiveECG.get(i).size()]));
            VitalVariables._ContiECGData[VitalVariables.write_ptr] = ecgdata;
            if (_ECGSaveDataFlag) {
                if (_ECGCounter < 2400) {
                    _ECGData[_ECGCounter][0] = ecgdata[1];
                    _ECGData[_ECGCounter][1] = ecgdata[2];
                    _ECGData[_ECGCounter][2] = ecgdata[6];
                    _ECGData[_ECGCounter][3] = ecgdata[7];
                    _ECGData[_ECGCounter][4] = ecgdata[8];
                    _ECGData[_ECGCounter][5] = ecgdata[9];
                    _ECGData[_ECGCounter][6] = ecgdata[10];
                    _ECGData[_ECGCounter][7] = ecgdata[11];
                    _ECGCounter++;

                } else {
                    _ECGCounter = 0;
                    _ECGSaveDataFlag = false;
                    _MonitorView.SetisSaving(false);
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            mbtSaveAll.setEnabled(true);
                            //mModespinner.setEnabled(true);
                            mbtSave.setEnabled(true);
                        }
                    });

                }
            }
            VitalVariables.write_ptr++;
            VitalVariables.diff_pointer++;
            if (VitalVariables.write_ptr >= VitalVariables._ContiECGData.length) {
                VitalVariables.write_ptr = 0;
            }
        }
    }

    @Override
    public void onHeartRate(final int HeartRate) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mtvHeartrate.setText(String.valueOf(HeartRate));
                VitalVariables.HeartRate = HeartRate;
            }
        });
    }

    @Override
    public void onSPO2(int SPO2val) {
        // mtvSPO2.setText(String.valueOf(SPO2val));
        VitalVariables.SPO2val = SPO2val;
    }

    @Override
    public void onLiveSPO2(ArrayList<Byte> SPO2Wave) {
        for (int i = 0; i < SPO2Wave.size(); i++) {

            VitalVariables._ContiSPoData[VitalVariables.spowrite_ptr] = SPO2Wave
                    .get(i);
            VitalVariables.spowrite_ptr++;
            VitalVariables.spodiff_pointer++;
            if (VitalVariables.spowrite_ptr >= VitalVariables._ContiSPoData.length) {
                VitalVariables.spowrite_ptr = 0;
            }
        }
    }

    @Override
    public void onNIBPData(int _Systolic, int _Diastolic) {
        /*
         * mtvBP.setText(String.valueOf(_Systolic) + "/" +
         * String.valueOf(_Diastolic));
         */
        VitalVariables._Systolic = _Systolic;
        VitalVariables._Diastolic = _Diastolic;
    }

    public void onSave(int _FinalSystolic, int _FinalDiastolic) {
        /*
         * mtvBP.setText(String.valueOf(_Systolic) + "/" +
         * String.valueOf(_Diastolic));
         */
        VitalVariables._Systolic = _FinalSystolic;
        VitalVariables._Diastolic = _FinalDiastolic;
    }

    @Override
    public void onSPIROValue(Float _FVC, Float _FEV1, Float _PEFR, Float _Ratio) {
        /*
         * mtvFVC.setText(String.valueOf(_FVC));
         * mtvFEV.setText(String.valueOf(_FEV1));
         * mtvPERF.setText(String.valueOf(_PEFR));
         * mtvRatio.setText(String.valueOf(_Ratio));
         */
        VitalVariables._FVC = _FVC;
        VitalVariables._FEV1 = _FEV1;
        VitalVariables._PEFR = _PEFR;
        VitalVariables._Ratio = _Ratio;
    }

    @Override
    public void onReady() {
        machinestatus = "Machine Status : " + "Ready";
        SetMessage();
        mtvMachineStatus.setSelected(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
		/*setFilters();  // Start listening notifications from UsbService
		startService(UsbService.class, usbConnection, null); // Start UsbService(if it was not started before) and Bind it
*/
    }

    @Override
    public void onProbeAbsent() {
        machinestatus = "Machine Status : " + "Probe Absent";
        SetMessage();

    }

    @Override
    public void onNoFinger() {
        machinestatus = "Machine Status : " + "No Finger";
        SetMessage();

    }

    @Override
    public void onPulseSearch() {
        machinestatus = "Machine Status : " + "Searching Pulse";
        SetMessage();

    }

    @Override
    public void onLowPerfusion() {
        machinestatus = "Machine Status : " + "Low Perfusion";
        SetMessage();

    }

    @Override
    public void onWeakPulse() {
        machinestatus = "Machine Status : " + "Weak Pulse";
        SetMessage();

    }

    @Override
    public void onNIBPReady() {
        machinestatus = "Machine Status : " + "NIBP Ready";
        SetMessage();

    }

    @Override
    public void onCalibrating() {
        machinestatus = "Machine Status : " + "Calibrating ...Please Wait";
        SetMessage();

    }

    @Override
    public void onLeak() {
        machinestatus = "Machine Status : " + "Leak";
        SetMessage();
    }

    @Override
    public void onOverPressure() {
        machinestatus = "Machine Status : " + "Over Pressure";
        SetMessage();
    }

    @Override
    public void onBPError() {
        machinestatus = "Machine Status : " + "BP Error";
        SetMessage();

    }

    @Override
    public void onNIBPReading() {
        machinestatus = "Machine Status : " + "NIBP Reading";
        SetMessage();

    }

    @Override
    public void onCuffOversize() {
        machinestatus = "Machine Status : " + "Cuff Oversize";
        SetMessage();
    }

    @Override
    public void onCuffUndersize() {
        machinestatus = "Machine Status : " + "Cuff Undersize";
        SetMessage();
    }

    @Override
    public void onCuffMissing() {
        machinestatus = "Machine Status : " + "Cuff Missing";
        SetMessage();
    }

    @Override
    public void onExcessiveMotion() {
        machinestatus = "Machine Status : " + "Excessive Motion";
        SetMessage();
    }

    @Override
    public void onSignaltoostrong() {
        machinestatus = "Machine Status : " + "Signal Too Strong";
        SetMessage();
    }

    @Override
    public void onNIBPHardwareerror() {
        machinestatus = "Machine Status : " + "NIBP Hardware Error";
        SetMessage();
    }

    @Override
    public void onNIBPTimeout() {
        machinestatus = "Machine Status : " + "NIBP Timeout";
        SetMessage();
    }

    @Override
    public void onNIBPFlowError() {
        machinestatus = "Machine Status : " + "NIBP Flow Error";
        SetMessage();
    }

    @Override
    public void onProjectedSystolic() {
        machinestatus = "Machine Status : " + "Projected Sysstolic";
        SetMessage();
    }

    @Override
    public void onProjectedDiastolic() {
        machinestatus = "Machine Status : " + "Projected Diastolic";
        SetMessage();
    }

    @Override
    public void onTubeBlocked() {
        machinestatus = "Machine Status : " + "Tube Blocked";
        SetMessage();
    }

    @Override
    public void onCheckingCuff() {
        machinestatus = "Machine Status : " + "Checking Cuff";
        SetMessage();
    }

    @Override
    public void onCheckingLeak() {
        machinestatus = "Machine Status : " + "Checking Leak";
        SetMessage();
    }

    @Override
    public void onInflating() {

    }

    @Override
    public void onCheckingPulse() {
        machinestatus = "Machine Status : " + "Checking Pulse";
        mtvMachineStatus.setText(machinestatus + currentmode);

    }

    @Override
    public void onReleasing() {
        machinestatus = "Machine Status : " + "Releasing";
        mtvMachineStatus.setText(machinestatus + currentmode);
    }


    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mUsbReceiver);
        //unbindService(usbConnection);
    }

    @Override
    public void onLeadsOFF() {
        machinestatus = "Machine Status : " + "Leads off";
        mtvMachineStatus.setText(machinestatus + currentmode);

    }

    @Override
    public void onMode(int mode) {
        currentmode = "\tMode : " + Mode.values()[mode].name();
        SetMessage();
        if (_Mode.ordinal() != mode) {
            switch (_Mode) {
                case IDLE:
                    switch (connectionType) {
                        case BLUETOOTH:
                            //vBluetoothCommunication.SetIDLEMode();
                            break;
                        case USB:
                            vitalTrackUSBCommunication.SetIDLEMode();
                            break;
                        default:
                            break;
                    }

                    break;
                case ECG:
                    switch (connectionType) {
                        case BLUETOOTH:
                            //vBluetoothCommunication.SetECGMode();
                            break;
                        case USB:
                            vitalTrackUSBCommunication.SetECGMode();
                            break;
                        default:
                            break;
                    }

                    break;
                case NIBP:
                    switch (connectionType) {
                        case BLUETOOTH:
                            //vBluetoothCommunication.SetNIBPMode();
                            break;
                        case USB:
                            vitalTrackUSBCommunication.SetNIBPMode();
                            break;
                        default:
                            break;
                    }

                    break;
                case SPIRO:
                    switch (connectionType) {
                        case BLUETOOTH:
                            //vBluetoothCommunication.SetSPIROMode();
                            break;
                        case USB:
                            vitalTrackUSBCommunication.SetSPIROMode();
                            break;
                        default:
                            break;
                    }

                    break;
                case SPO2:
                    switch (connectionType) {
                        case BLUETOOTH:
                            //vBluetoothCommunication.SetSPO2Mode();
                            break;
                        case USB:
                            vitalTrackUSBCommunication.SetSPO2Mode();
                            break;
                        default:
                            break;
                    }

                    break;
                default:
                    break;
            }
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        switch (connectionType) {
            case BLUETOOTH:
			/*	if (vBluetoothCommunication != null)
					vBluetoothCommunication.StopConnection();
		*/		break;
            case USB:
                if (vitalTrackUSBCommunication != null)
                    vitalTrackUSBCommunication.stopIoManager();
                break;
            default:
                break;
        }
    }

    private void SetMessage() {
        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                mtvMachineStatus.setText(machinestatus);

            }
        });
    }

    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (device != null) {
                            //call method to set up device communication
                            connectionType = ConnectionType.USB;
                            vitalTrackUSBCommunication.startIoManager(ctx, mUsbManager,
                                    MainActivity.this);
                        }
                    } else {
                        Log.d(TAG, "permission denied for device " + device);
                    }
                }
            }
        }
    };




    public static Charset charset = Charset.forName("UTF-8");
    public static CharsetEncoder encoder = charset.newEncoder();
    public static CharsetDecoder decoder = charset.newDecoder();


    public static String bb_to_str(ByteBuffer buffer){
        String data = "";
        try{
            int old_position = buffer.position();
            data = decoder.decode(buffer).toString();
            // reset buffer's position to its original so it is not altered:
            buffer.position(old_position);
        }catch (Exception e){
            e.printStackTrace();
            return "";
        }
        return data;
    }
    public boolean checkInternetconn(){

        ConnectivityManager conMgr =  (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        {
            NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

            if (netInfo == null) {

                material_dialog_netconn();
                return false;

            } else {

                return  true;
            }
        }
    }

    MaterialDialog mMaterialDialog_net;

    public void material_dialog_netconn() {
        mMaterialDialog_net = new MaterialDialog(this)
                .setMessage("Please Check Internet Connection!!!")
                .setNegativeButton("Exit", new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog_net.dismiss();

                    }
                });

        mMaterialDialog_net.show();
    }



    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();
}
