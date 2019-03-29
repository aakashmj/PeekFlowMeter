package metsl.spiro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PathEffect;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.Log;


/**
 * Created by aakash on 8/24/15.
 */
public class UsbDrawAndSaveECG {

    Bitmap ecgBmp;
    Canvas ecgCanvas;
    Paint ecgPaint = new Paint();

    static int count = 0;

    //Width of the container in which each Lead is plotted
    private float LeadWidth;

    //Height of the container in which each Lead is plotted
    private float LeadHeight;

    public static float mm_Per_Inch = 25f;

    //Inches in 1 millimeter]\041
    public static float Inch_Per_mm = 1f / mm_Per_Inch;

    //X DPI of the Image
    public static float DpiX = 600 ;//200;

    // Y DPI of the Image
    public static float DpiY =  600;///200;

    //Millimeter Per GridLine
    public static float _mm_Per_GridLine = 5.0f;

    //Millimeter Per Second
    private float mm_Per_s = (float) 25.0f;

    //Millimeter Per micro Volt
    private float mm_Per_mV = 10f;
    private float SamplingRate;
    private float[] gain = new float[] { 5, 10, 15, 20 };
    private int gain_val = EcgPreviewActivity.gainvalue;
    private int Offset = 0;
    private int devicetype = 1;


