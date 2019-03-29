package metsl.spiro;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONObject;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;

import aakashwifi.ecgfilter.elr_wifi.logger.Log4jHelper;


public class WCFCall {
	public static final String BASE_URL = "/WCFService.svc";
	SharedPreferences sharedpreferences;
	Context context;
	JSONObject json = new JSONObject();
	org.apache.log4j.Logger log = Log4jHelper.getLogger("WCFCall");

	public boolean InsertEcgData2(byte[] demop,String device_id,String date_of_downloadfile, String patient_id,
								  String percentagedata, String sp_name, String patient_name, String age, String gender, String height,
								  String weight, Context context) {
		boolean resp = false;

		try {
			sharedpreferences = context.getSharedPreferences("eUnoSettings", 0);
			String url = sharedpreferences.getString("url", "www.rhythms24x7.com");
			URL siteUrl = new URL("http://" + url
					+ "/ELRWifiECGUpload.aspx");
			HttpURLConnection conn = (HttpURLConnection) siteUrl
					.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			StringBuilder requestParams = new StringBuilder();
			requestParams.append(URLEncoder.encode("Ecg", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(Base64.encode(demop), "UTF-8"));

			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Patientid", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(patient_id, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("DeviceID", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(device_id, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("percentagedata", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(percentagedata, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("SpName", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(sp_name, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("DateOfDownloadFile",
					"UTF-8"));
			requestParams.append("=").append(URLEncoder.encode(date_of_downloadfile,"UTF-8"));

			requestParams.append("&");
			requestParams.append(URLEncoder.encode("PatientName", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(patient_name, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Age", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(age, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Gender", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(String.valueOf(gender), "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Height", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(height, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Weight", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(weight, "UTF-8"));
			requestParams.append("&");

			// sends POST data
			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());
			writer.write(requestParams.toString());
           // log.info(requestParams.toString());
			Log.d("upload url",requestParams.toString());
			log.info("URL"+siteUrl+""+requestParams.toString());
			writer.flush();
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line = "";
				while ((line = in.readLine()) != null) {
					if (line.matches("true")) {
						System.out.println(line);
						resp = true;
						log.info("Upload"+ true);

						break;
					}
				}
				in.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				log.info("Upload"+e.getMessage());

			}
			conn.disconnect();

		} catch (Exception e) {
			resp = false;
		}
		return resp;

	}

	private final String SOAP_NAMESPACE = "http://tempuri.org/";
	private final String SOAP_URL = "http://103.224.247.57:6484/ECGDataUploadService.asmx";

	private final String SOAP_ACTION = "http://tempuri.org/ECGDataUploadCMS";

	private final String SOAP_METHOD_NAME = "ECGDataUploadCMS";
	private SoapObject request;
	private PropertyInfo pi1,pi2,pi3,pi4,pi5,pi6,pi7,pi8;

	SoapPrimitive  response;
	public boolean UsbInsertEcgData(byte[] demop,String date_of_downloadfile, String patient_id,
								 String patient_name, String age, String gender, String mobile,String opdid) {
		boolean resp = false;
		try {

			//webservice thingi start

			request = new SoapObject(SOAP_NAMESPACE, SOAP_METHOD_NAME);

			pi1 = new PropertyInfo();
			pi1.setName("Patid");
			pi1.setValue(patient_id);//get the string that is to be sent to the web service
			pi1.setType(String.class);
			request.addProperty(pi1);

			pi4 = new PropertyInfo();
			pi4.setName("Patname");
			pi4.setValue(patient_name);
			pi4.setType(String.class);
			request.addProperty(pi4);

			pi5 = new PropertyInfo();
			pi5.setName("Mobileno");
			pi5.setValue(mobile);
			pi5.setType(String.class);
			request.addProperty(pi5);

			pi6 = new PropertyInfo();
			pi6.setName("age");
			pi6.setValue(age);
			pi6.setType(String.class);
			request.addProperty(pi6);

			pi7= new PropertyInfo();
			pi7.setName("gender");
			pi7.setValue(gender);
			pi7.setType(String.class);
			request.addProperty(pi7);

			pi8 = new PropertyInfo();
			pi8.setName("opdid");
			pi8.setValue(opdid);
			pi8.setType(String.class);
			request.addProperty(pi8);


			pi2 = new PropertyInfo();
			pi2.setName("ecgdata");
			pi2.setValue(Base64.encode(demop));//get the string that is to be sent to the web service
			pi2.setType(String.class);
			request.addProperty(pi2);

			pi3 = new PropertyInfo();
			pi3.setName("ecgdatetime");
			pi3.setValue(date_of_downloadfile);//get the string that is to be sent to the web service
			pi3.setType(String.class);
			request.addProperty(pi3);




			SoapSerializationEnvelope envp = new SoapSerializationEnvelope(SoapEnvelope.VER11);
			envp.dotNet = true;
			envp.setOutputSoapObject(request);
			HttpTransportSE androidHttpTransport = new HttpTransportSE(SOAP_URL);
			try {
				androidHttpTransport.call(SOAP_ACTION, envp);
				response = (SoapPrimitive)envp.getResponse();
				if(response.toString().contains("true")){
					resp=true;
				}else {
					resp=false;
				}
			} catch (Exception e) {
				Log.i("WS Error->",e.toString());
			}

		/*requestParams.append(URLEncoder.encode("DeviceID", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(device_id, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("DeviceID", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(device_id, "UTF-8"));
			//webservice thingi ends

			/*sharedpreferences = context.getSharedPreferences("eUnoSettings", 0);
			String url = sharedpreferences.getString("url", "103.8.127.207");
			URL siteUrl = new URL("http://" + url
					+ "/ecgupload/ecgdataupload.svc/ECGDataUploadCMS/");
			HttpURLConnection conn = (HttpURLConnection) siteUrl
					.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setInstanceFollowRedirects(false);

			SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			StringBuilder requestParams = new StringBuilder();
			requestParams.append(URLEncoder.encode("Patid", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(patient_id, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("ecgdata", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(Base64.encode(demop), "UTF-8"));

			requestParams.append("&");

			requestParams.append(URLEncoder.encode("ecgdatetime",
					"UTF-8"));
			requestParams.append("=").append(URLEncoder.encode(date_of_downloadfile,"UTF-8"));


			*//*requestParams.append(URLEncoder.encode("DeviceID", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(device_id, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("DeviceID", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(device_id, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("percentagedata", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(percentagedata, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("SpName", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(sp_name, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Ecg", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(Base64.encode(demop), "UTF-8"));

			requestParams.append("&");

			requestParams.append(URLEncoder.encode("DateOfDownloadFile",
					"UTF-8"));
			requestParams.append("=").append(URLEncoder.encode(date_of_downloadfile,"UTF-8"));

			requestParams.append("&");

			requestParams.append(URLEncoder.encode("Height", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(height, "UTF-8"));
			requestParams.append("&");
			requestParams.append(URLEncoder.encode("Weight", "UTF-8"));
			requestParams.append("=").append(
					URLEncoder.encode(weight, "UTF-8"));
			requestParams.append("&");*//*

			// sends POST data
			OutputStreamWriter writer = new OutputStreamWriter(
					conn.getOutputStream());
			writer.write(requestParams.toString());
			// log.info(requestParams.toString());
			Log.d("upload url",requestParams.toString());
			log.info("URL"+siteUrl+""+requestParams.toString());
			writer.flush();
			try {

				BufferedReader in = new BufferedReader(new InputStreamReader(
						conn.getInputStream()));
				String line = "";
				while ((line = in.readLine()) != null) {
					if (line.contains("true")) {
						System.out.println(line);
						resp = true;
						log.info("Upload"+ true);

						break;
					}
				}
				in.close();
			}
			catch (Exception e) {
				e.printStackTrace();
				log.info("Upload"+e.getMessage());

			}
			conn.disconnect();

		} catch (Exception e) {
			resp = false;
		}*/
		}catch (Exception e) {
				resp = false;
			}
		return resp;

	}


}


