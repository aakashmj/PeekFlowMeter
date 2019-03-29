package metsl.spiro.usbcomm;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbRequest;
import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class VitalTrackUSBCommunication {
    UsbVitalTrackCallback mVitaltrackusbcallback;
    public    UsbDeviceConnection mConnection;
    final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    ByteBuffer buffer;
    private final static String TAG = "UsbLibrary";
    private boolean _runFlag;
    private int SPO2FrameCount = 0;
    private int FrameCount = 0;
    private Thread _USBHandlerThread;
    protected boolean isSending = false;
    protected boolean forceStop = false;
    private static UsbDevice mDevice;
    private static UsbEndpoint mEndpointIn;
    int[] data;// = new int[25];
    int dataCount;
    /**
     * Control Byte
     */
    private int _controlByte = -1;
    UsbRequest request;
    /**
     * Number of Bytes after Control Byte
     */
    private int _noOfBytes = 0;
    private static UsbEndpoint mEndPointOut;
    /**
     * Array for Collecting Data Recieved from USB
     */
    private  int[] _dataBuffer = null;
    private Thread _displayThread;

    /**
     * Data Write Pointer
     */
    private  int _writePtr;

    /**
     * Data Read Pointer
     */
    private  int _readPtr;

    /**
     * Data Difference Pointer
     */
    private  int _diffPtr;

    private ArrayList<ArrayList<Integer>> ECGData = new ArrayList<ArrayList<Integer>>();
    private ArrayList<Byte> SPO2Data = new ArrayList<Byte>();
    UsbManager mUsbManager;

    public void startIoManager(Context ctx,UsbManager mUsbManager,
                               UsbVitalTrackCallback mVitaltrackusbcallback) {
        this.mUsbManager = mUsbManager;
        this.mVitaltrackusbcallback = mVitaltrackusbcallback;

        if (mConnection == null) {
            HashMap<String, UsbDevice> devicelist = mUsbManager.getDeviceList();
            Iterator<UsbDevice> deviceIterator = devicelist.values().iterator();
            boolean founddevice = false;
            while (deviceIterator.hasNext()) {
                UsbDevice device = deviceIterator.next();
                // your code
                if ((device.getProductId() == 60000)
                        && (device.getVendorId() == 4292)) {
                    SetUSBDevice(device);
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    ctx.registerReceiver(mUsbReceiver, filter);
                    mUsbManager.requestPermission(mDevice, mPermissionIntent);
                    Log.d(TAG, "setDevice " + mDevice);
                    if(usbgrntperm) {
                        if (mDevice.getInterfaceCount() < 1) {
                            Log.e(TAG, "could not find interface");
                            this.mVitaltrackusbcallback.onConnectionFailed();
                            return;
                        }
                        this.mVitaltrackusbcallback.onConnectComplete(mDevice
                                .getDeviceName());
                        UsbInterface intf = mDevice.getInterface(0);
                        // device should have one endpoint
                        if (intf.getEndpointCount() < 1) {
                            Log.e(TAG, "could not find endpoint");
                            return;
                        }
                        for (int i = 0; i < intf.getEndpointCount(); i++) {
                            // endpoint should be of type interrupt
                            UsbEndpoint ep = intf.getEndpoint(i);
                            int dir = ep.getDirection();
                            int type = ep.getType();

                            if (dir == UsbConstants.USB_DIR_IN && type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                                mEndpointIn = ep;
                            } else if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                                    && ep.getDirection() == UsbConstants.USB_DIR_OUT) {
                                mEndPointOut = ep;
                            } else {
                                mEndPointOut = ep;
                            }
                        }

                        if (mDevice != null) {
                            UsbDeviceConnection connection = mUsbManager
                                    .openDevice(mDevice);
                            if (connection != null
                                    && connection.claimInterface(intf, true)) {
                                Log.d(TAG, "open SUCCESS");
                                mConnection = connection;

                                // To Set Baud Rate
                                mConnection.controlTransfer(0x40, 0, 0, 0, null, 0, 0);// reset
                                mConnection.controlTransfer(0x40, 0, 1, 0, null, 0, 0);// clear Rx
                                mConnection.controlTransfer(0x40, 0, 2, 0, null, 0, 0);// clear Tx
                                mConnection.controlTransfer(0x40, 0x03, 0x001A, 0, null, 0, 0);//Baud rate 115200

                            } else {
                                Log.d(TAG, "open FAIL");
                                mConnection = null;
                            }
                        }
                    }
                    founddevice = true;
                    _runFlag = true;
                    // Start collecting data from usb
                    new Runnable(){

                        @Override
                        public void run() {
                            _USBHandlerThread = new Thread(USBCommHandler);
                            _USBHandlerThread.start();
                            _displayThread = new Thread(_displayHandler);
                            _displayThread.start();

                        }
                    };
                    // _monitorview.StartPlotting();
                    break;
                }// End if((device.getProductId()
                // your code
                if ((device.getProductId() == 8570)
                        && (device.getVendorId() == 65535)) {
                    SetUSBDevice(device);
                    PendingIntent mPermissionIntent = PendingIntent.getBroadcast(ctx, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
                    ctx.registerReceiver(mUsbReceiver, filter);
                    mUsbManager.requestPermission(mDevice, mPermissionIntent);
                    Log.d(TAG, "setDevice " + mDevice);
                    if (mDevice.getInterfaceCount() < 1) {
                        Log.e(TAG, "could not find interface");
                        this.mVitaltrackusbcallback.onConnectionFailed();
                        return;
                    }
                    this.mVitaltrackusbcallback.onConnectComplete(mDevice
                            .getDeviceName());
                    UsbInterface intf = mDevice.getInterface(1);
                    // device should have one endpoint
                    if (intf.getEndpointCount() < 1) {
                        Log.e(TAG, "could not find endpoint");
                        return;
                    }
                    for (int i = 0; i < intf.getEndpointCount(); i++) {
                        // endpoint should be of type interrupt
                        UsbEndpoint ep = intf.getEndpoint(i);
                        int dir = ep.getDirection();
                        int type = ep.getType();

                        if (dir == UsbConstants.USB_DIR_IN &&type == UsbConstants.USB_ENDPOINT_XFER_BULK) {
                            mEndpointIn = ep;
                        }else if(ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK
                                && ep.getDirection() == UsbConstants.USB_DIR_OUT)
                        {
                            mEndPointOut = ep;
                        } else {
                            mEndPointOut = ep;
                        }
                    }

                    if (mDevice != null) {
                        UsbDeviceConnection connection = mUsbManager
                                .openDevice(mDevice);
                        if (connection != null
                                && connection.claimInterface(intf, true)) {
                            Log.d(TAG, "open SUCCESS");
                            mConnection = connection;
                        } else {
                            Log.d(TAG, "open FAIL");
                            mConnection = null;
                        }
                    }
                    founddevice = true;
                    _runFlag = true;
                    // Start collecting data from usb
                    new Runnable(){

                        @Override
                        public void run() {
                            _USBHandlerThread = new Thread(USBCommHandler);
                            _USBHandlerThread.start();
                            _displayThread = new Thread(_displayHandler);
                            _displayThread.start();

                        }
                    };
                    // _monitorview.StartPlotting();
                    break;
                }// End if((device.getProductId()
            }// End While (deviceIterator.hasNext()
            if (!founddevice) {
                mVitaltrackusbcallback.onConnectionFailed();
                return;
            }// End if (founddevice == false)
        }// End if (mConnection == null) {
        else {
            _runFlag = true;
            // Start collecting data from usb
            _USBHandlerThread = new Thread(USBCommHandler);
            _USBHandlerThread.start();

            _displayThread = new Thread(_displayHandler);
            _displayThread.start();

            // _monitorview.StartPlotting();
        }
    }

    /**
     * Handler for Parsing and displaying Data
     */
    private Runnable _displayHandler = new Runnable() {

        @Override
        public void run() {

            int _sleepcount = 0;

            while (_runFlag) {

                // Parse the data
                ParseData();



            } // End Loop

        }

    };

    // ---------------------------------------------------------------
    // - THREAD HANDLER CLASS
    // ---------------------------------------------------------------
    private Runnable USBCommHandler = new Runnable() {

        @Override
        public void run() {

            _dataBuffer = new int[20000];
            _writePtr = 0;
            _diffPtr = 0;
            _readPtr = 0;
            buffer = ByteBuffer.allocate(64);
            request = new UsbRequest();
            request.initialize(mConnection, mEndpointIn);

            while (_runFlag) {
                try {
                    // queue a request on the interrupt endpoint
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        request.queue(buffer);
                        Log.i("buffer",String.valueOf(buffer));
                    }else{
                        if (!request.queue(buffer, buffer.capacity())) {
                            Log.i("buffer",String.valueOf(buffer));
                            throw new IOException("Error queueing request.");
                        }
                        Log.i("buffer",String.valueOf(buffer));

                    }




                    // wait for data from USB
                    if (mConnection.requestWait() == request) {
                        if (request == null) {
                            throw new IOException("Null response");
                        }
                        if (_runFlag) {
                            for (int i = 0; i < buffer.capacity(); i++) {
                                Log.i("buffer in  Forloop", String.valueOf(buffer.toString()));

                                _dataBuffer[_writePtr] = (buffer.get(i) & 0xFF);
                                _writePtr++;
                                _diffPtr++;
                                Log.i("diffptr in for loop",String.valueOf(_diffPtr));

                                if (_writePtr >= _dataBuffer.length)
                                    _writePtr = 0;
                            } // End Loop
//4 july commented for testing
                            //	_diffPtr += buffer.capacity();
                            buffer.clear();
                            Log.i("diffptr in writing",String.valueOf(_diffPtr));

                        } // End If Runflag == true
                    } else {
                        if (mDevice != null) {
                            SetUSBDevice(null);
                        }
                        Log.d(TAG, "requestWait failed, exiting");
                        break;
                    } // End If
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } // End Loop

        } // End Run
    };

    public void SetUSBDevice(UsbDevice usbDevice) {
        if (usbDevice == null) {
            mConnection = null;
            mDevice = null;
            mEndpointIn = null;
            mEndPointOut = null;
            return;
        }
        mDevice = usbDevice;

    }

    public void stopIoManager() {
        // Make the Run Flag as Flase to Stop the Data Collector and Display
        // Threads
        _runFlag = false;
        // Set the usbDevice as null to close connection
        if (mDevice != null) {
            SetUSBDevice(null);
        }

    }

    // Parse Data
    private void ParseData() {
        int lcount = 0;
        while (_diffPtr > 0) {
            if (_dataBuffer == null)
                break;
            int dataByte = _dataBuffer[_readPtr];
            // Log.i(TAG, "Diff : " + _diffPtr);
            _diffPtr--;
            _readPtr++;
            if (_readPtr >= _dataBuffer.length)
                _readPtr = 0;

            if (dataByte > 0xF0) {
                // Control Byte Found
                _controlByte = dataByte;
                _noOfBytes = 0;
                // Log.i(TAG, "Control byte : " + _controlByte);
            } else {
                if (_noOfBytes == 0) {
                    // Assign No of Bytes
                    _noOfBytes = dataByte;
                    data = new int[_noOfBytes];
                    dataCount = 0;
                } else {
                    data[dataCount] = dataByte;
                    dataCount++;
                    // Collect Data as per control byte----??-
                    if (dataCount == _noOfBytes) {
                        _noOfBytes = 0; // Reset the counters
                        dataCount = 0;
                        switch (_controlByte) {
                            case 0xF1:
                                try {
                                    // Live ECG
                                    ArrayList<Integer> ecgbyte = new ArrayList<Integer>();
                                    int[] ecgdata = new int[13];
                                    if (data.length == 6 || data.length == 8) {
                                        ecgdata[1] = (data[0] * 128) + data[1]
                                                - 3000;
                                        ecgdata[2] = (data[2] * 128) + data[3]
                                                - 3000;
                                        ecgdata[6] = (data[4] * 128) + data[5]
                                                - 3000;

                                        ecgdata[0] = (ecgdata[1] - ecgdata[2]);
                                        ecgdata[5] = ((ecgdata[1] + ecgdata[2]) / 2);
                                        ecgdata[4] = ((ecgdata[0] - ecgdata[2]) / 2);
                                        ecgdata[3] = ((-ecgdata[0] - ecgdata[1]) / 2);

                                    } else {
                                        ecgdata[1] = ((data[0] * 128) + data[1]
                                                - 3000)/4;
                                        ecgdata[2] = ((data[2] * 128) + data[3]
                                                - 3000)/4;
                                        ecgdata[6] = ((data[4] * 128) + data[5]
                                                - 3000)/4;
                                        ecgdata[7] = ((data[6] * 128) + data[7]
                                                - 3000)/4;
                                        ecgdata[8] = ((data[8] * 128) + data[9]
                                                - 3000)/4;
                                        ecgdata[9] = ((data[10] * 128) + data[11]
                                                - 3000)/4;
                                        ecgdata[10] = ((data[12] * 128) + data[13]
                                                - 3000)/4;
                                        ecgdata[11] = ((data[14] * 128) + data[15]
                                                - 3000)/4;
                                        ecgdata[0] = (ecgdata[1] - ecgdata[2]);
                                        ecgdata[5] = ((ecgdata[1] + ecgdata[2]) / 2);
                                        ecgdata[4] = ((ecgdata[0] - ecgdata[2]) / 2);
                                        ecgdata[3] = ((-ecgdata[0] - ecgdata[1]) / 2);
                                    }

                                    for (int i = 0; i < ecgdata.length; i++) {
                                        ecgbyte.add(ecgdata[i]);
                                    }

                                    ECGData.add(ecgbyte);
                                    FrameCount++;
                                    if (FrameCount >= 100) {
                                        FrameCount = 0;
                                        mVitaltrackusbcallback.onLiveECG(ECGData);
                                        ECGData.clear();

                                        break;
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                break;
                            case 0xF2:
                                mVitaltrackusbcallback
                                        .onHeartRate((data[0] & 0xFF));
                                break;
                            case 0xF3:
                                mVitaltrackusbcallback
                                        .onHeartRate((data[0] & 0xFF));
/*
							SPO2Data.add((byte) data[0]);
							SPO2FrameCount++;
							if (SPO2FrameCount >= 100) {
								SPO2FrameCount = 0;
								mVitaltrackusbcallback.onLiveSPO2(SPO2Data);
								SPO2Data.clear();

							}
*/
                                break;
                            case 0xF4:
                                mVitaltrackusbcallback.onSPO2((data[0] & 0xFF));
                                break;

                            case 0xF5: // handle NIBP Data
                                if (data.length >= 4) {
                                    int _Systolic, _Diastolic;
                                    _Systolic = data[1] * 128 + data[2];
                                    _Diastolic = data[3];
                                    mVitaltrackusbcallback.onNIBPData(_Systolic,
                                            _Diastolic);
                                }
                                break;
                            case 0xFE: // handle SPIRO Values
                                try {

                                    byte[] tempbyte = new byte[data.length];
                                    for (int i = 0; i < tempbyte.length; i++) {
                                        tempbyte[i] = (byte) data[i];
                                    }
                                    String spirovals = null;

                                    spirovals = new String(tempbyte, "US-ASCII");
                                    String[] _values = spirovals.split(",");
                                    Float _FVC, _FEV1, _PEFR, _Ratio;
                                    _FVC = Float.valueOf(_values[0]);
                                    _FEV1 = Float.valueOf(_values[1]);
                                    _PEFR = Float.valueOf(_values[2]);
                                    _Ratio = Float.valueOf(_values[3]);

                                    mVitaltrackusbcallback.onSPIROValue(_FVC,
                                            _FEV1, _PEFR, _Ratio);

                                } catch (Exception ex) {
                                    // TODO: handle exception
                                    System.err.println(ex.getMessage());
                                }
                                break;
                            case 0xFB:
                                try {

                                    switch (data[0]) {
                                        case 2:
                                            switch (data[1]) {
                                                case 0: // All ok
                                                    mVitaltrackusbcallback.onReady();
                                                    break;
                                                case 1: // Probe Absent
                                                    mVitaltrackusbcallback.onProbeAbsent();
                                                    break;
                                                case 2: // No finger
                                                    mVitaltrackusbcallback.onNoFinger();
                                                    break;
                                                case 3: // Pulse search
                                                    mVitaltrackusbcallback.onPulseSearch();
                                                    break;
                                                case 4: // low perfusion
                                                    mVitaltrackusbcallback.onLowPerfusion();
                                                    break;
                                                case 5: // Weak Pulse
                                                    mVitaltrackusbcallback.onWeakPulse();
                                                    break;
                                                default:
                                                    break;
                                            }
                                            break;

                                        case 7:
                                            switch (data[1]) {
                                                case 0: // All ok
                                                    mVitaltrackusbcallback.onNIBPReady();
                                                    break;
                                                case 1: // Busy
                                                    mVitaltrackusbcallback.onCalibrating();
                                                    break;
                                                case 2: // Leak
                                                    mVitaltrackusbcallback.onLeak();
                                                    break;
                                                case 3: // over prewsure
                                                    mVitaltrackusbcallback.onOverPressure();
                                                    break;
                                                case 4: // error
                                                    mVitaltrackusbcallback.onBPError();
                                                    break;
                                                case 5: // VALID
                                                    // statusLabel.setText("Valid");
                                                    // statusLabel.setForeground(Color.black);
                                                    break;
                                                case 6: // machine reading status
                                                    mVitaltrackusbcallback.onNIBPReading();
                                                    break;
                                                case 7:
                                                    mVitaltrackusbcallback.onCuffOversize();
                                                    break;
                                                case 8:
                                                    mVitaltrackusbcallback
                                                            .onCuffUndersize();

                                                    break;
                                                case 9:
                                                    mVitaltrackusbcallback.onWeakPulse();

                                                    break;
                                                case 10:
                                                    mVitaltrackusbcallback.onLeak();

                                                    break;
                                                case 11:
                                                    mVitaltrackusbcallback.onCuffMissing();
                                                    break;
                                                case 12:

                                                    mVitaltrackusbcallback
                                                            .onExcessiveMotion();

                                                    break;
                                                case 13:

                                                    mVitaltrackusbcallback
                                                            .onSignaltoostrong();

                                                    break;
                                                case 14:
                                                    mVitaltrackusbcallback
                                                            .onNIBPHardwareerror();
                                                    break;
                                                case 15:
                                                    mVitaltrackusbcallback.onNIBPTimeout();

                                                    break;
                                                case 16:
                                                    mVitaltrackusbcallback
                                                            .onNIBPFlowError();

                                                    break;
                                                case 17:
                                                    mVitaltrackusbcallback
                                                            .onProjectedSystolic();

                                                    break;
                                                case 18:
                                                    mVitaltrackusbcallback
                                                            .onProjectedDiastolic();

                                                    break;
                                                case 19:
                                                    mVitaltrackusbcallback.onTubeBlocked();
                                                    break;

                                                case 20:
                                                    mVitaltrackusbcallback.onCheckingCuff();

                                                    break;

                                                case 21:
                                                    mVitaltrackusbcallback.onCheckingLeak();

                                                    break;
                                                case 22:
                                                    mVitaltrackusbcallback.onInflating();

                                                    break;
                                                case 23:
                                                    mVitaltrackusbcallback
                                                            .onCheckingPulse();

                                                    break;
                                                case 24:
                                                    mVitaltrackusbcallback.onReleasing();

                                                    break;

                                                default:

                                                    break;

                                            }
                                            break;
                                        case 8:
                                            mVitaltrackusbcallback.onMode(data[1]);
                                            break;

                                        case 9:
                                            switch (data[1]) {
                                                case 0:
                                                    mVitaltrackusbcallback.onReady();
                                                    break;
                                                case 1:
                                                    mVitaltrackusbcallback.onLeadsOFF();
                                                    break;
                                            }
                                            break;
                                        case 12: // receiving the device ID from the VT2
                                            byte[] tempbyte = new byte[data.length];

                                            for (int i = 0; i < tempbyte.length; i++) {
                                                tempbyte[i] = (byte) data[i];
                                            }
                                            String _DeviceID = (new String(tempbyte))
                                                    .trim();

                                            mVitaltrackusbcallback
                                                    .onDeviceID(_DeviceID);

                                            break;
                                    }
                                } catch (Exception e) {
                                    // TODO: handle exception
                                    e.printStackTrace();
                                }
                                break;

                        } // End Switch Control Byte
                        _controlByte = 0;
                    }// End if(dataCount != NOB )
                } // End Else ( _noB == 0 )
            } // End Else ( data > 0xF0 )
            if (++lcount > 1000) {
                break;
            }
        }// End While(_diffPtr > 0)
        Log.i("diffptr in reading",String.valueOf(_diffPtr));

    } // End Parse Data

    public void Startspiroreading() {
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xA1, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void Getspirovalues() {
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xA2, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void SetIDLEMode() {
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xFF, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void SetECGMode() {
        //	SetIDLEMode();
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xF1, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void SetNIBPMode() {
        //		SetIDLEMode();
        byte[] moutput = { (byte) 0xF5, (byte) 0x96, (byte) 0xAA, (byte) 0xF5,
                (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void SetSPO2Mode() {
        //SetIDLEMode();
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xF2, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void SetSPIROMode() {
        //SetIDLEMode();
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xF6, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }

    public void StartNIBP() {
        byte[] moutput = { (byte) 0x96, (byte) 0xAA, (byte) 0xF5, (byte) 0x55 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }



    public void StopNIBP() {
        byte[] moutput = { (byte) 0xF5, (byte) 0x01, (byte) 0x01 };
        mConnection.bulkTransfer(mEndPointOut, moutput, moutput.length, 100);
    }
    boolean usbgrntperm =false;
    private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {

                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if(mDevice != null){
                            //call method to set up device communication
                            usbgrntperm=true;
                        }
                    }
                    else {
                        Log.d(TAG, "permission denied for device " + mDevice);
                    }
                }
            }
        }
    };
    // All Host(ECG) To Device(Mobile) Operat
}