    // use bp parameter as doctor name
    public Bitmap CreateImage(int[][] data, int sampling_rate,
                              String filename, Context _context, String Patid, int _HR, int _sao,
                              String _BP, boolean showval, int setgain, int _DeviceType,
                              String PatientName, String PatientAge, String PatientGender , String CreatedDate, String hospitalName, String LongLead, String Analysis ,
                              String comments , String interpretation , String height, String weight, String deviceID) {
        devicetype = _DeviceType;
        gain_val = EcgPreviewActivity.gainvalue;
        SamplingRate = sampling_rate;
        int _width = 2338;
        int _height = 1654;
        DpiX = _width / 12.69f;//11.69f
        DpiY = _height / 11.27f;//8.27
        ecgBmp = Bitmap.createBitmap(_width, _height, Config.RGB_565);
        ecgCanvas = new Canvas(ecgBmp);
        ecgCanvas.drawColor(Color.WHITE);
        ecgPaint.setAntiAlias(true);
        ecgPaint.setStrokeWidth(2.0f);
        float currX = 120.0f;
        float currY = 70.0f;

        if(count != 3){
            count = 3;}
        else {
            count = 3;
        }
        // Draw Heading and Patient Details
        //	currY = DrawHeading(_context, currX, currY);

        currY = DrawpatientInfo_3x4(currX, currY, _width, Patid, _HR, height, weight,
                showval, PatientName, PatientAge, PatientGender ,CreatedDate ,hospitalName,Analysis,deviceID);

        currY += 120;  // 160
        // Calculate the Height and width of the Container in which each lead of
        // 3 sec is to be plotted
        LeadWidth =  (DpiX * Inch_Per_mm) * 62.5f ; // for drawing 18 blocks each of 0.5mm

        LeadHeight = (DpiX * Inch_Per_mm) * 35; // for drawing 6 blocks each of 0.5mm

        // Calculate the width of the Container in lead of 8 sec i.e long lead
        // is to be plotted
        float LongBox = DpiX * Inch_Per_mm * 250;
        LongBox += 20;   // added by manisha  10th feb 2016
        LeadWidth += 18f;  // width  for one lead in 3x4

        // Draw the Grid
        plotECGGrid(currX, currY, LongBox, LeadHeight * 4);

        // Draw Gain Pulse
        //  DrawPulse(currX, currY, LongBox, (DpiX * Inch_Per_mm) * 52f);

        DrawLead(currX, currY, "I", data, LeadWidth,
                (DpiX * Inch_Per_mm) * 52f, 0, ((float) sampling_rate * 2.5f)
                        + Offset);

        // Calculate Next X Postion
        currX += LeadWidth;

        DrawLead(currX, currY, "AVR", data, LeadWidth,
                (DpiX * Inch_Per_mm) * 52f, 3, ((float) sampling_rate * 2.5f)
                        + Offset);

        currX += LeadWidth;

        DrawLead(currX, currY, "V1", data, LeadWidth,
                (DpiX * Inch_Per_mm) * 52f, 6, ((float) sampling_rate * 2.5f)
                        + Offset);

        currX += LeadWidth;

        DrawLead(currX, currY, "V4", data, LeadWidth,
                (DpiX * Inch_Per_mm) * 52f, 9, ((float) sampling_rate * 2.5f)
                        + Offset);


        currX += LeadWidth;
        // Draw Gain Pulse
        DrawPulse(currX, currY, LongBox, (DpiX * Inch_Per_mm) * 52f);

        // Calculate Next Y Postion
        currY += LeadHeight;
        currX = 120.0f;
        Offset = 0;

        // Draw Gain Pulse


        DrawLead(currX, currY, "II", data, LeadWidth, LeadHeight, 1,
                ((float) sampling_rate * 2.5f) + Offset);

        // Calculate Next X Postion
        currX += LeadWidth;

        DrawLead(currX, currY, "AVL", data, LeadWidth, LeadHeight, 4,
                ((float) sampling_rate * 2.5f) + Offset);

        currX += LeadWidth;

        DrawLead(currX, currY, "V2", data, LeadWidth, LeadHeight, 7,
                ((float) sampling_rate * 2.5f) + Offset);

        currX += LeadWidth;

        DrawLead(currX, currY, "V5", data, LeadWidth, LeadHeight, 10,
                ((float) sampling_rate * 2.5f) + Offset);

        // Draw Gain Pulse
        currX += LeadWidth;
        DrawPulse(currX, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        currY += LeadHeight;
        currX = 120.0f;
        Offset = 0;



        DrawLead(currX, currY, "III", data, LeadWidth, LeadHeight, 2,
                ((float) sampling_rate * 2.5f) + Offset);

        // Calculate Next X Postion
        currX += LeadWidth;

        DrawLead(currX, currY, "AVF", data, LeadWidth, LeadHeight, 5,
                ((float) sampling_rate * 2.5f) + Offset);

        currX += LeadWidth;

        DrawLead(currX, currY, "V3", data, LeadWidth, LeadHeight, 8,
                ((float) sampling_rate * 2.5f) + Offset);

        currX += LeadWidth;

        DrawLead(currX, currY, "V6", data, LeadWidth, LeadHeight, 11,
                ((float) sampling_rate * 2.5f) + Offset);


        // Draw Gain Pulse

        currX += LeadWidth;
        DrawPulse(currX, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        float l_w = LeadWidth;
        currY += LeadHeight;
        currX = 120.0f;
        Offset = 0;

        // ***********************************Draw Long Lead II********************************************//

        Draw_LongLead(currX, currY,LongLead, data, LongBox, LeadHeight, 1,((float) sampling_rate * 10.0f) + Offset);
        // *******************************Draw Long Lead II********************************************//


        // Draw Gain Pulse
        float x = LongBox + 180.0f;
        DrawPulse(x, currY, LongBox, LeadHeight);

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Comments : ", currX, currY + LeadHeight + 50,ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(comments, currX + 180, currY + LeadHeight + 50,ecgPaint);

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Interpretation : ", currX, currY + LeadHeight + 75,ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(interpretation, currX +180, currY + LeadHeight + 75,ecgPaint);

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        currY = _height - 100;


        Rect bound = new Rect(0, 0, 0, 0);
        String sign = "Signature : "+_BP;
        ecgPaint.getTextBounds(sign, 0, sign.length(), bound);
        ecgCanvas.drawText(sign, _width - 260 - bound.width(), currY - 20,ecgPaint);

        ecgCanvas.drawLine(currX, currY, _width - 100, currY, ecgPaint);

        currY += 10;

        Rect bounds = new Rect(0, 0, 0, 0);
        String genby = "(C) METSL 2016";
        ecgPaint.getTextBounds(genby, 0, genby.length(), bounds);
        ecgCanvas.drawText(genby, _width - 100 - bounds.width(), currY + 20,
                ecgPaint);
        ecgCanvas.drawText(_context.getString(R.string.app_name), currX,
                currY + 20, ecgPaint);


        return ecgBmp;
    }

    private float DrawHeading(Context _context, float currX, float currY) {
        Bitmap bitmap = BitmapFactory.decodeResource(_context.getResources(),
                R.drawable.button_nibp);
        ecgCanvas.drawBitmap(bitmap, currX, currY, ecgPaint);
        currY += bitmap.getHeight() + 25;
        return currY;
    }
    private float DrawpatientInfo_12x1(float currX, float currY, float paper_width,
                                       String Patid, int _HR, String _height, String _weight, boolean showval,
                                       String PatientName, String PatientAge, String PatientGender , String CreatedDate, String hospitalname, String Analysis, String deviceID) {

        String Str = Analysis;

        String one = null;
        String two = null;
        int count =0;

        for (String retval: Str.split("#Interpretation", 2)){
            if(count == 0)
            {
                one = retval;
                count++;
            }
            two = retval;
            System.out.println(retval);
        }
        Log.e("Analysisssssssssss ", "Analysis : " +one);
        Log.e("Analysisssssssssss ", "Analysis : " +two);
        assert one != null;
        one = one.replace("*Measurements:-", "");
        assert two != null;
        two = two.replace(":-", "");
        one = one.replace("(bpm)", "");
        one = one.replace("AXES", "");
        one = one.replace("(msec)", "");
        one = one.replace("(deg)", "");
        one = one.replace("Intervals", "");
        one = one.replace(":", " ");

        String strArry[]= one.split(" ");

        for (String item : strArry) {

            Log.e("str ", "" + item);
        }
        // currY -= 28;   // 30
        ecgPaint.setTextSize(26.0f);
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgPaint.setColor(Color.BLACK);
        currY +=  5 ;//35.0f;//15;

        if(hospitalname != null){
            ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
            ecgCanvas.drawText("Hospital Name :", currX, currY, ecgPaint);
            ecgPaint.setTypeface(Typeface.DEFAULT);
            ecgCanvas.drawText(hospitalname, currX + 200, currY, ecgPaint);

        }
        else {
            ecgCanvas.drawText("", currX, currY, ecgPaint);
        }

        currY += 23;//previously 17//

        ecgPaint.setTextSize(26.0f);

        float currY_fix = currY;
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Patient Details ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Name :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientName, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Age :     ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientAge, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Gender :   ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientGender, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        /*ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("DOB :", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText("NA", currX + 140, currY, ecgPaint);
        currY +=  27 ;*/

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Height :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(_height+" cm", currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Weight :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(_weight+" kg", currX + 140, currY, ecgPaint);

        currY = currY_fix;
        currX += 400;


        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Recording Details ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Recorded : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(CreatedDate, currX + 155, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Device   : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText("eUNO-WiFi", currX + 155, currY, ecgPaint);

        currY += 27;
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Device Id  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(deviceID, currX + 155, currY, ecgPaint);


        currY = currY_fix;
        currX += 420;


        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Measurements ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("HR  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length  > 3) {
            ecgCanvas.drawText(strArry[3] + " bpm", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" bpm", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("PR Interval  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 23) {
            ecgCanvas.drawText(strArry[23] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QRS Interval : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 28) {
            ecgCanvas.drawText(strArry[28] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QT Interval  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 32){
            ecgCanvas.drawText(strArry[32] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QTc Interval : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 36){
            ecgCanvas.drawText(strArry[36] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("P,QRS,T Axis : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 8) {
            float p = Float.parseFloat(strArry[13]);
            float qrs = Float.parseFloat(strArry[8]);
            float t = Float.parseFloat(strArry[17]);

            ecgCanvas.drawText(String.format("%.1f",p) + "," + String.format("%.1f",qrs) + "," + String.format("%.1f" ,t), currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText("", currX + 170, currY, ecgPaint);

        currY = currY_fix;
        currX += 350;
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Analysis  : ( Minnesota code )", currX, currY, ecgPaint);
        currY +=  27 ;
        ecgPaint.setTypeface(Typeface.DEFAULT);
        String ar[] = splitByNumber(two,75);

        if(EcgPreviewActivity.onAnalysis_status == 0 && Analysis.contains(""))
        {
            ecgCanvas.drawText("NA", currX, currY, ecgPaint);
        }
        if(two.length() < 74 )
        {
            ecgCanvas.drawText(two, currX, currY, ecgPaint);
        }
        if(ar.length > 1) {

            ecgCanvas.drawText(ar[0], currX, currY, ecgPaint);

            if(ar.length > 1){

                currY += 27;
                ecgCanvas.drawText(ar[1], currX,currY, ecgPaint);
            }

            if(ar.length > 2){

                currY += 27;
                ecgCanvas.drawText(ar[2], currX, currY, ecgPaint);
            }

            if(ar.length > 3) {

                currY+= 27;
                ecgCanvas.drawText(ar[3], currX, currY, ecgPaint);
            }

            if( ar.length > 4 ) {

                currY+= 27;
                ecgCanvas.drawText(ar[4], currX, currY , ecgPaint);
            }

            if( ar.length > 5 ) {

                currY +=27;
                ecgCanvas.drawText(ar[5], currX, currY, ecgPaint);
            }
        }

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        currY += 27;
        ecgCanvas.drawText("(Note : Unconfirmed Report, Kindly consult with physician.)"  , currX, currY, ecgPaint);        return currY;
    }
    private float DrawpatientInfo_6x2(float currX, float currY, float paper_width,
                                      String Patid, int _HR, String _height, String _weight, boolean showval,
                                      String PatientName, String PatientAge, String PatientGender , String CreatedDate, String hospitalname, String Analysis, String deviceID) {

        String Str = Analysis;
        String one = null;
        String two = null;
        int count =0;

        for (String retval: Str.split("#Interpretation", 2)){
            if(count == 0)
            {
                one = retval;
                count++;
            }
            two = retval;
            System.out.println(retval);
        }
        Log.e("Analysisssssssssss ", "Analysis : " +one);
        Log.e("Analysisssssssssss ", "Analysis : " +two);

        assert one != null;
        one = one.replace("*Measurements:-", "");
        assert two != null;
        two = two.replace(":-", "");
        one = one.replace("(bpm)", "");
        one = one.replace("AXES", "");
        one = one.replace("(msec)", "");
        one = one.replace("(deg)", "");
        one = one.replace("Intervals", "");
        one = one.replace(":", " ");

        String strArry[]= one.split(" ");

        for (String item : strArry) {

            Log.e("str ", "" + item);
        }

        ecgPaint.setTextSize(26.0f);
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgPaint.setColor(Color.BLACK);
        currY +=  5 ;//35.0f;//15;

        if(hospitalname != null) {
            ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
            ecgCanvas.drawText("Hospital Name : ", currX, currY, ecgPaint);
            ecgPaint.setTypeface(Typeface.DEFAULT);
            ecgCanvas.drawText( hospitalname, currX + 200, currY, ecgPaint);

        }else {
            ecgCanvas.drawText("", currX, currY, ecgPaint);
        }

        currY += 23;        //previously 17; //

        ecgPaint.setTextSize(26.0f);
        float currY_fix = currY;
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Patient Details ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgCanvas.drawText("Name :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientName, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Age :     ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientAge, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Gender :   ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientGender, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        /*ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("DOB :", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText("NA", currX + 140, currY, ecgPaint);
        currY +=  27 ;*/

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Height :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(_height+" cm", currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Weight :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(_weight+" kg", currX + 140, currY, ecgPaint);

        currY = currY_fix;
        currX += 400;


        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Recording Details ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Recorded : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(CreatedDate, currX + 155, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Device   : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText("eUNO-WiFi", currX + 155, currY, ecgPaint);
        currY += 27;
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Device Id   : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(deviceID, currX + 155, currY, ecgPaint);



        currY = currY_fix;
        currX += 420;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Measurements ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("HR  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length  > 3) {
            ecgCanvas.drawText(strArry[3] + " bpm", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" bpm", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("PR Interval  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 23) {
            ecgCanvas.drawText(strArry[23] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QRS Interval : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 28) {
            ecgCanvas.drawText(strArry[28] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QT Interval  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 32){
            ecgCanvas.drawText(strArry[32] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QTc Interval : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 36){
            ecgCanvas.drawText(strArry[36] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("P,QRS,T Axis : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 8) {
            float p = Float.parseFloat(strArry[13]);
            float qrs = Float.parseFloat(strArry[8]);
            float t = Float.parseFloat(strArry[17]);
            ecgCanvas.drawText(String.format("%.1f",p) + "," + String.format("%.1f",qrs) + "," + String.format("%.1f" ,t), currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText("", currX + 170, currY, ecgPaint);

        currY = currY_fix;
        currX += 350;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Analysis  : ( Minnesota code )", currX, currY, ecgPaint);
        currY +=  27 ;
        ecgPaint.setTypeface(Typeface.DEFAULT);
        String ar[] = splitByNumber(two,75);


        if(EcgPreviewActivity.onAnalysis_status == 0)
        {
            ecgCanvas.drawText("NA", currX, currY, ecgPaint);
        }
        if(two.length() < 74 )
        {
            ecgCanvas.drawText(two, currX, currY, ecgPaint);
        }

        if(ar.length > 1) {

            ecgCanvas.drawText(ar[0], currX, currY, ecgPaint);

            if(ar.length > 1){

                currY += 27;
                ecgCanvas.drawText(ar[1], currX,currY, ecgPaint);
            }

            if(ar.length > 2){

                currY += 27;
                ecgCanvas.drawText(ar[2], currX, currY, ecgPaint);
            }

            if(ar.length > 3) {

                currY+= 27;
                ecgCanvas.drawText(ar[3], currX, currY, ecgPaint);
            }

            if( ar.length > 4 ) {

                currY+= 27;
                ecgCanvas.drawText(ar[4], currX, currY , ecgPaint);
            }

            if( ar.length > 5 ) {

                currY +=27;
                ecgCanvas.drawText(ar[5], currX, currY, ecgPaint);
            }
        }

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        currY += 27;
        ecgCanvas.drawText("(Note : Unconfirmed Report, Kindly consult with physician.)"  , currX, currY, ecgPaint);        return currY;
    }

    private static String[] splitByNumber(String text, int number) {

        int inLength = text.length();
        int arLength = inLength / number;
        int left=inLength%number;
        if(left>0){++arLength;}
        String ar[] = new String[arLength];
        String tempText=text;
        for (int x = 0; x < arLength; ++x) {

            if(tempText.length()>number){
                ar[x]=tempText.substring(0, number);
                tempText=tempText.substring(number);
            }else{
                ar[x]=tempText;
            }

        }

        return ar;
    }

    private float DrawpatientInfo_3x4(float currX, float currY, float paper_width,
                                      String Patid, int _HR, String _height, String _weight, boolean showval,
                                      String PatientName, String PatientAge, String PatientGender , String CreatedDate,
                                      String hospitalname, String Analysis, String deviceID) {

        String Str = Analysis;

        String one = null;
        String two = null;
        int count =0;

        for (String retval: Str.split("#Interpretation", 2)){
            if(count == 0)
            {
                one = retval;
                count++;
            }
            two = retval;
            System.out.println(retval);
        }
        Log.e("Analysisssssssssss ", "Analysis : " +one);
        Log.e("Analysisssssssssss ", "Analysis : " +two);

        assert one != null;
        one = one.replace("*Measurements:-", "");
        assert two != null;
        two = two.replace(":-", "");
        one = one.replace("(bpm)", "");
        one = one.replace("AXES", "");
        one = one.replace("(msec)", "");
        one = one.replace("(deg)", "");
        one = one.replace("Intervals", "");
        one = one.replace(":", " ");

        String strArry[]= one.split(" ");

        for (String item : strArry) {

            Log.e("str ", "" + item);
        }


        // currY  += 5;   // 28 // -30
        ecgPaint.setTextSize(26.0f);
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgPaint.setColor(Color.BLACK);
        currY +=  5 ;//35.0f;//15;

        if(hospitalname != null){
            ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
            ecgCanvas.drawText("Hospital Name : ", currX, currY, ecgPaint);
            ecgPaint.setTypeface(Typeface.DEFAULT);
            ecgCanvas.drawText(hospitalname, currX + 200, currY, ecgPaint);

        }else {
            ecgCanvas.drawText("", currX, currY, ecgPaint);
        }

        currY += 35;//23;  // 28

        ecgPaint.setTextSize(26.0f);

        float currY_fix = currY;
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Patient Details ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgCanvas.drawText("Name :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientName, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Age :     ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientAge, currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Gender :   ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(PatientGender, currX + 140, currY, ecgPaint);
        currY +=  27 ;

       /* ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("DOB :", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText("NA", currX + 140, currY, ecgPaint);
        currY +=  27 ;*/

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Height :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(_height+" cm", currX + 140, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Weight :    ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(_weight+" kg", currX + 140, currY, ecgPaint);

        currY = currY_fix;
        currX += 400;


        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Recording Details ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Recorded : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(CreatedDate, currX + 155, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Device   : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText("eUNO-WiFi", currX + 155, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Device Id  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        ecgCanvas.drawText(deviceID, currX + 155, currY, ecgPaint);


        currY = currY_fix;
        currX += 420;


        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Measurements ", currX, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("HR  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length  > 3) {
            ecgCanvas.drawText(strArry[3] + " bpm", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" bpm", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("PR Interval  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 23) {
            ecgCanvas.drawText(strArry[23] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);
        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QRS Interval : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 28) {
            ecgCanvas.drawText(strArry[28] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QT Interval  : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 32){
            ecgCanvas.drawText(strArry[32] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("QTc Interval : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 36){
            ecgCanvas.drawText(strArry[36] + " ms", currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText(" ms", currX + 170, currY, ecgPaint);

        currY +=  27 ;


        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("P,QRS,T Axis : ", currX, currY, ecgPaint);
        ecgPaint.setTypeface(Typeface.DEFAULT);
        if(strArry.length > 8) {

            float p = Float.parseFloat(strArry[13]);
            float qrs = Float.parseFloat(strArry[8]);
            float t = Float.parseFloat(strArry[17]);
            ecgCanvas.drawText(String.format("%.1f",p) + "," + String.format("%.1f",qrs) + "," + String.format("%.1f" ,t), currX + 170, currY, ecgPaint);
        }else
            ecgCanvas.drawText("", currX + 170, currY, ecgPaint);

        currY = currY_fix;
        currX += 350;

        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
        ecgCanvas.drawText("Analysis  : ( Minnesota code )", currX, currY, ecgPaint);
        currY +=  27 ;
        ecgPaint.setTypeface(Typeface.DEFAULT);
        String ar[] = splitByNumber(two,75);

        if(EcgPreviewActivity.onAnalysis_status == 0)
        {
            ecgCanvas.drawText("NA", currX, currY, ecgPaint);
        }
        if(two.length() < 74)
        {
            ecgCanvas.drawText(two, currX, currY, ecgPaint);
        }

        if(ar.length > 1) {

            ecgCanvas.drawText(ar[0], currX, currY, ecgPaint);

            if(ar.length > 1){

                currY += 27;
                ecgCanvas.drawText(ar[1], currX,currY, ecgPaint);
            }

            if(ar.length > 2){

                currY += 27;
                ecgCanvas.drawText(ar[2], currX, currY, ecgPaint);
            }

            if(ar.length > 3) {

                currY+= 27;
                ecgCanvas.drawText(ar[3], currX, currY, ecgPaint);
            }

            if( ar.length > 4 ) {

                currY+= 27;
                ecgCanvas.drawText(ar[4], currX, currY , ecgPaint);
            }

            if( ar.length > 5 ) {

                currY +=27;
                ecgCanvas.drawText(ar[5], currX, currY, ecgPaint);
            }

        }
        ecgPaint.setTypeface(Typeface.DEFAULT_BOLD);
       currY += 27;
        ecgCanvas.drawText("(Note : Unconfirmed Report, Kindly consult with physician.)"  , currX, currY, ecgPaint);

        return currY;
    }


    public void plotECGGrid(float curx, float cury, float LeadWidth,
                            float LeadHeight) {

        float nMinX = curx,
                nMinY = cury,
                nMaxX = LeadWidth + curx,
                nMaxY = LeadHeight + cury;

        // Draw Grids

        if(EcgPreviewActivity.offgrid_value == 0) {

            if(EcgPreviewActivity.grid_scale_value == 0){

                DrawGrid(nMinX, nMinY, nMaxX + 105.0f, nMaxY );

            }else{

                DrawGrid(nMinX, nMinY, nMaxX + 105.0f, nMaxY + 100.0f);
            }
        }
        else{

            if(count == 1){
                // **************for 12x 1 offgrid value************************** //
                nMinY = nMinY + 1330.0f;            // 1360.0f;
                nMaxY = nMaxY  + 380.0f; // 410.0f;
                nMaxX =  nMaxX + 91.0f ;  // LeadWidth + 72; //nMaxX + 105.0f

                Draw_Off_Grid_scale_12x1(nMinX, nMinY,nMaxX , nMaxY);

                Log.e("12x1","12x1");
            }
            else if(count == 2){

                // **************for 6x2 offgrid value************************** //

                nMinY = nMinY + 1300.0f; // 1340 //1380.0f;
                nMaxY = nMaxY  + 280.0f;    // 300       //340.0f;
                nMaxX = nMaxX + 80;
                Draw_Off_Grid_scale_6x2(nMinX, nMinY,nMaxX , nMaxY);
                Log.e("6x2","6x2");


            }
            else if(count == 3) {

                // **************for 3x4 offgrid value************************** //

                // if we increase y value the scale is decreased
                //for plotting Scale managed Y points
                nMinY = nMinY + 1280;        // 1325.0f;     // 1430.0f;
                nMaxY= nMaxY  + 270;           //295.0f;       //400.0f;
                nMaxX = LeadWidth + 76; //nMaxX + 105.0f
                Draw_Off_Grid_scale(nMinX, nMinY, nMaxX + 105.0f, nMaxY);
            }
        }

    }


    private  void DrawGrid(float nMinX, float nMinY, float nMaxX, float nMaxY) {

        // Get the pixels in 5 mm
        float fGridX = DpiX * Inch_Per_mm * _mm_Per_GridLine,
                fGridY = DpiY * Inch_Per_mm * _mm_Per_GridLine;

        ecgPaint.setColor(Color.parseColor("#F5B5C5"));
        ecgPaint.setAntiAlias(true);
        for (float i = nMinX; i <= nMaxX; i += fGridX) {

            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);

        }

        // Draw Major Grid horizontal lines.
        for (float i = nMinY; i <= nMaxY; i += fGridY) {

            ecgCanvas.drawLine(nMinX, i, nMaxX, i, ecgPaint);
        }
        // Get the pixels in 1 mm
        fGridX /= 5;
        fGridY /= 5;

        DashPathEffect dashPath = new DashPathEffect(new float[] { 0.4f,
                fGridY - 0.4f }, (float) 0.4);

        ecgPaint.setPathEffect(dashPath);
        ecgPaint.setStyle(Style.STROKE);
        ecgPaint.setColor(Color.parseColor("#8A2BE2"));

        // Draw Minor Grid dotted lines.
        for (float i = nMinX; i <= nMaxX; i += fGridX) {

            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);

        }

        // Draw outer rectangle
        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setPathEffect(new PathEffect());
        ecgPaint.setStrokeWidth(3);
        ecgCanvas.drawRect(nMinX, nMinY, nMaxX, nMaxY, ecgPaint);

        // cleanup
    }
//********************************************start of offgrid******************************************************//

    private void Draw_Off_Grid_scale(float nMinX, float nMinY, float nMaxX, float nMaxY) {

        // Get the pixels in 5 mm
        float fGridX, fGridY = DpiY * Inch_Per_mm
                * _mm_Per_GridLine;

        fGridX = 41.6f;

        // for set offgrid set color to white
        ecgPaint.setColor(Color.BLACK);

        // Draw Major Grid vertical lines.
        for (float i = nMinX; i <= nMaxX; i += fGridX) {

            // if (j % nAlternate == 0) {



            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);

            //i += 0.055;

            // }

        }


        // Get the pixels in 1 mm
        fGridX /= 5;
        fGridY /= 5;
        ecgPaint.setStrokeWidth(1.5f);

        DashPathEffect dashPath = new DashPathEffect(new float[] { 0.4f,
                fGridY - 0.4f }, (float) 0.4);

        // ecgPaint.setPathEffect(dashPath);
        ecgPaint.setStyle(Style.STROKE);
        ecgPaint.setColor(Color.parseColor("#2a2a2a"));

        // Draw Minor Grid dotted lines.
        nMinY = nMinY + 25.0f;  // for: minor grid should be small than najor grid so defined

        for (float i = nMinX; i <= nMaxX; i += fGridX) {

            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);

        }

        // Draw outer rectangle
        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setPathEffect(new PathEffect());
        ecgPaint.setStrokeWidth(3);

        // cleanup
    }

    private void Draw_Off_Grid_scale_12x1(float nMinX, float nMinY, float nMaxX, float nMaxY)
    {



        // ecgPaint.setStrokeWidth(2.0f);
        // Get the pixels in 5 mm
        float fGridX, fGridY = DpiY * Inch_Per_mm
                * _mm_Per_GridLine;


        // for set offgrid set color to white
        ecgPaint.setColor(Color.BLACK);

        // Draw Major Grid vertical lines.
        fGridX = 41.6f;     // chaned value for 25 lines of grid
        for (float i = nMinX; i <= nMaxX; i += fGridX) {

            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);

        }

        // Get the pixels in 1 mm
        fGridX /= 5;
        fGridY /= 5;
        ecgPaint.setStrokeWidth(1.5f);

        DashPathEffect dashPath = new DashPathEffect(new float[] { 0.4f,
                fGridY - 0.4f }, (float) 0.4);

        // ecgPaint.setPathEffect(dashPath);
        ecgPaint.setStyle(Style.STROKE);
        ecgPaint.setColor(Color.parseColor("#2a2a2a"));

        // Draw Minor Grid dotted lines.

        nMinY = nMinY + 25.0f;  // for: minor grid should be small than najor grid so defined


        for (float i = nMinX; i <= nMaxX; i += fGridX) {


            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);

        }

        // Draw outer rectangle
        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setPathEffect(new PathEffect());
        ecgPaint.setStrokeWidth(3);

        // cleanup
    }

    private void Draw_Off_Grid_scale_6x2(float nMinX, float nMinY, float nMaxX, float nMaxY)
    {


        ecgPaint.setAntiAlias(true);
        // ecgPaint.setStrokeWidth(2.0f);
        // Get the pixels in 5 mm
        float fGridX, fGridY = DpiY * Inch_Per_mm
                * _mm_Per_GridLine;


        // for set offgrid set color to white

        ecgPaint.setColor(Color.BLACK);

        // ecgPaint.setStroke(new BasicStroke(0.5f, BasicStroke.CAP_ROUND,
        // BasicStroke.JOIN_ROUND, 10, MajGrid_dashPattern, 0));

        // Draw Major Grid vertical lines.
        fGridX = 41.6f;     // chaned value for 25 lines of grid
        for (float i = nMinX; i <= nMaxX; i += fGridX) {

            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);
        }


        // Get the pixels in 1 mm
        fGridX /= 5;
        fGridY /= 5;
        ecgPaint.setStrokeWidth(1.5f);

        DashPathEffect dashPath = new DashPathEffect(new float[] { 0.4f,
                fGridY - 0.4f }, (float) 0.4);

        ecgPaint.setStyle(Style.STROKE);
        ecgPaint.setColor(Color.parseColor("#2a2a2a"));

        // Draw Minor Grid dotted lines.
        nMinY = nMinY + 25.0f;  // for: minor grid should be small than najor grid so defined


        for (float i = nMinX; i <= nMaxX; i += fGridX) {


            ecgCanvas.drawLine(i, nMinY, i, nMaxY, ecgPaint);
        }

        // Draw outer rectangle
        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setPathEffect(new PathEffect());
        ecgPaint.setStrokeWidth(3);

        // cleanup
        dashPath = null;
    }


    //********************************************End of offgrid**********************************************************//

    public void DrawLead(float x, float y, String leadSymbol, int[][] data,
                         float LeadWidth, float LeadHeight, int index, float lenght) {

        // Calculate the vertical offset
        float m_offset = (y + (LeadHeight * 0.5f));

        // Calculate the X axis incrementalValue
        float val = mm_Per_s * (DpiX / 25f)
                / SamplingRate;

        val += 0.015f; // added by manisha // PRE VALUE 0.002, 0.00125,0.001 ,0.0015, 0.0025,.05

        float p_ecg_y, prev_x, cur_x;

        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setDither(true);
        ecgPaint.setStrokeWidth(2.0f);  //2.5
        ecgPaint.setStyle(Style.FILL);
        // Draw the Lead Name
        ecgCanvas.drawText(leadSymbol, x + 10, y + 120, ecgPaint);   // previously y+20

        // Calculate Pixels per Vertical uVolts and Gain value
        float fPixel_Per_uV = mm_Per_mV * DpiX * Inch_Per_mm * 0.001f;

        float gainval = 10.0f * fPixel_Per_uV;

        Log.e("Gain value ", "gainvalue" + gainval);


        if(devicetype == 5) {

            switch (EcgPreviewActivity.gainvalue) {
                case 0:
                    gainval *= 1.5f;
                    break;
                case 1:
                    gainval *= 3.5f;
                    break;
                case 2:
                    gainval *= 5.0f;
                    break;
                case 3:
                    gainval *= 6.25f;
                    break;
            }
        }
        else
        {
            switch (EcgPreviewActivity.gainvalue) {
                case 0:
                    gainval *= 0.5f;
                    break;
                case 1:
                    gainval *= 1.0f;
                    break;
                case 2:
                    gainval *= 1.5f;
                    break;
                case 3:
                    gainval *= 2.0f;
                    break;
            }
        }



        cur_x = (x + val);

        // Calculate first data Value
        float ecg_y = (m_offset - data[0][index] * gainval);
        p_ecg_y = ecg_y;
        prev_x = cur_x;

        // Draw the Waveform
        for (int i = Offset; i < lenght - 1; i++) {
            ecg_y = (m_offset - data[i][index] * gainval);

            ecgCanvas.drawLine(prev_x, p_ecg_y, cur_x, ecg_y, ecgPaint);
            p_ecg_y = ecg_y;
            prev_x = cur_x;
            cur_x += val;
            Offset++;
        }
        Log.i("Sangeeta",
                "Lead I : " + leadSymbol + " Offset : "
                        + String.valueOf(Offset) + " Lenght : "
                        + String.valueOf(lenght));

        ecgCanvas.drawLine(prev_x, ecg_y - 5, prev_x, ecg_y - (50 * gainval), // previously (200 * gainval)
                ecgPaint);
        ecgCanvas.drawLine(prev_x, ecg_y + 5, prev_x, ecg_y + (40 * gainval),    // previously (200 * gainval)
                ecgPaint);

    }


    public void DrawLead_for_6x2(float x, float y, String leadSymbol, int[][] data,
                                 float LeadWidth, float LeadHeight, int index, float lenght){

        count = 2;

        // Calculate the vertical offset
        float m_offset = (y + (LeadHeight * 0.5f));

        // Calculate the X axis incrementalValue
        float val = mm_Per_s * (DpiX / 25f)
                / SamplingRate;

        val += 0.015f; // for accuracy

        float p_ecg_y, prev_x, cur_x;

        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setDither(true);
        ecgPaint.setStrokeWidth(1.5f);   //
        ecgPaint.setStyle(Style.FILL);
        // Draw the Lead Name
        ecgCanvas.drawText(leadSymbol, x + 10, y + 120, ecgPaint);   // previously y+20

        // Calculate Pixels per Vertical uVolts and Gain value
        float fPixel_Per_uV = mm_Per_mV * DpiX * Inch_Per_mm * 0.001f;

        float gainval = 10.0f * fPixel_Per_uV;

        Log.e("Gain value ", "gainvalue" + gainval);


        if(devicetype == 5) {

            switch (EcgPreviewActivity.gainvalue) {
                case 0:
                    gainval *= 1.5f;
                    break;
                case 1:
                    gainval *= 3.5f;
                    break;
                case 2:
                    gainval *= 5.0f;
                    break;
                case 3:
                    gainval *= 6.25f;
                    break;
            }
        }
        else
        {
            switch (EcgPreviewActivity.gainvalue) {
                case 0:
                    gainval *= 0.5f;
                    break;
                case 1:
                    gainval *= 1.0f;
                    break;
                case 2:
                    gainval *= 1.5f;
                    break;
                case 3:
                    gainval *= 2.0f;
                    break;
            }
        }



        cur_x = (x + val);

        // Calculate first data Value
        float ecg_y = (m_offset - data[0][index] * gainval);
        p_ecg_y = ecg_y;
        prev_x = cur_x;

        // Draw the Waveform
        for (int i = Offset; i < lenght; i++) {
            ecg_y = (m_offset - data[i][index] * gainval);

            ecgCanvas.drawLine(prev_x, p_ecg_y, cur_x, ecg_y, ecgPaint);
            p_ecg_y = ecg_y;
            prev_x = cur_x;
            cur_x += val;
            Offset++;
        }
        Log.i("Sangeeta",
                "Lead I : " + leadSymbol + " Offset : "
                        + String.valueOf(Offset) + " Lenght : "
                        + String.valueOf(lenght));

        ecgCanvas.drawLine(prev_x, ecg_y - 5, prev_x, ecg_y - (50 * gainval),// 140  // previously (200 * gainval)
                ecgPaint);
        ecgCanvas.drawLine(prev_x, ecg_y + 5, prev_x, ecg_y + (40 * gainval), // 60   // previously (200 * gainval)
                ecgPaint);

    }

    public void DrawPulse(float currX, float currY, float LongBox,
                          float LeadHeight) {

        // Calculate the vertical offset
        float m_offset = (currY + (LeadHeight * 0.5f));

        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setStrokeWidth(1.0f);

        // Calculate Pixels per Vertical uVolts and Gain value
        float fPixel_Per_uV = mm_Per_mV * DpiX * Inch_Per_mm * 0.001f;

        float gainval = gain[gain_val] * fPixel_Per_uV;  // commented by me
        gainval = gainval / 2;
        float ecg_y = (m_offset - 200 * gainval);

        // //Calculate the pixels in 1 mm
        float fGridX = (DpiX * Inch_Per_mm * _mm_Per_GridLine) / 5;

        // Draw the Pulse
        ecgCanvas.drawLine(currX, m_offset, currX + (2 * fGridX), m_offset,
                ecgPaint);

        ecgCanvas.drawLine(currX + (2 * fGridX), m_offset,
                currX + (2 * fGridX), ecg_y, ecgPaint);

        ecgCanvas.drawLine(currX + (2 * fGridX), ecg_y, currX + (4 * fGridX),
                ecg_y, ecgPaint);

        ecgCanvas.drawLine(currX + (4 * fGridX), ecg_y, currX + (4 * fGridX),
                m_offset, ecgPaint);

        ecgCanvas.drawLine(currX + (4 * fGridX), m_offset,
                currX + (6 * fGridX), m_offset, ecgPaint);

    }


    //****************************************Create 6x2 image****************************************************************//

    public Bitmap CreateImage_6x2_print(int[][] data, int sampling_rate,
                                        String filename, Context _context, String Patid, int _HR, int _sao,
                                        String _BP, boolean showval, int setgain, int _DeviceType,
                                        String PatientName, String PatientAge, String PatientGender , String CreatedDate, String hospitalName,
                                        String LongLead, String Analysis, String comments , String interpretation , String height, String weight, String deviceID) {
        devicetype = _DeviceType;
        gain_val = EcgPreviewActivity.gainvalue;
        SamplingRate = sampling_rate;
        int _width = 2338;
        int _height = 1654; // 1654;
        DpiX = _width / 11.69f;
        DpiY = _height / 8.27f;
        ecgBmp = Bitmap.createBitmap(_width, _height, Config.RGB_565);
        ecgCanvas = new Canvas(ecgBmp);
        ecgCanvas.drawColor(Color.WHITE);
        String two = null;
        float currX = 200.0f;
        float currY = 70.0f;  // previously 30

        // Draw Heading and Patient Details
        //	currY = DrawHeading(_context, currX, currY);

        if(count != 2)
        {
            count =2;
        }else
            count =2;

        currY = DrawpatientInfo_6x2(currX, currY, _width, Patid, _HR, height,weight,
                showval, PatientName, PatientAge, PatientGender, CreatedDate, hospitalName,Analysis,deviceID);

       // currY += 20;
        // Calculate the Height and width of the Container in which each lead of
        // 3 sec is to be plotted
        LeadWidth = (DpiX * Inch_Per_mm) * 62.5f  ; // for drawing 18 blocks each of 0.5mm

        LeadHeight = (DpiX * Inch_Per_mm) * 36.00f;// // previous value is 35 changed  by manisha //for drawing 6 blocks each of
        // 0.5mm Calculate the width of the Container in lead of 8 sec i.e long lead is to be plotted
        float LongBox = DpiX * Inch_Per_mm * 250;


        LongBox += 20;   // added by manisha  16th feb 2016
        LeadWidth += 37f;  // width  for one lead in 6x2

        // Draw the Grid
        // if we decrease Y value grid size increased
        if(!Analysis.isEmpty() )
        {
            currY += 20;
            plotECGGrid(currX, currY, LongBox, LeadHeight * 4);

        }else{
            currY += 120;     //previously 95//
            plotECGGrid(currX, currY, LongBox, LeadHeight * 4);

        }
        // Draw Gain Pulse
        currY = LeadHeight - 145.0f;
/*
        DrawPulse(currX, currY, LongBox, ((DpiX * Inch_Per_mm) * 52f) - 130.0f); */  // 52

        currY  += 150;

        DrawLead_for_6x2(currX, currY, "I", data, LeadWidth,
                ((DpiX * Inch_Per_mm) * 52f) - 130.0f, 0, ((float) sampling_rate * 5.0f)  //52
                        + Offset);

        // Calculate Next X Postion

        currX += LeadWidth + 500.0f;


        DrawLead_for_6x2(currX, currY, "V1", data, LeadWidth,
                ((DpiX * Inch_Per_mm) * 52f) - 130.0f, 6, ((float) sampling_rate * 5.0f)
                        + Offset);

        // Draw Gain Pulse
        currX += LeadWidth + 500.0f;
        DrawPulse(currX, currY, LongBox, ((DpiX * Inch_Per_mm) * 52f) - 130.0f);

        // Calculate Next Y Postion
        currY += LeadHeight - 125.0f ;
        currX = 200.0f;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "II", data, LeadWidth, LeadHeight, 1,
                ((float) sampling_rate * 5.0f) + Offset);


        //calculate Next X position  // plot v2

        currX += LeadWidth + 500.0f;

        DrawLead_for_6x2(currX, currY, "V2", data, LeadWidth, LeadHeight, 7,
                ((float) sampling_rate * 5.0f) + Offset);


        // Draw Gain Pulse
        currX += LeadWidth + 500.0f;
        DrawPulse(currX, currY, LongBox, LeadHeight);


        // Calculate Next Y Postion
        currY += LeadHeight - 110.0f;
        currX = 200.0f;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "III", data, LeadWidth, LeadHeight, 2,
                ((float) sampling_rate * 5.0f) + Offset);


        //calculate Next X position  // plot v3

        currX += LeadWidth + 500.0f;

        DrawLead_for_6x2(currX, currY, "V3", data, LeadWidth, LeadHeight, 8,
                ((float) sampling_rate * 5.0f) + Offset);



        // Draw Gain Pulse
        currX += LeadWidth + 500.0f;
        DrawPulse(currX, currY, LongBox, LeadHeight);


        // Calculate Next Y Postion
        currY += LeadHeight - 110.0f;
        currX = 200.0f;
        Offset = 0;



        // previously LH = (DpiX * Inch_Per_mm) * 52f;
        DrawLead_for_6x2(currX, currY, "AVR", data, LeadWidth, LeadHeight, 3, ((float) sampling_rate * 5.0f)
                + Offset);


        //calculate Next X position  // plot v4

        currX += LeadWidth + 500.0f;

        DrawLead_for_6x2(currX, currY, "V4", data, LeadWidth,
                LeadHeight, 9, ((float) sampling_rate * 5.0f)
                        + Offset);

        // Draw Gain Pulse
        currX += LeadWidth + 500.0f;
        DrawPulse(currX, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        currY += LeadHeight - 135.0f;
        currX = 200.0f;
        Offset = 0;

        float curr_y_value = currY;



        DrawLead_for_6x2(currX, currY, "AVL", data, LeadWidth, LeadHeight, 4,
                ((float) sampling_rate * 5.0f) + Offset);



        //calculate Next X position  // plot v5

        currX += LeadWidth + 500.0f;
        //  currY = curr_y_value + 60.0f;

        DrawLead_for_6x2(currX, currY, "V5", data, LeadWidth, LeadHeight, 10,
                ((float) sampling_rate * 5.0f) + Offset);

        // Draw Gain Pulse

        currX += LeadWidth + 500.0f;
        DrawPulse(currX, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        currY += LeadHeight - 130.0f;

        currX = 200.0f;
        //currX += LeadWidth;
        Offset = 0;



        DrawLead_for_6x2(currX, currY, "AVF", data, LeadWidth, LeadHeight, 5,
                ((float) sampling_rate * 5.0f) + Offset);




        //calculate Next X position  // plot v6

        currX += LeadWidth + 500.0f;

        DrawLead_for_6x2(currX, currY, "V6", data, LeadWidth, LeadHeight, 11,
                ((float) sampling_rate * 5.0f) + Offset);

        // cal gain
        currX += LeadWidth + 500.0f;
        DrawPulse(currX, currY, LongBox, LeadHeight );  //(LeadHeight- 130.0f)

        // Calculate Next Y Postion
        currY += LeadHeight - 130.0f;
        currX = 200.0f;
        //currX += LeadWidth;
        Offset = 0;

        // ***************************Draw Long Lead II********************************************//

       /*   DrawLead_for_6x2(currX, currY, "II", data, LongBox, LeadHeight, 1,
                  ((float) sampling_rate * 10.0f) + Offset);*/

        Draw_LongLead(currX, currY,LongLead, data, LongBox, LeadHeight, 1,
                ((float) sampling_rate * 10.0f) + Offset);

        // Draw Gain Pulse

        float x_val = LongBox + 250.0f;

        DrawPulse(x_val, currY, LongBox, LeadHeight);

        // ***************************End of Draw Long Lead II********************************************//

        currY -= 50;   // if we did - the shifted up and + shifted down

        ecgCanvas.drawText("Comments : "+comments, currX, currY + LeadHeight + 45.0f, ecgPaint);
        ecgCanvas.drawText("Interpretation : "+interpretation, currX, currY + LeadHeight + 70.0f,ecgPaint);


        currY = _height - 50;


        Rect bound = new Rect(0, 0, 0, 0);
        String sign = "Signature : "+_BP;
        ecgPaint.getTextBounds(sign, 0, sign.length(), bound);
        ecgCanvas.drawText(sign, _width - 260 - bound.width(), currY - 20,ecgPaint);

        ecgCanvas.drawLine(currX, currY, _width - 100, currY, ecgPaint);

        Rect bounds = new Rect(0, 0, 0, 0);
        String genby = "(C) METSL 2016";
        ecgPaint.getTextBounds(genby, 0, genby.length(), bounds);
        ecgCanvas.drawText(genby, _width - 100 - bounds.width(), currY + 20,
                ecgPaint);
        ecgCanvas.drawText(_context.getString(R.string.app_name), currX,
                currY + 20, ecgPaint);



        return ecgBmp;
    }

    //*****************************End of Create 6x2 image***************************************************************************//

    //************************************************************************************************************************//
    //****************************************Create 12x1 image****************************************************************//

    public Bitmap CreateImage_12x1_print(int[][] data, int sampling_rate,
                                         String filename, Context _context, String Patid, int _HR, int _sao,
                                         String _BP, boolean showval, int setgain, int _DeviceType,
                                         String PatientName, String PatientAge, String PatientGender , String CreatedDate, String hospitalName, String LongLead, String Analysis, String comments, String interpretation , String height, String weight, String deviceID) {
        devicetype = _DeviceType;
        gain_val = EcgPreviewActivity.gainvalue;
        SamplingRate = sampling_rate;
        int _width = 2338;
        int _height = 1654; // 1654;
        DpiX = _width / 11.69f;
        DpiY = _height / 8.27f;
        ecgBmp = Bitmap.createBitmap(_width, _height, Config.RGB_565);
        ecgCanvas = new Canvas(ecgBmp);
        ecgCanvas.drawColor(Color.WHITE);

        float currX = 200.0f;
        float currY = 30.0f;  // previously 70

        if(count != 1){
            count = 1;
        }
        else {
            count = 1;
        }


        // Draw Heading and Patient Details
        currY = DrawpatientInfo_12x1(currX, currY, _width, Patid, _HR, height, weight,
                showval, PatientName, PatientAge, PatientGender, CreatedDate, hospitalName,Analysis,deviceID);

        //currY += 20;
        // Calculate the Height and width of the Container in which each lead of
        // 3 sec is to be plotted
        LeadWidth = (DpiX * Inch_Per_mm) * 62.5f  ; // for drawing 18 blocks each of 0.5mm

        // for drawing 6 blocks each of 0.5mm
        LeadHeight = (DpiX * Inch_Per_mm) * 38.00f;// previous value is 35 changed  by manisha

        // Calculate the width of the Container in lead of 8 sec i.e long lead is to be plotted
        float LongBox = DpiX * Inch_Per_mm * 250;
        float LongBox_6x2 = DpiX * Inch_Per_mm * 312;

        // Draw the Grid

        if(!Analysis.isEmpty() )
        {
            currY += 10;
            plotECGGrid(currX, currY, LongBox, LeadHeight * 4);

        }else{
            currY += 110;     //previously 95//
            plotECGGrid(currX, currY, LongBox, LeadHeight * 4);

        }


        currY = LeadHeight - 195.0f;

        currY+= 80;
        DrawLead_for_6x2(currX, currY, "I", data, LongBox_6x2,
                ((DpiX * Inch_Per_mm) * 52f) - 130.0f, 0, ((float) sampling_rate * 10.0f)  //52
                        + Offset);

        // Draw Gain Pulse
        float x  =  LongBox + 270.0f;
        DrawPulse(x, currY, LongBox, ((DpiX * Inch_Per_mm) * 52f) - 130.0f);

        // Calculate Next X Postion
        currY += LeadHeight - 210.0f ;
        currX = 200.0f;
        Offset = 0;

        DrawLead_for_6x2(currX, currY, "II", data, LongBox_6x2, LeadHeight, 1,
                ((float) sampling_rate * 10.0f) + Offset);

        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        currY += LeadHeight - 210.0f;
        currX = 200.0f;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "III", data, LongBox_6x2, LeadHeight, 2,
                ((float) sampling_rate * 10.0f) + Offset);


        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);


        // Calculate Next Y Postion
        currY += LeadHeight - 210.0f;
        currX = 200.0f;
        Offset = 0;



        // previously LH = (DpiX * Inch_Per_mm) * 52f;
        DrawLead_for_6x2(currX, currY, "AVR", data, LongBox_6x2, LeadHeight, 3, ((float) sampling_rate * 10.0f)
                + Offset);

        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);


        // Calculate Next Y Postion
        currY += LeadHeight - 210.0f;
        currX = 200.0f;
        Offset = 0;

        float curr_y_value = currY;



        DrawLead_for_6x2(currX, currY, "AVL", data, LongBox_6x2, LeadHeight, 4,
                ((float) sampling_rate * 10.0f) + Offset);

        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        currY += LeadHeight - 210.0f;

        currX = 200.0f;
        //currX += LeadWidth;
        Offset = 0;

        //(LeadHeight- 130.0f)

        DrawLead_for_6x2(currX, currY, "AVF", data, LongBox_6x2, LeadHeight, 5,
                ((float) sampling_rate * 10.0f) + Offset);


        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        // Calculate Next Y Postion
        currY += LeadHeight - 190.0f ;
        currX = 200.0f;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "V1", data, LongBox_6x2,
                ((DpiX * Inch_Per_mm) * 52f) - 130.0f, 6, ((float) sampling_rate * 10.0f)
                        + Offset);


        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        currY += LeadHeight - 190.0f;
        currX = 200.0f;
        Offset = 0;

        DrawLead_for_6x2(currX, currY, "V2", data, LongBox_6x2, LeadHeight, 7,
                ((float) sampling_rate * 10.0f) + Offset);

        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);


        currY += LeadHeight - 185.0f;
        currX = 200.0f;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "V3", data, LongBox_6x2, LeadHeight, 8,
                ((float) sampling_rate * 10.0f) + Offset);


        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        currY += LeadHeight - 185.0f;
        currX = 200.0f;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "V4", data, LongBox_6x2,
                LeadHeight, 9, ((float) sampling_rate * 10.0f)
                        + Offset);

        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);


        currY += LeadHeight - 185.0f;

        currX = 200.0f;
        //currX += LeadWidth;
        Offset = 0;


        DrawLead_for_6x2(currX, currY, "V5", data, LongBox_6x2, LeadHeight, 10,
                ((float) sampling_rate * 10.0f) + Offset);

        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        currY += LeadHeight - 180.0f;
        currX = 200.0f;
        //currX += LeadWidth;
        Offset = 0;




        DrawLead_for_6x2(currX, currY, "V6", data, LongBox_6x2, LeadHeight, 11 ,
                ((float) sampling_rate * 10.0f) + Offset);

        // Draw Gain Pulse
        DrawPulse(LongBox + 270.0f, currY, LongBox, LeadHeight);

        currY -= 10;
        ecgCanvas.drawText("Comments : "+comments, currX, currY + LeadHeight - 70,ecgPaint);
        ecgCanvas.drawText("Interpretation : "+interpretation, currX + 800, currY + LeadHeight - 70,ecgPaint);


        currY = _height - 50;


        Rect bound = new Rect(0, 0, 0, 0);
        String sign = "Signature : "+_BP;
        ecgPaint.getTextBounds(sign, 0, sign.length(), bound);
        ecgCanvas.drawText(sign, _width - 260 - bound.width(), currY - 20,ecgPaint);


        ecgCanvas.drawLine(currX, currY, _width - 100, currY, ecgPaint);

        Rect bounds = new Rect(0, 0, 0, 0);
        String genby = "(C) METSL 2016";
        ecgPaint.getTextBounds(genby, 0, genby.length(), bounds);
        ecgCanvas.drawText(genby, _width - 100 - bounds.width(), currY + 20,
                ecgPaint);
        ecgCanvas.drawText(_context.getString(R.string.app_name), currX,
                currY + 20, ecgPaint);



        return ecgBmp;
    }

    //*****************************End of Create 12x1 image***************************************************************************//

    //**********************************DrawLonglead***************************************************//
    public  void Draw_LongLead(float x, float y, String leadSymbol, int[][] data,
                               float LeadWidth, float LeadHeight, int index, float lenght){


        switch (leadSymbol){
            case "I" :

                index = 0;
                break;
            case "II" :

                index = 1;
                break;
            case "III" :

                index = 2;
                break;
            case "aVR" :
                index = 3;

                break;
            case "aVL" :

                index = 4;

                break;
            case "aVF" :


                index = 5;

                break;
            case "V1" :
                index = 6;
                break;
            case "V2" :

                index = 7;
                break;
            case "V3" :
                index = 8;
                break;
            case "V4" :
                index = 9;
                break;
            case "V5" :
                index = 10;
                break;
            case "V6" :
                index = 11;
                break;

        }




        // Calculate the vertical offset
        float m_offset = (y + (LeadHeight * 0.5f));

        // Calculate the X axis incrementalValue
        float val = mm_Per_s * (DpiX / 25f)
                / SamplingRate;

        val += 0.015f;   // added by me 11/feb/2016

        float p_ecg_y, prev_x, cur_x;

        ecgPaint.setColor(Color.BLACK);
        ecgPaint.setDither(true);
        ecgPaint.setStrokeWidth(2.0f); //2
        ecgPaint.setStyle(Style.FILL);
        // Draw the Lead Name
        ecgCanvas.drawText(leadSymbol, x + 10, y + 120, ecgPaint);   // previously y+20

        // Calculate Pixels per Vertical uVolts and Gain value
        float fPixel_Per_uV = mm_Per_mV * DpiX * Inch_Per_mm * 0.001f;

        float gainval = 10.0f * fPixel_Per_uV;

        Log.e("Gain value ", "gainvalue" + gainval);


        if(devicetype == 5) {

            switch (EcgPreviewActivity.gainvalue) {
                case 0:
                    gainval *= 1.5f;
                    break;
                case 1:
                    gainval *= 3.5f;
                    break;
                case 2:
                    gainval *= 5.0f;
                    break;
                case 3:
                    gainval *= 6.25f;
                    break;
            }
        }
        else
        {
            switch (EcgPreviewActivity.gainvalue) {
                case 0:
                    gainval *= 0.5f;
                    break;
                case 1:
                    gainval *= 1.0f;
                    break;
                case 2:
                    gainval *= 1.5f;
                    break;
                case 3:
                    gainval *= 2.0f;
                    break;
            }
        }



        cur_x = (x + val);

        // Calculate first data Value
        float ecg_y = (m_offset - data[0][index] * gainval);
        p_ecg_y = ecg_y;
        prev_x = cur_x;

        // Draw the Waveform
        for (int i = Offset; i < lenght; i++) {
            ecg_y = (m_offset - data[i][index] * gainval);

            ecgCanvas.drawLine(prev_x, p_ecg_y, cur_x, ecg_y, ecgPaint);
            p_ecg_y = ecg_y;
            prev_x = cur_x;
            cur_x += val;
            Offset++;
        }
        Log.i("Sangeeta",
                "Lead I : " + leadSymbol + " Offset : "
                        + String.valueOf(Offset) + " Lenght : "
                        + String.valueOf(lenght));

        ecgCanvas.drawLine(prev_x, ecg_y - 5, prev_x, ecg_y - (50 * gainval), // previously (200 * gainval)
                ecgPaint);
        ecgCanvas.drawLine(prev_x, ecg_y + 5, prev_x, ecg_y + (40 * gainval),    // previously (200 * gainval)
                ecgPaint);

    }
    //************************************End of LongLead************************************************//
}
