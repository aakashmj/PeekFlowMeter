package metsl.spiro;

import metsl.vitaltrack.VitalTrackCallback;

public class VitalVariables {
	public static int diff_pointer;
	public static int read_ptr;
	public static int write_ptr;
	public static int  _ContiECGData [][] ;
	
	public static int spodiff_pointer;
	public static int sporead_ptr;
	public static int spowrite_ptr;
	public static int[] _ContiSPoData;


	public static VitalTrackCallback mVitalTrackCallback = null;
	public static String device_ID;
	public static String devicename;
	public static boolean _IsLiveECG = false;
	public static int HeartRate;
	public static int SPO2val;
	public static boolean SPO2Wave = false;

	public static int _Systolic = 0;
	public static int _Diastolic = 0;
	public static float _FVC;
	public static float _FEV1;
	public static float _PEFR;
	public static float _Ratio;
	public static boolean IsConnected = false;
	public static boolean IsTrying = false;

	public static int mode;

	public static int BluetoothStatus;

	public static int _ContiUSBECGData[][] = new int[16000][15];
	public static int _ContiUSBSPoData[]= new int[16000];
	public static int usbread_ptr = 0;
	public static int usbwrite_ptr = 0;
	public static int usbdiff_pointer = 0;

	public static int usbsporead_ptr = 0;
	public static int usbspowrite_ptr = 0;
	public static int usbspodiff_pointer = 0;

	// public static CustomizedBluetoothDevice CBD;
	public static String PATIENT_ID = "";


	public static void resetvariables() {
		device_ID = "";
		devicename = "";
		_IsLiveECG = false;
		HeartRate = 0;
		IsConnected = false;
		IsTrying = false;
		BluetoothStatus = 0;
		PATIENT_ID = "";
		read_ptr = 0;
		write_ptr = 0;
		diff_pointer = 0;
		_ContiECGData = new int[16000][15];

	}
}
